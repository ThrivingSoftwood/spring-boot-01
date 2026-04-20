-- create database [ts_auth];
use [ts_auth];
/* =======================================================
   1. 部门组织表 (sys_dept)
   作用：行级数据权限的核心基石，定义数据归属。
   ======================================================= */
drop table if exists [ts_auth].[dbo].[sys_dept];
create table [ts_auth].[dbo].sys_dept
(
    id            bigint identity (1,1) primary key,
    parent_id     bigint        default 0  not null, -- 父部门ID (0代表顶级)
    ancestors     nvarchar(200) default '' not null, -- 祖级列表 (如: 0,1,5，用于递归范围查询)
    dept_name     nvarchar(50)             not null, -- 部门名称
    sort_order    int           default 0,           -- 显示顺序
    status        tinyint       default 1,           -- 部门状态 (1:正常, 0:停用)
    ext_info      nvarchar(max),
    deleted       int           default 0,           -- 逻辑删除 (0:存在, 1:删除)
    last_modifier varchar(100),
    create_time   datetime      default getdate(),   -- 创建时间
    update_time   datetime      default getdate()    -- 更新时间
);
exec sp_addextendedproperty 'MS_Description', '部门组织表', 'SCHEMA', 'dbo', 'TABLE', 'sys_dept';
exec sp_addextendedproperty 'MS_Description', '祖级列表(快速定位下属部门)', 'SCHEMA', 'dbo', 'TABLE', 'sys_dept',
     'COLUMN', 'ancestors';


/* =======================================================
   2. 系统用户表 (sys_user)
   作用：集成 permission_version 以支持无 Redis 环境下的静默刷新。
   ======================================================= */
drop table if exists [ts_auth].[dbo].sys_user;
create table [ts_auth].[dbo].sys_user
(
    id                 bigint identity (1,1) primary key,
    login_account      varchar(100)       not null,
    username           nvarchar(100),
    password           varchar(100)       not null,
    dept_id            bigint,
    permission_version varchar(20),
    status             tinyint  default 1,
    login_time         varchar(30),
    last_password      varchar(100),
    ext_info           nvarchar(max),
    deleted            int      default 0 not null,
    last_modifier      varchar(100),
    create_time        datetime default getdate(),
    update_time        datetime default getdate()
);

exec sp_addextendedproperty 'MS_Description', N'自增主键', 'SCHEMA', 'dbo', 'TABLE', 'sys_user', 'COLUMN', 'id';

exec sp_addextendedproperty 'MS_Description', N'登录账户', 'SCHEMA', 'dbo', 'TABLE', 'sys_user', 'COLUMN',
     'login_account';

exec sp_addextendedproperty 'MS_Description', N'用户名', 'SCHEMA', 'dbo', 'TABLE', 'sys_user', 'COLUMN', 'username';

exec sp_addextendedproperty 'MS_Description', N'当前密码', 'SCHEMA', 'dbo', 'TABLE', 'sys_user', 'COLUMN', 'password';

exec sp_addextendedproperty 'MS_Description', N'归属部门', 'SCHEMA', 'dbo', 'TABLE', 'sys_user', 'COLUMN', 'dept_id';

exec sp_addextendedproperty 'MS_Description', N'权限版本号,日期去掉所有分隔符后拼接成的字符串', 'SCHEMA', 'dbo',
     'TABLE', 'sys_user', 'COLUMN', 'permission_version';

exec sp_addextendedproperty 'MS_Description', N'状态, 1:启用 0:禁用', 'SCHEMA', 'dbo', 'TABLE', 'sys_user', 'COLUMN',
     'status';

exec sp_addextendedproperty 'MS_Description', N'最近登录时间', 'SCHEMA', 'dbo', 'TABLE', 'sys_user', 'COLUMN',
     'login_time';

exec sp_addextendedproperty 'MS_Description', N'上次密码', 'SCHEMA', 'dbo', 'TABLE', 'sys_user', 'COLUMN',
     'last_password';

exec sp_addextendedproperty 'MS_Description', N'扩展信息', 'SCHEMA', 'dbo', 'TABLE', 'sys_user', 'COLUMN', 'ext_info';

exec sp_addextendedproperty 'MS_Description', N'已逻辑删除', 'SCHEMA', 'dbo', 'TABLE', 'sys_user', 'COLUMN', 'deleted';

exec sp_addextendedproperty 'MS_Description', N'最后修改人', 'SCHEMA', 'dbo', 'TABLE', 'sys_user', 'COLUMN',
     'last_modifier';

exec sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', 'dbo', 'TABLE', 'sys_user', 'COLUMN',
     'create_time';

exec sp_addextendedproperty 'MS_Description', N'修改时间', 'SCHEMA', 'dbo', 'TABLE', 'sys_user', 'COLUMN',
     'update_time';

exec sp_addextendedproperty 'MS_Description', '系统用户表', 'SCHEMA', 'dbo', 'TABLE', 'sys_user';

