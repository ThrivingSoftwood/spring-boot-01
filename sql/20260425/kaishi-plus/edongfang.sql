/* 本地测试库创建 edongfang 数据库，目标系统已经有对应的数据库 */
CREATE DATABASE if not exists `edongfang` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION = 'N' */;

/* 本系统本次为目标系统开发功能实际上仅需要向 edongfang 数据库添加的表 edongfang_order_stub */
DROP TABLE IF EXISTS `edongfang`.`edongfang_order_stub`;
CREATE TABLE `edongfang`.`edongfang_order_stub`
(
    `id`                        BIGINT      NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `e_order_id`                VARCHAR(50) NOT NULL COMMENT 'E采平台订单编号(业务核心关联键)',

    -- 核心状态快照（用于比对状态是否发生变化）
    `last_submit_state`         INT              DEFAULT NULL COMMENT '上一次记录的订单处理状态(-1:取消, 0:未确认, 1:已确认)',
    `last_status`               INT              DEFAULT NULL COMMENT '上一次记录的订单状态(0:新建, 5:发货, -2:取消, 1:签收等)',
    `last_receive_time`         TIMESTAMP   NULL DEFAULT NULL COMMENT '上一次记录的物流签收时间',

    -- 通知发送凭据（标记各阶段通知是否已发送，防止重复推送）
    `preorder_notified`         TINYINT(1)       DEFAULT 0 COMMENT '新预购订单消息是否已发送(0:未发, 1:已发)',
    `consult_result_notified`   TINYINT(1)       DEFAULT 0 COMMENT '磋商结果(确认/取消)通知是否已发送(0:未发, 1:已发)',
    `confirm_receipt_notified`  TINYINT(1)       DEFAULT 0 COMMENT '客户确认收货通知是否已发送(0:未发, 1:已发)',
    `logistics_signed_notified` TINYINT(1)       DEFAULT 0 COMMENT '平台物流签收通知是否已发送(0:未发, 1:已发)',

    -- 冗余摘要信息（可选，用于构建消息时减少对大表的关联查询，提高轮询效率）
    `order_amount`              DECIMAL(18, 2)   DEFAULT NULL COMMENT '订单金额',
    `purchaser`                 VARCHAR(50)      DEFAULT NULL COMMENT '采购人',
    `receiver_name`             VARCHAR(60)      DEFAULT NULL COMMENT '收货人',

    -- 审计字段
    `create_time`               TIMESTAMP        DEFAULT CURRENT_TIMESTAMP COMMENT '存根创建时间',
    `update_time`               TIMESTAMP        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次同步更新时间',

    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_e_order_id` (`e_order_id`) USING BTREE,
    INDEX `idx_update_time` (`update_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='订单生命周期状态存根与通知判定凭据表';

/* 下面的表都是远端 edongfang 数据库上存在的表，此处仅作参考资料 */
drop table if exists `edongfang`.edongfang_invoice_order;
create table `edongfang`.edongfang_invoice_order
(
    pk          varchar(36)              not null comment '主键'
        primary key,
    parent      varchar(36)              not null comment '申请表id',
    e_order_id  varchar(36)              not null comment '订单id',
    urls        varchar(255)             null comment '商品数量',
    create_by   varchar(36) charset utf8 null comment '创建人',
    create_time timestamp                null comment '创建时间',
    update_by   varchar(36) charset utf8 null comment '更新人',
    update_time timestamp                null comment '更新时间',
    deleted     int default 0            null comment '是否删除',
    tenant_id   varchar(36)              null comment '租户id',
    cu          varchar(36)              null comment '组织id'
)
    comment '发票订单关联' collate = utf8mb4_general_ci
                           row_format = DYNAMIC;

create index fk_order_item_order_pk
    on `edongfang`.edongfang_invoice_order (parent);

drop table if exists `edongfang`.edongfang_invoices;
create table `edongfang`.edongfang_invoices
(
    pk                   varchar(36)              not null comment '主键'
        primary key,
    bill_no              varchar(200)             not null comment '结算单号',
    e_order_ids          varchar(200)             not null comment '发票请求子订单号',
    mark_id              varchar(200)             not null comment '第三方申请发票的唯一id标识',
    settle_num           int                      not null comment '结算单子订单总数',
    settle_naked_price   decimal(18, 2)           not null comment '结算单不含税总金额（裸价）',
    settle_tax_price     decimal(18, 2)           not null comment '结算单总税价',
    invoice_type         int                      not null comment '发票类型',
    invoice_content      varchar(500)             not null comment '开票内容',
    invoice_expect_date  varchar(50)              not null comment '期望开票时间',
    invoice_title        varchar(200)             not null comment '发票抬头',
    invoice_address      varchar(500)             not null comment '发票地址',
    invoice_phone        varchar(100)             not null comment '发票电话',
    invoice_tax_num      varchar(100)             not null comment '税号',
    invoice_bank         varchar(100)             not null comment '发票开户行',
    invoice_bank_accout  varchar(100)             not null comment '银行账号',
    invoice_company_name varchar(100)             not null comment '收票单位',
    bill_toer            varchar(50)              not null comment '收票人',
    bill_to_contact      varchar(50)              not null comment '收票人联系方式',
    bill_to_province     varchar(50)              not null comment '收票人地址（省编码）',
    bill_to_city         varchar(50)              not null comment '收票人地址（市编码）',
    bill_to_county       varchar(50)              not null comment '收票人地址（区编码）',
    bill_to_town         varchar(50)              not null comment '收票人地址（镇编码）',
    bill_to_address      varchar(500)             not null comment '收票人全量地址',
    repayment_date       varchar(50)              null comment '预计还款时间',
    remark               varchar(200)             not null comment '备注',
    create_by            varchar(36) charset utf8 null comment '创建人',
    create_time          timestamp                null comment '创建时间',
    update_by            varchar(36) charset utf8 null comment '更新人',
    update_time          timestamp                null comment '更新时间',
    deleted              int default 0            null comment '是否删除',
    tenant_id            varchar(36)              null comment '租户id',
    cu                   varchar(36)              null comment '组织id',
    invoice_code         varchar(48)              null comment '发票代码',
    invoice_num          varchar(48)              null comment '发票号码',
    invoice_naked_amount decimal(10, 2)           null comment '发票金额（裸价）',
    invoice_tax_rate     decimal(10, 2)           null comment '发票税率',
    invoice_tax_amount   decimal(10, 2)           null comment '发票税额',
    invoice_amount       decimal(10, 2)           null comment '价税合计'
)
    comment '开票申请表' collate = utf8mb4_general_ci
                         row_format = DYNAMIC;

drop table if exists `edongfang`.edongfang_logistics;
create table `edongfang`.edongfang_logistics
(
    pk              varchar(36)              not null comment '主键'
        primary key,
    e_order_id      varchar(50)              not null comment 'E采平台订单编号',
    logistics_state int                      not null comment '订单物流状态 0：新建-1： 拒收-2 ：已取消 1：妥投完成 4： 退换货中 5 ：已出库',
    submit_state    int                      not null comment '订单处理状态-1：取消 0 ：未确认 1：已确认',
    package_id      varchar(50)              null comment '发货单id（若无拆单，为e_order_id值）',
    create_by       varchar(36) charset utf8 null comment '创建人',
    create_time     timestamp                null comment '创建时间',
    update_by       varchar(36) charset utf8 null comment '更新人',
    update_time     timestamp                null comment '更新时间',
    deleted         int default 0            null comment '是否删除',
    tenant_id       varchar(36)              null comment '租户id',
    cu              varchar(36)              null comment '组织id',
    order_price     decimal(10, 2)           null comment '订单金额',
    order_type      int                      null comment '订单类型 1：母订单 2：子订单',
    express_company varchar(64)              null comment '物流公司',
    express_no      varchar(48)              null comment '物流号',
    invoice_no      varchar(50)              null comment '发票号码',
    invoice_code    varchar(50)              null comment '发票编码',
    type            int default 0            null comment '0 订单物流 1 发票物流',
    receive_time    timestamp                null comment '签收时间',
    constraint uk_e_order_id
        unique (e_order_id)
)
    comment '订单发货信息' collate = utf8mb4_general_ci
                           row_format = DYNAMIC;

drop table if exists `edongfang`.edongfang_logistics_items;
create table `edongfang`.edongfang_logistics_items
(
    pk            varchar(36)              not null comment '主键'
        primary key,
    e_order_id    varchar(36)              not null comment '订单表外键',
    sku           varchar(255)             not null comment '商品编号',
    num           decimal(11, 2)           not null comment '商品数量',
    price         decimal(18, 2)           not null comment '含税单价',
    parent        varchar(36)              not null comment '发货的pk',
    create_by     varchar(36) charset utf8 null comment '创建人',
    create_time   timestamp                null comment '创建时间',
    update_by     varchar(36) charset utf8 null comment '更新人',
    update_time   timestamp                null comment '更新时间',
    deleted       int default 0            null comment '是否删除',
    tenant_id     varchar(36)              null comment '租户id',
    cu            varchar(36)              null comment '组织id',
    signed_count  decimal(11, 2)           null comment '签收数量',
    signed_amount decimal(11, 2)           null comment '签收金额'
)
    comment '订单发货商品表' collate = utf8mb4_general_ci
                             row_format = DYNAMIC;

create index fk_order_item_order_pk
    on `edongfang`.edongfang_logistics_items (e_order_id);

drop table if exists `edongfang`.edongfang_logistics_tracks;
create table `edongfang`.edongfang_logistics_tracks
(
    pk           varchar(36)              not null comment '主键'
        primary key,
    e_order_id   varchar(36)              not null comment '订单表外键',
    content      varchar(255)             not null comment '配送信息内容',
    operate_time timestamp                not null comment '时间',
    operator     varchar(36)              not null comment '操作人',
    parent       varchar(36)              not null comment '发货的pk',
    create_by    varchar(36) charset utf8 null comment '创建人',
    create_time  timestamp                null comment '创建时间',
    update_by    varchar(36) charset utf8 null comment '更新人',
    update_time  timestamp                null comment '更新时间',
    deleted      int default 0            null comment '是否删除',
    tenant_id    varchar(36)              null comment '租户id',
    cu           varchar(36)              null comment '组织id'
)
    comment '物流轨迹' collate = utf8mb4_general_ci
                       row_format = DYNAMIC;

create index fk_order_item_order_pk
    on `edongfang`.edongfang_logistics_tracks (e_order_id);

drop table if exists `edongfang`.edongfang_messages;
create table `edongfang`.edongfang_messages
(
    pk          varchar(36)                         not null comment '主键'
        primary key,
    type        varchar(5)                          not null comment '类型',
    result      varchar(512)                        not null comment '结果',
    create_by   varchar(36) charset utf8            null comment '创建人',
    create_time timestamp default CURRENT_TIMESTAMP null comment '创建时间',
    update_by   varchar(36) charset utf8            null comment '更新人',
    update_time timestamp                           null comment '更新时间',
    deleted     int       default 0                 not null comment '是否删除',
    tenant_id   varchar(36)                         null comment '租户id',
    cu          varchar(36)                         null comment '组织id',
    consumed    int       default 0                 not null comment '是否消费'
)
    comment '消息' collate = utf8mb4_general_ci
                   row_format = DYNAMIC;

drop table if exists `edongfang`.edongfang_order_items;
create table `edongfang`.edongfang_order_items
(
    pk                varchar(36)              not null comment '主键'
        primary key,
    parent            varchar(36)              not null comment '订单表外键',
    sku               varchar(255)             not null comment '商品编号',
    num               decimal(11, 2)           not null comment '商品数量',
    price             decimal(18, 2)           not null comment '含税单价',
    naked_price       decimal(18, 2)           not null comment '未税单价',
    tax_price         decimal(18, 2)           null comment '商品税额',
    tax_rate          decimal(18, 2)           null comment '税率',
    naked_price_total decimal(18, 2)           null comment '商品未税总额',
    tax_price_total   decimal(18, 2)           null comment '商品税额合计',
    price_total       decimal(18, 2)           null comment '商品含税总价/商品小计',
    name              varchar(255)             null comment '商品名称',
    material_name     varchar(255)             null comment '客户物料名称',
    material_code     varchar(40)              null comment '客户物料编码',
    description       varchar(255)             null comment '商品备注',
    create_by         varchar(36) charset utf8 null comment '创建人',
    create_time       timestamp                null comment '创建时间',
    update_by         varchar(36) charset utf8 null comment '更新人',
    update_time       timestamp                null comment '更新时间',
    deleted           int default 0            null comment '是否删除',
    tenant_id         varchar(36)              null comment '租户id',
    cu                varchar(36)              null comment '组织id',
    signed_count      decimal(11, 2)           null comment '签收数量',
    signed_amount     decimal(11, 2)           null comment '签收金额',
    return_count      decimal(11, 2)           null comment '退货数量',
    return_amount     decimal(11, 2)           null comment '退股金额',
    e_order_id        varchar(50)              not null comment 'E采平台订单编号'
)
    comment '订单商品表' collate = utf8mb4_general_ci
                         row_format = DYNAMIC;

create index fk_order_item_order_pk
    on `edongfang`.edongfang_order_items (parent);

drop table if exists `edongfang`.edongfang_orders;
create table `edongfang`.edongfang_orders
(
    pk                   varchar(36)              not null comment '主键'
        primary key,
    e_order_id           varchar(50)              not null comment 'E采平台订单编号',
    name                 varchar(60)              not null comment '收货人',
    province             int                      not null comment '一级地址',
    province_name        varchar(50)              not null comment '一级地址名称',
    city                 int                      not null comment '二级地址',
    city_name            varchar(50)              not null comment '二级地址名称',
    county               int                      not null comment '三级地址',
    county_name          varchar(50)              not null comment '三级地址名称',
    purchaser            varchar(50)              not null comment '采购人',
    address              varchar(510)             not null comment '详细地址',
    zip                  varchar(20)              null comment '邮编',
    phone                varchar(50)              null comment '座机号',
    mobile               varchar(50)              null comment '手机号',
    email                varchar(255)             null comment '邮箱',
    remark               varchar(255)             null comment '订单备注',
    invoice_title        varchar(255)             not null comment '发票抬头',
    invoice_type         int                      not null comment '发票类型',
    invoice_tax_num      varchar(100)             not null comment '发票税号',
    invoice_bank         varchar(100)             null comment '发票开户行',
    invoice_bank_account varchar(50)              null comment '发票银行账号',
    invoice_address      varchar(255)             null comment '发票地址',
    invoice_phone        varchar(32)              null comment '发票电话',
    payment              int default 9            null comment '支付方式',
    order_price          decimal(18, 2)           null comment '订单金额',
    freight              decimal(18, 2)           null comment '运费',
    dep_name             varchar(255)             null comment '采购单位名称',
    purchaser_phone      varchar(50)              null comment '下单人电话',
    purchaser_mobile     varchar(50)              null comment '下单人手机号',
    purchaser_email      varchar(50)              null comment '下单人邮箱',
    create_by            varchar(36) charset utf8 null comment '创建人',
    create_time          timestamp                null comment '创建时间',
    update_by            varchar(36) charset utf8 null comment '更新人',
    update_time          timestamp                null comment '更新时间',
    deleted              int default 0            null comment '是否删除',
    tenant_id            varchar(36)              null comment '租户id',
    cu                   varchar(36)              null comment '组织id',
    submit_state         int                      null comment '订单处理状态-1：取消 0 ：未确认 1：已确认',
    refund_status        int                      null comment '退换货状态 0：无退换货操作 1：退换货中 2：已完成退换货',
    status               int                      null comment '订单状态 0：新建5：发货-2：取消-1：拒收1：签收4：退换货中',
    constraint uk_e_order_id
        unique (e_order_id)
)
    comment '订单创建主表' collate = utf8mb4_general_ci
                           row_format = DYNAMIC;

drop table if exists `edongfang`.edongfang_product_images;
create table `edongfang`.edongfang_product_images
(
    pk          varchar(32)              not null
        primary key,
    sku         varchar(50)              not null comment '商品编号',
    path        varchar(255)             not null comment '图片路径',
    `order`     int                      not null comment '图片排序',
    create_by   varchar(36) charset utf8 null comment '创建人',
    create_time timestamp                null comment '创建时间',
    update_by   varchar(36) charset utf8 null comment '更新人',
    update_time timestamp                null comment '更新时间',
    deleted     int default 0            null comment '是否删除',
    tenant_id   varchar(36)              null comment '租户id',
    cu          varchar(36)              null comment '组织id'
)
    collate = utf8mb4_general_ci
    row_format = DYNAMIC;

drop table if exists `edongfang`.edongfang_product_params;
create table `edongfang`.edongfang_product_params
(
    pk          varchar(32)              not null
        primary key,
    sku         varchar(50)              not null comment '商品编码',
    name        varchar(100)             not null comment '属性名称',
    value       varchar(255)             not null comment '属性值	',
    create_by   varchar(36) charset utf8 null comment '创建人',
    create_time timestamp                null comment '创建时间',
    update_by   varchar(36) charset utf8 null comment '更新人',
    update_time timestamp                null comment '更新时间',
    deleted     int default 0            null comment '是否删除',
    tenant_id   varchar(36)              null comment '租户id',
    cu          varchar(36)              null comment '组织id'
)
    collate = utf8mb4_general_ci
    row_format = DYNAMIC;

drop table if exists `edongfang`.edongfang_product_prices;
create table `edongfang`.edongfang_product_prices
(
    pk           varchar(32)              not null
        primary key,
    sku          varchar(50)              not null comment '商品编号',
    market_price decimal(18, 4)           null comment '市场售价',
    mall_price   decimal(18, 4)           null comment '商城售价',
    price        decimal(18, 4)           null comment '协议优惠价',
    tax_rate     decimal(10, 4)           null comment '商品税率',
    naked_price  decimal(18, 4)           null comment '商品裸价',
    tax_amount   decimal(18, 4)           null comment '发票税额',
    create_by    varchar(36) charset utf8 null comment '创建人',
    create_time  timestamp                null comment '创建时间',
    update_by    varchar(36) charset utf8 null comment '更新人',
    update_time  timestamp                null comment '更新时间',
    deleted      int default 0            null comment '是否删除',
    tenant_id    varchar(36)              null comment '租户id',
    cu           varchar(36)              null comment '组织id'
)
    comment '商品价格表' collate = utf8mb4_general_ci
                         row_format = DYNAMIC;

create index idx_sku
    on `edongfang`.edongfang_product_prices (sku);

drop table if exists `edongfang`.edongfang_product_stocks;
create table `edongfang`.edongfang_product_stocks
(
    pk          varchar(36)              not null comment '主键'
        primary key,
    sku         varchar(50)              null comment '商品编号',
    num         int                      null comment '商品库存',
    area        varchar(255)             not null comment '地址',
    `desc`      varchar(255)             not null comment '描述（有货、缺货）',
    create_by   varchar(36) charset utf8 null comment '创建人',
    create_time timestamp                null comment '创建时间',
    update_by   varchar(36) charset utf8 null comment '更新人',
    update_time timestamp                null comment '更新时间',
    deleted     int default 0            null comment '是否删除',
    tenant_id   varchar(36)              null comment '租户id',
    cu          varchar(36)              null comment '组织id'
)
    comment '获取商品库存接口返回数据' collate = utf8mb4_general_ci
                                       row_format = DYNAMIC;

create index IDX_PRODUCT_STOCKS_SKU
    on `edongfang`.edongfang_product_stocks (sku);

create index IDX_PRODUCT_STOCKS_SKU_AREA
    on `edongfang`.edongfang_product_stocks (sku, area);

drop table if exists `edongfang`.edongfang_products;
create table `edongfang`.edongfang_products
(
    pk                varchar(36)              not null comment '主键id'
        primary key,
    sku               varchar(50)              not null comment '商品编号',
    url               varchar(255)             null comment '商品url',
    model             varchar(100)             null comment '型号',
    weight            decimal(10, 2)           not null comment '重量',
    image_path        varchar(255)             not null comment '主图地址',
    state             int                      not null comment '1上架 0下架',
    brand_name        varchar(100)             not null comment '品牌',
    name              varchar(255)             not null comment '名称',
    product_area      varchar(100)             not null comment '产地',
    upc               varchar(50)              null comment '条形码',
    unit              varchar(20)              not null comment '单位',
    category          varchar(50)              not null comment '分类',
    category_name     varchar(100)             not null comment '分类名称',
    service           text                     null comment '售后服务',
    introduction      text                     not null comment '商品描述（图文, html）',
    param             text                     null comment '商品属性（html）',
    ware              text                     not null comment '包装清单',
    tax_rate          decimal(5, 2)            null comment '商品税率',
    tax_category_code varchar(50)              null comment '税收分类编码',
    search_keyword    varchar(255)             not null comment '搜索关键词',
    sale_actives      tinyint default 0        null comment '是否促销',
    create_by         varchar(36) charset utf8 null comment '创建人',
    create_time       timestamp                null comment '创建时间',
    update_by         varchar(36) charset utf8 null comment '更新人',
    update_time       timestamp                null comment '更新时间',
    deleted           int     default 0        null comment '是否删除',
    tenant_id         varchar(36)              null comment '租户id',
    cu                varchar(36)              null comment '组织id',
    constraint IDX_PRODUCT_SKU
        unique (sku)
)
    collate = utf8mb4_general_ci
    row_format = DYNAMIC;

drop table if exists `edongfang`.edongfang_refund_items;
create table `edongfang`.edongfang_refund_items
(
    pk            varchar(36)              not null comment '主键'
        primary key,
    parent        varchar(36)              not null comment '订单表外键',
    sku           varchar(255)             not null comment '商品编号',
    num           decimal(11, 2)           not null comment '商品数量',
    price         decimal(18, 2)           not null comment '含税单价',
    create_by     varchar(36) charset utf8 null comment '创建人',
    create_time   timestamp                null comment '创建时间',
    update_by     varchar(36) charset utf8 null comment '更新人',
    update_time   timestamp                null comment '更新时间',
    deleted       int default 0            null comment '是否删除',
    tenant_id     varchar(36)              null comment '租户id',
    cu            varchar(36)              null comment '组织id',
    signed_count  decimal(11, 2)           null comment '签收数量',
    signed_amount decimal(11, 2)           null comment '签收金额',
    return_count  decimal(11, 2)           null comment '退货数量',
    return_amount decimal(11, 2)           null comment '退股金额',
    e_order_id    varchar(50)              not null comment 'E采平台订单编号'
)
    comment '订单商品表' collate = utf8mb4_general_ci
                         row_format = DYNAMIC;

create index fk_order_item_order_pk
    on `edongfang`.edongfang_refund_items (parent);

drop table if exists `edongfang`.edongfang_refunds;
create table `edongfang`.edongfang_refunds
(
    pk            varchar(36)              not null comment '主键'
        primary key,
    apply_code    varchar(200)             not null comment '退换货申请编号',
    package_id    varchar(100)             not null comment '退货对应发货单号',
    apply_type    varchar(2)               not null comment '退换货申请类型',
    apply_time    varchar(50)              not null comment '退换货申请时间',
    apply_reason  varchar(200)             not null comment '退换货原因',
    pickup_way    varchar(10)              not null comment '上门取件、第三方物流',
    address       varchar(300)             not null comment '地址',
    apply_name    varchar(50)              not null comment '退换货联系人',
    apply_mobile  varchar(50)              not null comment '退换货联系手机',
    province_code varchar(11)              not null comment '退货人省份编码',
    city_code     varchar(11)              not null comment '退货人城市编码',
    county_code   varchar(11)              not null comment '退货人区县编码',
    full_address  varchar(300)             not null comment '退货人详细地址',
    create_by     varchar(36) charset utf8 null comment '创建人',
    create_time   timestamp                null comment '创建时间',
    update_by     varchar(36) charset utf8 null comment '更新人',
    update_time   timestamp                null comment '更新时间',
    deleted       int default 0            null comment '是否删除',
    tenant_id     varchar(36)              null comment '租户id',
    cu            varchar(36)              null comment '组织id',
    operator      varchar(48)              null comment '操作人'
)
    comment '退换货申请主表' collate = utf8mb4_general_ci
                             row_format = DYNAMIC;

