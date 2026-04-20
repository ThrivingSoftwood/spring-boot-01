-- create database [kaishi-plus];

use [kaishi-plus];

drop table if exists [kaishi-plus].[dbo].[purchase_manual_finish];

create table dbo.purchase_manual_finish
(
    id            bigint identity
        primary key,
    vch_type      int      default 34 not null,
    vch_code      numeric(10)         not null,
    dly_order     numeric             not null,
    last_modifier varchar(100),
    ext_info      nvarchar(max),
    deleted       int      default 0  not null,
    create_time   datetime default getdate(),
    update_time   datetime default getdate()
);

exec sp_addextendedproperty 'MS_Description', N'自增主键', 'SCHEMA', 'dbo', 'TABLE', 'purchase_manual_finish', 'COLUMN',
     'id';

exec sp_addextendedproperty 'MS_Description', N'单据类型', 'SCHEMA', 'dbo', 'TABLE', 'purchase_manual_finish', 'COLUMN',
     'vch_type';

exec sp_addextendedproperty 'MS_Description', N'单据编号', 'SCHEMA', 'dbo', 'TABLE', 'purchase_manual_finish', 'COLUMN',
     'vch_code';

exec sp_addextendedproperty 'MS_Description', N'详单编号', 'SCHEMA', 'dbo', 'TABLE', 'purchase_manual_finish', 'COLUMN',
     'dly_order';

exec sp_addextendedproperty 'MS_Description', N'最近修改人', 'SCHEMA', 'dbo', 'TABLE', 'purchase_manual_finish',
     'COLUMN', 'last_modifier';

exec sp_addextendedproperty 'MS_Description', N'扩展信息', 'SCHEMA', 'dbo', 'TABLE', 'purchase_manual_finish', 'COLUMN',
     'ext_info';

exec sp_addextendedproperty 'MS_Description', N'已逻辑删除 0:否 1:是', 'SCHEMA', 'dbo', 'TABLE',
     'purchase_manual_finish', 'COLUMN', 'deleted';

exec sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', 'dbo', 'TABLE', 'purchase_manual_finish', 'COLUMN',
     'create_time';

exec sp_addextendedproperty 'MS_Description', N'修改时间', 'SCHEMA', 'dbo', 'TABLE', 'purchase_manual_finish', 'COLUMN',
     'update_time';



drop table if exists [dbo].[department_association_info];
create table [dbo].department_association_info
(
    id                    bigint identity ( 1, 1 ) primary key,
    auth_department_id    bigint       default 0  not null,
    ori_department_typeid nvarchar(25) default '' not null,
    ext_info              nvarchar(max),
    deleted               int          default 0,
    last_modifier         varchar(100),
    create_time           datetime     default getdate(),
    update_time           datetime     default getdate()
);
EXEC sp_addextendedproperty 'MS_Description', '部门关联信息表([ts_auth] 库到 [凯诗防护2026] 库)', 'SCHEMA', 'dbo',
     'TABLE', 'department_association_info';



drop table if exists [dbo].employee_association_info;
create table [dbo].employee_association_info
(
    id                    bigint identity primary key,
    login_account         varchar(100)             not null,
    ori_fullname          nvarchar(100) default '' not null,
    employee_typeid       nvarchar(25)  default '',
    employee_user_code    nvarchar(26)  default '',
    login_employee_typeid nvarchar(25)  default '',
    login_user_code       nvarchar(50)  default '' not null,
    ext_info              nvarchar(max),
    deleted               int           default 0  not null,
    last_modifier         varchar(100),
    create_time           datetime      default getdate(),
    update_time           datetime      default getdate()
);
EXEC sp_addextendedproperty 'MS_Description', '用户关联信息表([ts_auth] 库到 [凯诗防护2026] 库)', 'SCHEMA', 'dbo',
     'TABLE', 'employee_association_info';