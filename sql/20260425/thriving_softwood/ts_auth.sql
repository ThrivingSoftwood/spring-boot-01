use [ts_auth];

DROP TABLE IF EXISTS [dbo].[sys_message];

CREATE TABLE [dbo].[sys_message]
(
    [id]            BIGINT IDENTITY (1,1) PRIMARY KEY,
    [receiver_id]   BIGINT        NOT NULL,    -- 接收人 ID (关联 sys_user.id)
    [title]         NVARCHAR(100) NOT NULL,    -- 消息标题
    [content]       NVARCHAR(MAX) NOT NULL,    -- 消息主体内容 (JSON或纯文本)
    [msg_type]      TINYINT       NOT NULL,    -- 消息类型: 1-业务预警, 2-系统通知, 3-审批待办, 4-运行异常
    [biz_ref_id]    VARCHAR(100),              -- 业务关联标识 (如: 订单号 e_order_id，用于前端点击跳转)
    [read_status]   TINYINT  DEFAULT 0,        -- 阅读状态: 0-未读, 1-已读
    [read_time]     DATETIME,                  -- 阅读时间

    -- 标准审计字段
    [ext_info]      NVARCHAR(MAX),             -- 扩展字段 (预留存 JSON)
    [deleted]       INT      DEFAULT 0,        -- 逻辑删除 (0:存在, 1:删除)
    [last_modifier] VARCHAR(100),              -- 最后操作人
    [create_time]   DATETIME DEFAULT GETDATE(),-- 消息产生时间
    [update_time]   DATETIME DEFAULT GETDATE() -- 状态更新时间
);

-- 🚀 性能优化：为高频查询添加索引
-- 场景1：查询某用户的所有未读消息 (首页铃铛)
CREATE NONCLUSTERED INDEX [idx_receiver_read] ON [dbo].[sys_message] ([receiver_id], [read_status], [deleted]);
-- 场景2：基于业务关联ID防抖去重 (例如：防止同一笔订单重复发预警)
CREATE NONCLUSTERED INDEX [idx_biz_ref] ON [dbo].[sys_message] ([biz_ref_id], [msg_type]);

-- 添加表与字段注释
EXEC sp_addextendedproperty 'MS_Description', '系统消息中心表(SSE推送持久化底座)', 'SCHEMA', 'dbo', 'TABLE',
     'sys_message';
EXEC sp_addextendedproperty 'MS_Description', '接收人(关联sys_user.id)', 'SCHEMA', 'dbo', 'TABLE', 'sys_message',
     'COLUMN', 'receiver_id';
EXEC sp_addextendedproperty 'MS_Description', '1-业务预警, 2-系统通知', 'SCHEMA', 'dbo', 'TABLE', 'sys_message',
     'COLUMN', 'msg_type';
EXEC sp_addextendedproperty 'MS_Description', '业务关联标识(供前端跳转及后端防抖)', 'SCHEMA', 'dbo', 'TABLE',
     'sys_message', 'COLUMN', 'biz_ref_id';


truncate table [ts_auth].[dbo].[sys_permission];

set identity_insert [ts_auth].[dbo].[sys_permission] on;

INSERT INTO ts_auth.dbo.sys_permission (id, parent_id, permission_name, permission_code, permission_type, path,
                                        component, icon, sort_order, ext_info, deleted, last_modifier, create_time,
                                        update_time)
