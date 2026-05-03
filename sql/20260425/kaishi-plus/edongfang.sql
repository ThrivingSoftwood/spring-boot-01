DROP TABLE IF EXISTS `edongfang`.`edongfang_order_stub`;
CREATE TABLE `edongfang`.`edongfang_order_stub`
(
    `id`                       BIGINT      NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `e_order_id`               VARCHAR(50) NOT NULL COMMENT 'E采平台订单编号(业务核心关联键)',

    -- 核心状态快照（用于比对状态是否发生变化）
    `last_submit_state`        INT              DEFAULT NULL COMMENT '上一次记录的订单处理状态(-1:取消, 0:未确认, 1:已确认)',
    `last_status`              INT              DEFAULT NULL COMMENT '上一次记录的订单状态(0:新建, 5:发货, -2:取消, 1:签收等)',
    `last_receive_time`        TIMESTAMP   NULL DEFAULT NULL COMMENT '上一次记录的物流签收时间',
    `shipped_flag`             INT              DEFAULT 0 COMMENT '发货状态(0:未发货, 1:已发货)',

    -- 通知发送凭据（标记各阶段通知是否已发送，防止重复推送）
    `preorder_notified`        TINYINT(1)       DEFAULT 0 COMMENT '新预购订单消息是否已发送(0:未发, 1:已发)',
    `consult_result_notified`  TINYINT(1)       DEFAULT 0 COMMENT '磋商结果(确认/取消)通知是否已发送(0:未发, 1:已发)',
    `confirm_receipt_notified` TINYINT(1)       DEFAULT 0 COMMENT '客户确认收货通知是否已发送(0:未发, 1:已发)',

    -- 冗余摘要信息（可选，用于构建消息时减少对大表的关联查询，提高轮询效率）
    `order_price`              DECIMAL(18, 2)   DEFAULT NULL COMMENT '订单金额',
    `purchaser`                VARCHAR(50)      DEFAULT NULL COMMENT '采购人',
    `name`                     VARCHAR(60)      DEFAULT NULL COMMENT '收货人',

    -- 审计字段
    `create_time`              TIMESTAMP        DEFAULT CURRENT_TIMESTAMP COMMENT '存根创建时间',
    `update_time`              TIMESTAMP        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次同步更新时间',

    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_e_order_id` (`e_order_id`) USING BTREE,
    INDEX `idx_update_time` (`update_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='订单生命周期状态存根与通知判定凭据表';