insert into [ts_auth].dbo.sys_user (login_account, password, username, last_password, login_time, status,
                                    last_modifier, ext_info, deleted, dept_id,
                                    permission_version)
values (N'kaishi', N'$2a$10$jM5sKMRhytzUif3NHn3vbOCVZC6ehJIkVNyEQNuD.8OVjSA9VpWn2', N'凯诗管理员', null,
        N'2026-04-13 12:14:53', 1, null, null, 0, null, null);


/* =======================================================
   3. 角色表 (sys_role)
   作用：纯粹的身份聚合载体，解耦具体权限规则。
   ======================================================= */
drop table if exists [ts_auth].[dbo].sys_role;
create table [ts_auth].[dbo].sys_role
(
    id            bigint identity (1,1) primary key,
    role_code     VARCHAR(50)        not null UNIQUE, -- 角色编码 (如: ADMIN, BUYER)
    role_name     nvarchar(50)       not null,        -- 角色名称 (如: 管理员, 采购员)
    sort_order    int      default 0,                 -- 显示顺序
    status        tinyint  default 1 not null,        -- 角色状态 (1:正常, 0:停用)
    ext_info      nvarchar(max),
    deleted       int      default 0,                 -- 逻辑删除 (0:存在, 1:删除)
    last_modifier varchar(100),
    create_time   datetime default getdate(),         -- 创建时间
    update_time   datetime default getdate()          -- 更新时间
);
exec sp_addextendedproperty 'MS_Description', '系统角色表', 'SCHEMA', 'dbo', 'TABLE', 'sys_role';


set identity_insert ts_auth.dbo.sys_role on;
INSERT INTO ts_auth.dbo.sys_role (id, role_code, role_name, sort_order, status, ext_info, deleted)
VALUES (1, N'SUPER_ADMIN', N'超级管理员', 1, 1, null, 0);
set identity_insert ts_auth.dbo.sys_role off;


/* =======================================================
   4. 资源权限表 (sys_permission)
   作用：全量存储功能性权限，解决"列级字段隐藏"的存储问题。
   ======================================================= */
drop table if exists [ts_auth].[dbo].sys_permission;
create table [ts_auth].[dbo].sys_permission
(
    id              bigint identity (1,1) primary key,
    parent_id       bigint        default 0 not null, -- 父权限ID
    permission_name nvarchar(50)            not null, -- 权限名称 (如: 查看采购单价)
    permission_code VARCHAR(100),                     -- 权限标识 (如: purchase:price:view)
    permission_type tinyint                 not null, -- 权限类型 (1:目录, 2:菜单, 3:按钮, 4:API, 5:敏感列字段)
    path            nvarchar(200) default '',         -- 前端路由地址/API路径
    component       nvarchar(200) default '',         -- 前端组件路径
    icon            VARCHAR(50)   default '',         -- 图标
    sort_order      int           default 0,          -- 排序
    ext_info        nvarchar(max),
    deleted         int           default 0,          -- 逻辑删除 (0:存在, 1:删除)
    last_modifier   varchar(100),
    create_time     datetime      default getdate(),  -- 创建时间
    update_time     datetime      default getdate()   -- 更新时间
);
exec sp_addextendedproperty 'MS_Description', '资源权限表(含菜单按钮及敏感字段)', 'SCHEMA', 'dbo', 'TABLE',
     'sys_permission';
exec sp_addextendedproperty 'MS_Description', '权限类型(1目录 2菜单 3按钮 4接口 5敏感列)', 'SCHEMA', 'dbo', 'TABLE',
     'sys_permission', 'COLUMN', 'permission_type';

-- 初始数据
set identity_insert ts_auth.dbo.sys_permission on;
INSERT INTO ts_auth.dbo.sys_permission (id, parent_id, permission_name, permission_code, permission_type, path,
                                        component, icon, sort_order, ext_info, deleted)
VALUES (1, 0, N'系统管理', null, 1, N'/system', null, N'Setting', 1, null, 0),
       (2, 1, N'部门管理', N'system:dept:menu', 2, N'dept', N'system/dept/IndexView', N'OfficeBuilding', 1, null, 0),
       (3, 1, N'用户管理', N'system:user:menu', 2, N'user', N'system/user/IndexView', N'User', 2, null, 0),
       (4, 1, N'角色与授权', N'system:role:menu', 2, N'role', N'system/role/IndexView', N'Key', 3, null, 0),
       (5, 1, N'菜单与资源', N'system:permission:menu', 2, N'permission', N'system/permission/IndexView', N'Menu',
        4, null, 0),
       (6, 1, N'数据规则', N'system:datarule:menu', 2, N'data-rule', N'system/dataRule/IndexView', N'Filter', 5,
        null, 0),
       (7, 0, N'采购管理', null, 1, N'/purchase', null, N'ShoppingCart', 2, null, 0),
       (8, 7, N'采购单追踪', null, 1, N'trace', N'', N'List', 1, null, 0),
       (9, 8, N'已完成采购单', N'purchase:trace:finished', 2, N'finished', N'purchase/trace/IndexView', N'', 1,
        N'{ "queryPurchased": true }', 0),
       (10, 8, N'其他采购单', N'purchase:trace:others', 2, N'others', N'purchase/trace/IndexView', N'', 2,
        N'{ "queryPurchased": false }', 0),
       (11, 8, N'置为完成(按钮)', N'purchase:trace:finish', 3, N'', N'', N'', 3, null, 0),
       (12, 8, N'查看采购单价(字段)', N'purchase:price:view', 5, N'', N'', N'', 4, null, 0),
       (13, 8, N'查看供应商字段', N'purchase:btype:view', 5, N'', N'', N'', 5, N'', 0);