VALUES (1, 0, N'系统管理', null, 1, N'/system', null, N'Setting', 1, null, 0, null, N'2026-04-20 18:17:42.763',
        N'2026-04-20 18:17:42.763'),
       (2, 1, N'部门管理', N'system:dept:menu', 2, N'dept', N'modules/system/view/dept/IndexView', N'OfficeBuilding', 1,
        null, 0, null, N'2026-04-20 18:17:42.763', N'2026-04-20 18:17:42.763'),
       (3, 1, N'用户管理', N'system:user:menu', 2, N'user', N'modules/system/view/user/IndexView', N'User', 2, null, 0,
        null, N'2026-04-20 18:17:42.763', N'2026-04-20 18:17:42.763'),
       (4, 1, N'角色与授权', N'system:role:menu', 2, N'role', N'modules/system/view/role/IndexView', N'Key', 3, null, 0,
        null, N'2026-04-20 18:17:42.763', N'2026-04-20 18:17:42.763'),
       (5, 1, N'菜单与资源', N'system:permission:menu', 2, N'permission', N'modules/system/view/permission/IndexView',
        N'Menu', 4, null, 0, null, N'2026-04-20 18:17:42.763', N'2026-04-20 18:17:42.763'),
       (6, 1, N'数据规则', N'system:datarule:menu', 2, N'data-rule', N'modules/system/view/dataRule/IndexView',
        N'Filter', 5, null, 0, null, N'2026-04-20 18:17:42.763', N'2026-04-20 18:17:42.763'),
       (7, 0, N'采购管理', null, 1, N'/purchase', null, N'ShoppingCart', 2, null, 0, null, N'2026-04-20 18:17:42.763',
        N'2026-04-20 18:17:42.763'),
       (8, 7, N'采购单追踪', null, 1, N'trace', N'', N'List', 1, null, 0, null, N'2026-04-20 18:17:42.763',
        N'2026-04-20 18:17:42.763'),
       (9, 8, N'已完成采购单', N'purchase:trace:finished', 2, N'finished', N'modules/purchase/view/trace/IndexView',
        N'', 1, N'{ "queryPurchased": true }', 0, null, N'2026-04-20 18:17:42.763', N'2026-04-20 18:17:42.763'),
       (10, 8, N'其他采购单', N'purchase:trace:others', 2, N'others', N'modules/purchase/view/trace/IndexView', N'', 2,
        N'{ "queryPurchased": false }', 0, null, N'2026-04-20 18:17:42.763', N'2026-04-20 18:17:42.763'),
       (11, 8, N'置为完成(按钮)', N'purchase:trace:finish', 3, N'', N'', N'', 3, null, 0, null,
        N'2026-04-20 18:17:42.763', N'2026-04-20 18:17:42.763'),
       (12, 8, N'查看采购单价(字段)', N'purchase:price:view', 5, N'', N'', N'', 4, null, 0, null,
        N'2026-04-20 18:17:42.763', N'2026-04-20 18:17:42.763'),
       (13, 8, N'查看供应商字段', N'purchase:btype:view', 5, N'', N'', N'', 5, N'', 0, null, N'2026-04-20 18:17:42.763',
        N'2026-04-20 18:17:42.763'),
       (14, 0, N'E 采管理', N'', 1, N'/edongfang', N'', N'Sell', 3, N'', 0, null, N'2026-05-02 05:30:24.467',
        N'2026-05-02 05:30:24.467'),
       (15, 14, N'商品管理', N'', 1, N'product', N'', N'Goods', 1, N'', 0, null, N'2026-05-02 05:32:22.650',
        N'2026-05-02 05:32:22.650'),
       (16, 15, N'商品列表', N'edongfang:product', 2, N'list', N'modules/edongfang/product/view/IndexView', N'', 1, N'',
        0, null, N'2026-05-02 05:35:22.800', N'2026-05-02 05:35:22.800'),
       (17, 14, N'订单管理', N'', 1, N'order', N'', N'Tickets', 2, N'', 0, null, N'2026-05-03 05:28:45.527',
        N'2026-05-03 05:28:45.527'),
       (18, 17, N'订单列表', N'edongfang:order:list', 2, N'list', N'modules/edongfang/order/view/IndexView', N'', 1,
        N'{ "queryShipped" : false }', 0, null, N'2026-05-03 05:32:37.163', N'2026-05-03 05:32:37.163'),
       (19, 17, N'发货信息', N'edongfang:order:shipped', 2, N'shipped', N'modules/edongfang/order/view/IndexView', N'',
        2, N'{ "queryShipped" : true }', 0, null, N'2026-05-03 05:53:38.230', N'2026-05-03 05:53:38.230'),
       (20, 17, N'发货已确认信息', N'edongfang:order:logistics', 2, N'logistics',
        N'modules/edongfang/logistics/view/IndexView', N'', 3, N'', 0, null, N'2026-05-03 07:43:26.147',
        N'2026-05-03 07:43:26.147');


set identity_insert [ts_auth].[dbo].[sys_permission] off;
