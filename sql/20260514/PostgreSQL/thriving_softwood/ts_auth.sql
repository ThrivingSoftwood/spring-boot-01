-- 创建模式（等同于 SQL Server 中的数据库 + dbo 模式）
-- CREATE database ts_auth;

create or replace function update_update_time() returns trigger
    language plpgsql
as
$$
begin
    new.update_time = current_timestamp;
    return new;
end;
$$;

alter function update_update_time() owner to "postgres";

-- 创建模式（等同于 SQL Server 中的数据库 + dbo 模式）
-- CREATE database ts_auth;


/* =======================================================
   1. 部门组织表 (sys_dept)
   作用：行级数据权限的核心基石，定义数据归属。
   ======================================================= */
DROP TABLE IF EXISTS public.sys_dept CASCADE;
CREATE TABLE public.sys_dept
(
    id            BIGSERIAL PRIMARY KEY,                  -- identity(1,1) -> BIGSERIAL
    parent_id     BIGINT       DEFAULT 0  NOT NULL,       -- 父部门ID (0代表顶级)
    ancestors     VARCHAR(200) DEFAULT '' NOT NULL,       -- 祖级列表 (nvarchar -> varchar)
    dept_name     VARCHAR(50)             NOT NULL,       -- 部门名称
    sort_order    INT          DEFAULT 0,
    status        SMALLINT     DEFAULT 1,                 -- 部门状态 (1:正常, 0:停用, tinyint -> SMALLINT)
    ext_info      TEXT,                                   -- nvarchar(max) -> TEXT
    deleted       INT          DEFAULT 0,                 -- 逻辑删除 (0:存在, 1:删除)
    last_modifier VARCHAR(100),
    create_time   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP, -- datetime + getdate() -> TIMESTAMP
    update_time   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE public.sys_dept IS '部门组织表';
COMMENT ON COLUMN public.sys_dept.ancestors IS '祖级列表(快速定位下属部门)';


ALTER TABLE public.sys_dept
    OWNER TO postgres;

CREATE TRIGGER trg_update_sys_dept_ut
    BEFORE UPDATE
    ON public.sys_dept
    FOR EACH ROW
EXECUTE PROCEDURE update_update_time();

TRUNCATE TABLE public.sys_dept RESTART IDENTITY;

/* =======================================================
   2. 系统用户表 (sys_user)
   作用：集成 permission_version 以支持无 Redis 环境下的静默刷新。
   ======================================================= */
DROP TABLE IF EXISTS public.sys_user CASCADE;
CREATE TABLE public.sys_user
(
    id                 BIGSERIAL PRIMARY KEY,
    login_account      VARCHAR(100)        NOT NULL,
    username           VARCHAR(100),
    password           VARCHAR(100)        NOT NULL,
    dept_id            BIGINT,
    permission_version VARCHAR(20),
    status             SMALLINT  DEFAULT 1,
    login_time         VARCHAR(30), -- 原类型保留为 varchar
    last_password      VARCHAR(100),
    ext_info           TEXT,
    deleted            INT       DEFAULT 0 NOT NULL,
    last_modifier      VARCHAR(100),
    create_time        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time        TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE public.sys_user IS '系统用户表';
COMMENT ON COLUMN public.sys_user.id IS '自增主键';
COMMENT ON COLUMN public.sys_user.login_account IS '登录账户';
COMMENT ON COLUMN public.sys_user.username IS '用户名';
COMMENT ON COLUMN public.sys_user.password IS '当前密码';
COMMENT ON COLUMN public.sys_user.dept_id IS '归属部门';
COMMENT ON COLUMN public.sys_user.permission_version IS '权限版本号,日期去掉所有分隔符后拼接成的字符串';
COMMENT ON COLUMN public.sys_user.status IS '状态, 1:启用 0:禁用';
COMMENT ON COLUMN public.sys_user.login_time IS '最近登录时间';
COMMENT ON COLUMN public.sys_user.last_password IS '上次密码';
COMMENT ON COLUMN public.sys_user.ext_info IS '扩展信息';
COMMENT ON COLUMN public.sys_user.deleted IS '已逻辑删除';
COMMENT ON COLUMN public.sys_user.last_modifier IS '最后修改人';
COMMENT ON COLUMN public.sys_user.create_time IS '创建时间';
COMMENT ON COLUMN public.sys_user.update_time IS '修改时间';

ALTER TABLE public.sys_user
    OWNER TO postgres;

CREATE TRIGGER trg_update_sys_user_ut
    BEFORE UPDATE
    ON public.sys_user
    FOR EACH ROW
EXECUTE PROCEDURE update_update_time();

TRUNCATE TABLE public.sys_user RESTART IDENTITY;

-- 初始数据：管理员用户（id 由序列自动生成）
INSERT INTO public.sys_user (login_account, password, username, last_password, login_time,
                             status, last_modifier, ext_info, deleted, dept_id, permission_version)
VALUES ('kaishi', '$2a$10$jM5sKMRhytzUif3NHn3vbOCVZC6ehJIkVNyEQNuD.8OVjSA9VpWn2',
        '凯诗管理员', NULL, '2026-04-13 12:14:53', 1, NULL, NULL, 0, NULL, NULL);
SELECT setval('public.sys_user_id_seq', 1);

/* =======================================================
   3. 角色表 (sys_role)
   作用：纯粹的身份聚合载体，解耦具体权限规则。
   ======================================================= */
DROP TABLE IF EXISTS public.sys_role CASCADE;
CREATE TABLE public.sys_role
(
    id            BIGSERIAL PRIMARY KEY,
    role_code     VARCHAR(50)         NOT NULL UNIQUE, -- 角色编码 (如: ADMIN, BUYER)
    role_name     VARCHAR(50)         NOT NULL,        -- 角色名称
    sort_order    INT       DEFAULT 0,
    status        SMALLINT  DEFAULT 1 NOT NULL,
    ext_info      TEXT,
    deleted       INT       DEFAULT 0,
    last_modifier VARCHAR(100),
    create_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE public.sys_role IS '系统角色表';

ALTER TABLE public.sys_role
    OWNER TO postgres;

CREATE TRIGGER trg_update_sys_role_ut
    BEFORE UPDATE
    ON public.sys_role
    FOR EACH ROW
EXECUTE PROCEDURE update_update_time();

TRUNCATE TABLE public.sys_role RESTART IDENTITY;

-- 插入种子数据并同步序列
INSERT INTO public.sys_role (id, role_code, role_name, sort_order, status, ext_info, deleted)
VALUES (1, 'SUPER_ADMIN', '超级管理员', 1, 1, NULL, 0);
SELECT setval('public.sys_role_id_seq', 1);

/* =======================================================
   4. 资源权限表 (sys_permission)
   作用：全量存储功能性权限，解决"列级字段隐藏"的存储问题。
   ======================================================= */
DROP TABLE IF EXISTS public.sys_permission CASCADE;
CREATE TABLE public.sys_permission
(
    id              BIGSERIAL PRIMARY KEY,
    parent_id       BIGINT       DEFAULT 0 NOT NULL,
    permission_name VARCHAR(50)            NOT NULL,
    permission_code VARCHAR(100),
    permission_type SMALLINT               NOT NULL, -- 权限类型 (1:目录,2:菜单,3:按钮,4:API,5:敏感列字段)
    path            VARCHAR(200) DEFAULT '',
    component       VARCHAR(200) DEFAULT '',
    icon            VARCHAR(50)  DEFAULT '',
    sort_order      INT          DEFAULT 0,
    ext_info        TEXT,
    deleted         INT          DEFAULT 0,
    last_modifier   VARCHAR(100),
    create_time     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE public.sys_permission IS '资源权限表(含菜单按钮及敏感字段)';
COMMENT ON COLUMN public.sys_permission.permission_type IS '权限类型(1目录 2菜单 3按钮 4接口 5敏感列)';

ALTER TABLE public.sys_permission
    OWNER TO postgres;

CREATE TRIGGER trg_update_sys_permission_ut
    BEFORE UPDATE
    ON public.sys_permission
    FOR EACH ROW
EXECUTE PROCEDURE update_update_time();


-- =====================================================================
-- 重新初始化资源权限表 (sys_permission) 数据
-- =====================================================================
TRUNCATE TABLE public.sys_permission RESTART IDENTITY;

INSERT INTO public.sys_permission
(id, parent_id, permission_name, permission_code, permission_type,
 path, component, icon, sort_order, ext_info, deleted,
 last_modifier, create_time, update_time)
VALUES (1, 0, '系统管理', null, 1, '/system', null, 'Setting', 1, null, 0, null,
        '2026-04-20 18:17:42.763', '2026-04-20 18:17:42.763'),
       (2, 1, '部门管理', 'system:dept:menu', 2, 'dept',
        'modules/system/view/dept/IndexView', 'OfficeBuilding', 1, null, 0, null,
        '2026-04-20 18:17:42.763', '2026-04-20 18:17:42.763'),
       (3, 1, '用户管理', 'system:user:menu', 2, 'user',
        'modules/system/view/user/IndexView', 'User', 2, null, 0, null,
        '2026-04-20 18:17:42.763', '2026-04-20 18:17:42.763'),
       (4, 1, '角色与授权', 'system:role:menu', 2, 'role',
        'modules/system/view/role/IndexView', 'Key', 3, null, 0, null,
        '2026-04-20 18:17:42.763', '2026-04-20 18:17:42.763'),
       (5, 1, '菜单与资源', 'system:permission:menu', 2, 'permission',
        'modules/system/view/permission/IndexView', 'Menu', 4, null, 0, null,
        '2026-04-20 18:17:42.763', '2026-04-20 18:17:42.763'),
       (6, 1, '数据规则', 'system:datarule:menu', 2, 'data-rule',
        'modules/system/view/dataRule/IndexView', 'Filter', 5, null, 0, null,
        '2026-04-20 18:17:42.763', '2026-04-20 18:17:42.763'),
       (7, 0, '采购管理', null, 1, '/purchase', null, 'ShoppingCart', 2, null, 0, null,
        '2026-04-20 18:17:42.763', '2026-04-20 18:17:42.763'),
       (8, 7, '采购单追踪', null, 1, 'trace', '', 'List', 1, null, 0, null,
        '2026-04-20 18:17:42.763', '2026-04-20 18:17:42.763'),
       (9, 8, '已完成采购单', 'purchase:trace:finished', 2, 'finished',
        'modules/purchase/view/trace/IndexView', '', 1, '{ "queryPurchased": true }', 0, null,
        '2026-04-20 18:17:42.763', '2026-04-20 18:17:42.763'),
       (10, 8, '其他采购单', 'purchase:trace:others', 2, 'others',
        'modules/purchase/view/trace/IndexView', '', 2, '{ "queryPurchased": false }', 0, null,
        '2026-04-20 18:17:42.763', '2026-04-20 18:17:42.763'),
       (11, 8, '置为完成(按钮)', 'purchase:trace:finish', 3, '', '', '', 3, null, 0, null,
        '2026-04-20 18:17:42.763', '2026-04-20 18:17:42.763'),
       (12, 8, '查看采购单价(字段)', 'purchase:price:view', 5, '', '', '', 4, null, 0, null,
        '2026-04-20 18:17:42.763', '2026-04-20 18:17:42.763'),
       (13, 8, '查看供应商字段', 'purchase:btype:view', 5, '', '', '', 5, '', 0, null,
        '2026-04-20 18:17:42.763', '2026-04-20 18:17:42.763'),
       (14, 0, 'E 采管理', '', 1, '/edongfang', '', 'Sell', 3, '', 0, null,
        '2026-05-02 05:30:24.467', '2026-05-02 05:30:24.467'),
       (15, 14, '商品管理', '', 1, 'product', '', 'Goods', 1, '', 0, null,
        '2026-05-02 05:32:22.650', '2026-05-02 05:32:22.650'),
       (16, 15, '商品列表', 'edongfang:product', 2, 'list',
        'modules/edongfang/product/view/IndexView', '', 1, '', 0, null,
        '2026-05-02 05:35:22.800', '2026-05-02 05:35:22.800'),
       (17, 14, '订单管理', '', 1, 'order', '', 'Tickets', 2, '', 0, null,
        '2026-05-03 05:28:45.527', '2026-05-03 05:28:45.527'),
       (18, 17, '订单列表', 'edongfang:order:list', 2, 'list',
        'modules/edongfang/order/view/IndexView', '', 1,
        '{ "queryShipped" : false }', 0, null,
        '2026-05-03 05:32:37.163', '2026-05-03 05:32:37.163'),
       (19, 17, '发货信息', 'edongfang:order:shipped', 2, 'shipped',
        'modules/edongfang/order/view/IndexView', '', 2,
        '{ "queryShipped" : true }', 0, null,
        '2026-05-03 05:53:38.230', '2026-05-03 05:53:38.230'),
       (20, 17, '发货已确认信息', 'edongfang:order:logistics', 2, 'logistics',
        'modules/edongfang/logistics/view/IndexView', '', 3, '', 0, null,
        '2026-05-03 07:43:26.147', '2026-05-03 07:43:26.147');
SELECT setval('public.sys_permission_id_seq', 20);

/* =======================================================
   5. 数据规则引擎表 (sys_data_rule)
   作用：存储解耦后的行级过滤规则，实现可视化构建器的后端映射。
   ======================================================= */
DROP TABLE IF EXISTS public.sys_data_rule CASCADE;
CREATE TABLE public.sys_data_rule
(
    id              BIGSERIAL PRIMARY KEY,
    target_resource VARCHAR(100) NOT NULL, -- 拦截资源标识
    rule_name       VARCHAR(50)  NOT NULL, -- 规则描述名称
    scope_type      SMALLINT     NOT NULL, -- 范围类型 (1:全部,2:本人,3:本部门,4:本部门及以下,5:高级自定义)
    custom_sql_json TEXT,                  -- 可视化构建器生成的 UI 逻辑 JSON (VARCHAR(MAX) -> TEXT)
    ext_info        TEXT,
    deleted         INT       DEFAULT 0,
    last_modifier   VARCHAR(100),
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE public.sys_data_rule IS '行级数据规则表';
COMMENT ON COLUMN public.sys_data_rule.custom_sql_json IS '可视化构建器逻辑存储';

ALTER TABLE public.sys_data_rule
    OWNER TO postgres;

CREATE TRIGGER trg_update_sys_data_rule_ut
    BEFORE UPDATE
    ON public.sys_data_rule
    FOR EACH ROW
EXECUTE PROCEDURE update_update_time();


TRUNCATE TABLE public.sys_data_rule RESTART IDENTITY;

/* =======================================================
   6. 用户-角色 关联表 (sys_user_role)
   ======================================================= */
DROP TABLE IF EXISTS public.sys_user_role CASCADE;
CREATE TABLE public.sys_user_role
(
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT NOT NULL,
    role_id       BIGINT NOT NULL,
    ext_info      TEXT,
    deleted       INT       DEFAULT 0,
    last_modifier VARCHAR(100),
    create_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE public.sys_user_role IS '用户与角色关联表';

ALTER TABLE public.sys_user_role
    OWNER TO postgres;

CREATE TRIGGER trg_update_sys_user_role_ut
    BEFORE UPDATE
    ON public.sys_user_role
    FOR EACH ROW
EXECUTE PROCEDURE update_update_time();


TRUNCATE TABLE public.sys_user_role RESTART IDENTITY;

-- 初始关联：用户1 -> 角色1
INSERT INTO public.sys_user_role (id, user_id, role_id, ext_info, deleted)
VALUES (1, 1, 1, NULL, 0);
SELECT setval('public.sys_user_role_id_seq', 20);

/* =======================================================
   7. 角色-权限 关联表 (sys_role_permission)
   作用：将角色与功能菜单、按钮以及敏感列(type=5)进行绑定。
   ======================================================= */
DROP TABLE IF EXISTS public.sys_role_permission CASCADE;
CREATE TABLE public.sys_role_permission
(
    id            BIGSERIAL PRIMARY KEY,
    role_id       BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    ext_info      TEXT,
    deleted       INT       DEFAULT 0,
    last_modifier VARCHAR(100),
    create_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE public.sys_role_permission IS '角色与资源权限关联表';

ALTER TABLE public.sys_role_permission
    OWNER TO postgres;

CREATE TRIGGER trg_update_sys_role_permission_ut
    BEFORE UPDATE
    ON public.sys_role_permission
    FOR EACH ROW
EXECUTE PROCEDURE update_update_time();

TRUNCATE TABLE public.sys_role_permission RESTART IDENTITY;

/* =======================================================
   8. 角色-数据规则 关联表 (sys_role_data_rule)
   作用：将角色与特定的数据行过滤逻辑绑定。
   ======================================================= */
DROP TABLE IF EXISTS public.sys_role_data_rule CASCADE;
CREATE TABLE public.sys_role_data_rule
(
    id            BIGSERIAL PRIMARY KEY,
    role_id       BIGINT NOT NULL,
    rule_id       BIGINT NOT NULL,
    ext_info      TEXT,
    deleted       INT       DEFAULT 0,
    last_modifier VARCHAR(100),
    create_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE public.sys_role_data_rule IS '角色与行级数据规则关联表';

ALTER TABLE public.sys_role_data_rule
    OWNER TO postgres;

CREATE TRIGGER trg_update_sys_role_data_rule_ut
    BEFORE UPDATE
    ON public.sys_role_data_rule
    FOR EACH ROW
EXECUTE PROCEDURE update_update_time();


TRUNCATE TABLE public.sys_role_data_rule RESTART IDENTITY;

-- =====================================================================
-- 系统消息中心表 (sys_message)
-- 作用：SSE 推送持久化底座
-- =====================================================================
DROP TABLE IF EXISTS public.sys_message CASCADE;

CREATE TABLE public.sys_message
(
    id            BIGSERIAL PRIMARY KEY,
    receiver_id   BIGINT       NOT NULL,               -- 接收人 ID (关联 sys_user.id)
    title         VARCHAR(100) NOT NULL,               -- 消息标题
    content       TEXT         NOT NULL,               -- 消息主体内容 (JSON 或纯文本)
    msg_type      SMALLINT     NOT NULL,               -- 消息类型: 1-业务预警, 2-系统通知, 3-审批待办, 4-运行异常
    biz_ref_id    VARCHAR(100),                        -- 业务关联标识 (如: 订单号 e_order_id)
    read_status   SMALLINT  DEFAULT 0,                 -- 阅读状态: 0-未读, 1-已读
    read_time     TIMESTAMP,                           -- 阅读时间

    -- 标准审计字段
    ext_info      TEXT,                                -- 扩展字段 (预留存 JSON)
    deleted       INT       DEFAULT 0,                 -- 逻辑删除 (0:存在, 1:删除)
    last_modifier VARCHAR(100),                        -- 最后操作人
    create_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 消息产生时间
    update_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- 状态更新时间
);

-- 性能优化索引
CREATE INDEX idx_receiver_read ON public.sys_message (receiver_id, read_status, deleted);
CREATE INDEX idx_biz_ref ON public.sys_message (biz_ref_id, msg_type);

-- 表与字段注释
COMMENT ON TABLE public.sys_message IS '系统消息中心表(SSE推送持久化底座)';
COMMENT ON COLUMN public.sys_message.receiver_id IS '接收人(关联sys_user.id)';
COMMENT ON COLUMN public.sys_message.msg_type IS '1-业务预警, 2-系统通知';
COMMENT ON COLUMN public.sys_message.biz_ref_id IS '业务关联标识(供前端跳转及后端防抖)';

ALTER TABLE public.sys_message
    OWNER TO postgres;

CREATE TRIGGER trg_update_sys_message_ut
    BEFORE UPDATE
    ON public.sys_message
    FOR EACH ROW
EXECUTE PROCEDURE update_update_time();


TRUNCATE TABLE public.sys_message RESTART IDENTITY;


-- ============================================================
-- 字典表 (sys_dictionary)
-- 作用：存储系统级字典数据，支持键值对配置
-- ============================================================
DROP TABLE IF EXISTS public.sys_dictionary CASCADE;
CREATE TABLE public.sys_dictionary
(
    id          SERIAL PRIMARY KEY,
    dict_type   VARCHAR(50)  NOT NULL, -- 字典类型 (如: status, unit)
    dict_key    VARCHAR(50)  NOT NULL, -- 字典键 (如: 0, 1, kg)
    dict_value  VARCHAR(200) NOT NULL, -- 字典值 (如: 启用, 禁用, 千克)
    sort_order  INT       DEFAULT 0,   -- 排序号
    status      SMALLINT  DEFAULT 1,   -- 状态 (1:启用, 0:禁用)
    remark      TEXT,                  -- 备注说明
    deleted     INT       DEFAULT 0,   -- 逻辑删除 (0:存在, 1:删除)
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ext_info    TEXT
);

COMMENT ON TABLE public.sys_dictionary IS '系统字典表';
COMMENT ON COLUMN public.sys_dictionary.id IS '自增主键';
COMMENT ON COLUMN public.sys_dictionary.dict_type IS '字典类型';
COMMENT ON COLUMN public.sys_dictionary.dict_key IS '字典键';
COMMENT ON COLUMN public.sys_dictionary.dict_value IS '字典值';
COMMENT ON COLUMN public.sys_dictionary.sort_order IS '排序号';
COMMENT ON COLUMN public.sys_dictionary.status IS '状态 (1:启用, 0:禁用)';
COMMENT ON COLUMN public.sys_dictionary.remark IS '备注说明';
COMMENT ON COLUMN public.sys_dictionary.create_time IS '创建时间';
COMMENT ON COLUMN public.sys_dictionary.update_time IS '更新时间';
COMMENT ON COLUMN public.sys_dictionary.ext_info IS '扩展信息';

CREATE UNIQUE INDEX idx_sys_dict_type_key ON public.sys_dictionary (dict_type, dict_key);

ALTER TABLE public.sys_dictionary
    OWNER TO postgres;

CREATE TRIGGER trg_update_sys_dictionary_ut
    BEFORE UPDATE
    ON public.sys_dictionary
    FOR EACH ROW
EXECUTE PROCEDURE update_update_time();


TRUNCATE TABLE public.sys_dictionary RESTART IDENTITY;

-- 采购/供应链管理目录
INSERT INTO public.sys_permission (id, parent_id, permission_name, permission_code, permission_type, path, component,
                                   icon, sort_order)
VALUES (30, 0, '供应商协同平台', null, 1, '/customer/first', null, 'Connection', 4);

-- 内部员工菜单
INSERT INTO public.sys_permission (id, parent_id, permission_name, permission_code, permission_type, path, component,
                                   icon, sort_order)
VALUES (31, 30, '入库商品管理', 'customer:first:product:menu', 2, 'product/list',
        'modules/customer/first/product/view/IndexView', 'Box', 1);

INSERT INTO public.sys_permission (id, parent_id, permission_name, permission_code, permission_type, path, component,
                                   icon, sort_order)
VALUES (32, 30, '供应商库管理', 'customer:first:supplier:menu', 2, 'supplier/list',
        'modules/customer/first/supplier/view/IndexView', 'UserFilled', 2);

-- 确保 30 节点（供应商协同平台）已存在
-- 这次我们增加针对“供应商”专属的菜单，并挂载到路由上

INSERT INTO public.sys_permission (id, parent_id, permission_name, permission_code, permission_type, path, component,
                                   icon, sort_order)
VALUES (33, 30, '商品大厅 (报价)', 'customer:first:supplier_product:menu', 2, 'supplier-product/list',
        'modules/customer/first/quotation/view/SupplierProductIndexView', 'Goods', 3);

INSERT INTO public.sys_permission (id, parent_id, permission_name, permission_code, permission_type, path, component,
                                   icon, sort_order)
VALUES (34, 30, '我的报价', 'customer:first:my_quotation:menu', 2, 'my-quotation/list',
        'modules/customer/first/quotation/view/MyQuotationIndexView', 'Money', 4);

-- 记得修正序列
SELECT setval('public.sys_permission_id_seq', 34);

-- 将报价大盘挂载到 内部员工视图下
INSERT INTO public.sys_permission (id, parent_id, permission_name, permission_code,
                                   permission_type, path, component, icon, sort_order)
VALUES (35, 30, '报价大盘监控', 'customer:first:dashboard:menu',
        2, 'quotation/dashboard', 'modules/customer/first/quotation/view/DashboardIndexView', 'DataAnalysis', 5);

-- 更新序列值，防止主键冲突
SELECT setval('public.sys_permission_id_seq', 35);