set identity_insert ts_auth.dbo.sys_permission off;

/* =======================================================
   5. 数据规则引擎表 (sys_data_rule)
   作用：存储解耦后的行级过滤规则，实现可视化构建器的后端映射。
   ======================================================= */
drop table if exists [ts_auth].[dbo].sys_data_rule;
create table [ts_auth].[dbo].sys_data_rule
(
    id              bigint identity (1,1) primary key,
    target_resource VARCHAR(100) not null,      -- 拦截资源标识 (对应VO类名或MapperID，如: DlyBuy)
    rule_name       nvarchar(50) not null,      -- 规则描述名称
    scope_type      tinyint      not null,      -- 范围类型 (1:全部, 2:本人, 3:本部门, 4:本部门及以下, 5:高级自定义)
    custom_sql_json VARCHAR(MAX),               -- 存储可视化构建器生成的 UI 逻辑 JSON
    ext_info        nvarchar(max),
    deleted         int      default 0,         -- 逻辑删除 (0:存在, 1:删除)
    last_modifier   varchar(100),
    create_time     datetime default getdate(), -- 创建时间
    update_time     datetime default getdate()  -- 更新时间
);
exec sp_addextendedproperty 'MS_Description', '行级数据规则表', 'SCHEMA', 'dbo', 'TABLE', 'sys_data_rule';
exec sp_addextendedproperty 'MS_Description', '可视化构建器逻辑存储', 'SCHEMA', 'dbo', 'TABLE', 'sys_data_rule',
     'COLUMN', 'custom_sql_json';


/* =======================================================
   6. 用户-角色 关联表 (sys_user_role)
   ======================================================= */
drop table if exists [ts_auth].[dbo].sys_user_role;
create table [ts_auth].[dbo].sys_user_role
(
    id            bigint identity (1,1) primary key,
    user_id       bigint not null,
    role_id       bigint not null,
    ext_info      nvarchar(max),
    deleted       int      default 0,         -- 逻辑删除 (0:存在, 1:删除)
    last_modifier varchar(100),
    create_time   datetime default getdate(), -- 创建时间
    update_time   datetime default getdate(), -- 更新时间
);
exec sp_addextendedproperty 'MS_Description', '用户与角色关联表', 'SCHEMA', 'dbo', 'TABLE', 'sys_user_role';
set identity_insert ts_auth.dbo.sys_user_role on;
INSERT INTO ts_auth.dbo.sys_user_role (id, user_id, role_id, ext_info, deleted)
VALUES (1, 1, 1, null, 0);
set identity_insert ts_auth.dbo.sys_user_role off;


/* =======================================================
   7. 角色-权限 关联表 (sys_role_permission)
   作用：将角色与功能菜单、按钮以及敏感列(type=5)进行绑定。
   ======================================================= */
drop table if exists [ts_auth].[dbo].sys_role_permission;
create table [ts_auth].[dbo].sys_role_permission
(
    id            bigint identity (1,1) primary key,
    role_id       bigint not null,
    permission_id bigint not null,
    ext_info      nvarchar(max),
    deleted       int      default 0,         -- 逻辑删除 (0:存在, 1:删除)
    last_modifier varchar(100),
    create_time   datetime default getdate(), -- 创建时间
    update_time   datetime default getdate(), -- 更新时间
);
exec sp_addextendedproperty 'MS_Description', '角色与资源权限关联表', 'SCHEMA', 'dbo', 'TABLE', 'sys_role_permission';


/* =======================================================
   8. 角色-数据规则 关联表 (sys_role_data_rule)
   作用：将角色与特定的数据行过滤逻辑绑定。
   ======================================================= */
drop table if exists [ts_auth].[dbo].sys_role_data_rule;
create table [ts_auth].[dbo].sys_role_data_rule
(
    id            bigint identity (1,1) primary key,
    role_id       bigint not null,
    rule_id       bigint not null,
    ext_info      nvarchar(max),
    deleted       int      default 0,         -- 逻辑删除 (0:存在, 1:删除)
    last_modifier varchar(100),
    create_time   datetime default getdate(), -- 创建时间
    update_time   datetime default getdate(), -- 更新时间
);
exec sp_addextendedproperty 'MS_Description', '角色与行级数据规则关联表', 'SCHEMA', 'dbo', 'TABLE',
     'sys_role_data_rule';