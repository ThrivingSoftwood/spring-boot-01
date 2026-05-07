create table edongfang.edongfang_order_stub
(
    id                       bigint auto_increment comment '自增主键'
        primary key,
    e_order_id               varchar(50)                          not null comment 'E采平台订单编号(业务核心关联键)',
    last_submit_state        int                                  null comment '上一次记录的订单处理状态(-1:取消, 0:未确认, 1:已确认)',
    last_status              int                                  null comment '上一次记录的订单状态(0:新建, 5:发货, -2:取消, 1:签收等)',
    last_receive_time        timestamp                            null comment '上一次记录的物流签收时间',
    pending_action_status    int                                  null comment '进行中的操作指令状态(5:发货中, -2:取消中, 1:妥投中，为空代表无挂起操作)',
    shipped_flag             int        default 0                 null comment '发货状态(0:未发货, 1:已发货)',
    preorder_notified        tinyint(1) default 0                 null comment '新预购订单消息是否已发送(0:未发, 1:已发)',
    consult_result_notified  tinyint(1) default 0                 null comment '磋商结果(确认/取消)通知是否已发送(0:未发, 1:已发)',
    confirm_receipt_notified tinyint(1) default 0                 null comment '客户确认收货通知是否已发送(0:未发, 1:已发)',
    order_price              decimal(18, 2)                       null comment '订单金额',
    purchaser                varchar(50)                          null comment '采购人',
    name                     varchar(60)                          null comment '收货人',
    create_time              timestamp  default CURRENT_TIMESTAMP null comment '存根创建时间',
    update_time              timestamp  default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '最后一次同步更新时间',
    constraint uk_e_order_id
        unique (e_order_id)
)
    comment '订单生命周期状态存根与通知判定凭据表' collate = utf8mb4_general_ci;

create index idx_update_time
    on edongfang.edongfang_order_stub (update_time);

