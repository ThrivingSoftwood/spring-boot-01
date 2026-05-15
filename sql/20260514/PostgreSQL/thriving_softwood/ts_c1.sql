-- create database ts_c1;

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

-- ============================================================
-- 1. 商品信息表 (product_info)
-- ============================================================
DROP TABLE IF EXISTS public.product_info CASCADE;
CREATE TABLE public.product_info
(
    id                 SERIAL PRIMARY KEY,
    product_code       VARCHAR(20)  NOT NULL, -- 商品编码
    product_name       VARCHAR(100) NOT NULL, -- 商品名称
    specification      VARCHAR(100),          -- 规格型号
    purchase_price     NUMERIC(64, 8),        -- 标准采购价(单位:元)
    sale_price         NUMERIC(64, 8),        -- 标准销售价(含税,单位:元)
    tax_rate           NUMERIC(7, 4),         -- 税率(%)
    default_supplier   TEXT,                  -- 默认供应商
    unit               TEXT,                  -- 单位
    optional_suppliers TEXT,                  -- 可选供应商
    data_status        TEXT,                  -- 数据状态
    disable_status     TEXT,                  -- 禁用状态
    material_property  TEXT,                  -- 物料属性
    base_unit          TEXT,                  -- 基本单位
    remark             TEXT,                  -- 备注
    attachment         TEXT,                  -- 附件
    deleted            INT       DEFAULT 0,   -- 逻辑删除 (0:存在, 1:删除)
    last_modifier      VARCHAR(100),
    create_time        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ext_info           TEXT
);

COMMENT ON TABLE public.product_info IS '商品信息表';
COMMENT ON COLUMN public.product_info.id IS '自增主键，唯一标识每条商品信息记录';
COMMENT ON COLUMN public.product_info.product_code IS '商品编码';
COMMENT ON COLUMN public.product_info.product_name IS '商品名称';
COMMENT ON COLUMN public.product_info.specification IS '规格型号';
COMMENT ON COLUMN public.product_info.purchase_price IS '标准采购价(单位:元)';
COMMENT ON COLUMN public.product_info.sale_price IS '标准销售价(含税,单位:元)';
COMMENT ON COLUMN public.product_info.tax_rate IS '税率(%)';
COMMENT ON COLUMN public.product_info.default_supplier IS '默认供应商';
COMMENT ON COLUMN public.product_info.unit IS '单位';
COMMENT ON COLUMN public.product_info.optional_suppliers IS '可选供应商';
COMMENT ON COLUMN public.product_info.data_status IS '数据状态';
COMMENT ON COLUMN public.product_info.disable_status IS '禁用状态';
COMMENT ON COLUMN public.product_info.material_property IS '物料属性';
COMMENT ON COLUMN public.product_info.base_unit IS '基本单位';
COMMENT ON COLUMN public.product_info.remark IS '备注';
COMMENT ON COLUMN public.product_info.attachment IS '附件';
COMMENT ON COLUMN public.product_info.create_time IS '创建时间';
COMMENT ON COLUMN public.product_info.update_time IS '更新时间';
COMMENT ON COLUMN public.product_info.ext_info IS '扩展信息';

ALTER TABLE public.product_info
    OWNER TO postgres;

CREATE TRIGGER trg_update_product_info_ut
    BEFORE UPDATE
    ON public.product_info
    FOR EACH ROW
EXECUTE PROCEDURE update_update_time();


TRUNCATE TABLE public.product_info RESTART IDENTITY;

-- ============================================================
-- 2. 供应商信息表 (supplier_info)
-- ============================================================
DROP TABLE IF EXISTS public.supplier_info CASCADE;
CREATE TABLE public.supplier_info
(
    id                SERIAL PRIMARY KEY,
    supplier_code     VARCHAR(20)  NOT NULL, -- 供应商编码
    supplier_name     VARCHAR(100) NOT NULL, -- 供应商名称
    basic_info        VARCHAR(1),            -- 从此处向下为基础信息
    region            TEXT,                  -- 地区(省-市-县)
    raw_material_name TEXT,                  -- 原材料名称
    quotation_manager TEXT,                  -- 报价负责人
    internal_contact  TEXT,                  -- 内部联系人
    purchase_contact  TEXT,                  -- 采购联系人
    contact_name      TEXT,                  -- 联系人姓名
    contact_phone     TEXT,                  -- 联系人电话
    remark            TEXT,                  -- 备注
    email             TEXT,                  -- 邮箱
    notify_method     TEXT,                  -- 通知方式(通知链接)
    attachment        TEXT,                  -- 附件
    financial_info    VARCHAR(1),            -- 从此处向下为财务信息
    vat_rate          NUMERIC(7, 4),         -- 增值税税率
    invoice_name      TEXT,                  -- 开票名称
    tax_id            TEXT,                  -- 开票税号
    bank_name         TEXT,                  -- 开户银行
    bank_account      TEXT,                  -- 银行账号
    bank_address      TEXT,                  -- 开户地址
    bank_phone        TEXT,                  -- 开户电话
    related_contact   TEXT,                  -- 关联的联系人
    deleted           INT       DEFAULT 0,   -- 逻辑删除 (0:存在, 1:删除)
    last_modifier     VARCHAR(100),
    create_time       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ext_info          TEXT
);

COMMENT ON TABLE public.supplier_info IS '供应商信息表';
COMMENT ON COLUMN public.supplier_info.id IS '自增主键，唯一标识每条供应商信息记录';
COMMENT ON COLUMN public.supplier_info.supplier_code IS '供应商编码';
COMMENT ON COLUMN public.supplier_info.supplier_name IS '供应商名称';
COMMENT ON COLUMN public.supplier_info.basic_info IS '从此处向下为基础信息';
COMMENT ON COLUMN public.supplier_info.region IS '地区(省-市-县)';
COMMENT ON COLUMN public.supplier_info.raw_material_name IS '原材料名称';
COMMENT ON COLUMN public.supplier_info.quotation_manager IS '报价负责人';
COMMENT ON COLUMN public.supplier_info.internal_contact IS '内部联系人';
COMMENT ON COLUMN public.supplier_info.purchase_contact IS '采购联系人';
COMMENT ON COLUMN public.supplier_info.contact_name IS '联系人姓名';
COMMENT ON COLUMN public.supplier_info.contact_phone IS '联系人电话';
COMMENT ON COLUMN public.supplier_info.remark IS '备注';
COMMENT ON COLUMN public.supplier_info.email IS '邮箱';
COMMENT ON COLUMN public.supplier_info.notify_method IS '通知方式(通知链接)';
COMMENT ON COLUMN public.supplier_info.attachment IS '附件';
COMMENT ON COLUMN public.supplier_info.financial_info IS '从此处向下为财务信息';
COMMENT ON COLUMN public.supplier_info.vat_rate IS '增值税税率';
COMMENT ON COLUMN public.supplier_info.invoice_name IS '开票名称';
COMMENT ON COLUMN public.supplier_info.tax_id IS '开票税号';
COMMENT ON COLUMN public.supplier_info.bank_name IS '开户银行';
COMMENT ON COLUMN public.supplier_info.bank_account IS '银行账号';
COMMENT ON COLUMN public.supplier_info.bank_address IS '开户地址';
COMMENT ON COLUMN public.supplier_info.bank_phone IS '开户电话';
COMMENT ON COLUMN public.supplier_info.related_contact IS '关联的联系人';
COMMENT ON COLUMN public.supplier_info.create_time IS '创建时间';
COMMENT ON COLUMN public.supplier_info.update_time IS '更新时间';
COMMENT ON COLUMN public.supplier_info.ext_info IS '扩展信息';

ALTER TABLE public.supplier_info
    OWNER TO postgres;

CREATE TRIGGER trg_update_supplier_info_ut
    BEFORE UPDATE
    ON public.supplier_info
    FOR EACH ROW
EXECUTE PROCEDURE update_update_time();


TRUNCATE TABLE public.supplier_info RESTART IDENTITY;


-- ============================================================
-- 3. 联系人表 (contact_info)
-- ============================================================
DROP TABLE IF EXISTS public.contact_info CASCADE;
CREATE TABLE public.contact_info
(
    id               SERIAL PRIMARY KEY,
    name             VARCHAR(200),        -- 姓名
    position         VARCHAR(200),        -- 职位
    phone            TEXT,                -- 联系电话
    is_main_contact  TEXT,                -- 主要联系人标记
    related_customer TEXT,                -- 关联的客户
    related_supplier TEXT,                -- 关联的供应商
    deleted          INT       DEFAULT 0, -- 逻辑删除 (0:存在, 1:删除)
    last_modifier    VARCHAR(100),
    create_time      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ext_info         TEXT
);

COMMENT ON TABLE public.contact_info IS '联系人表';
COMMENT ON COLUMN public.contact_info.id IS '自增主键，唯一标识每条联系人信息记录';
COMMENT ON COLUMN public.contact_info.name IS '姓名';
COMMENT ON COLUMN public.contact_info.position IS '职位';
COMMENT ON COLUMN public.contact_info.phone IS '联系电话';
COMMENT ON COLUMN public.contact_info.is_main_contact IS '主要联系人标记';
COMMENT ON COLUMN public.contact_info.related_customer IS '关联的客户';
COMMENT ON COLUMN public.contact_info.related_supplier IS '关联的供应商';
COMMENT ON COLUMN public.contact_info.create_time IS '创建时间';
COMMENT ON COLUMN public.contact_info.update_time IS '更新时间';
COMMENT ON COLUMN public.contact_info.ext_info IS '扩展信息';

ALTER TABLE public.contact_info
    OWNER TO postgres;

CREATE TRIGGER trg_update_contact_info_ut
    BEFORE UPDATE
    ON public.contact_info
    FOR EACH ROW
EXECUTE PROCEDURE update_update_time();


TRUNCATE TABLE public.contact_info RESTART IDENTITY;

-- ============================================================
-- 报价单信息表 (quotation)
-- ============================================================
DROP TABLE IF EXISTS public.quotation CASCADE;
CREATE TABLE public.quotation
(
    id                 SERIAL PRIMARY KEY,
    quotation_no       VARCHAR(30)  NOT NULL, -- 报价单号
    supplier_code      VARCHAR(20)  NOT NULL, -- 所属供应商编码
    product_info       VARCHAR(30),           -- 商品信息
    product_name       VARCHAR(100) NOT NULL, -- 商品名称
    product_code       VARCHAR(20)  NOT NULL, -- 商品编码
    supply_place       TEXT,                  -- 供货地
    available_qty      NUMERIC(64, 8),        -- 可供货数量
    quantity_unit      TEXT,                  -- 数量单位
    quotation_type     TEXT,                  -- 报价类型
    unit_price         NUMERIC(64, 8),        -- 报价单价(含税)
    tax_rate           NUMERIC(7, 4),         -- 增值税税率(%)
    remark             TEXT,                  -- 报价备注信息
    auxiliary_material TEXT,                  -- 报价辅助材料
    outdated           INT       DEFAULT 0,   -- 过期(失效)标记 (0:生效, 1:失效)
    deleted            INT       DEFAULT 0,   -- 已删除标记 (0:正常, 1:删除)
    create_time        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ext_info           TEXT
);

COMMENT ON TABLE public.quotation IS '报价单信息表';
COMMENT ON COLUMN public.quotation.id IS '自增主键，唯一标识每条报价记录';
COMMENT ON COLUMN public.quotation.quotation_no IS '报价单号';
COMMENT ON COLUMN public.quotation.supplier_code IS '所属供应商编码';
COMMENT ON COLUMN public.quotation.product_info IS '商品信息';
COMMENT ON COLUMN public.quotation.product_name IS '商品名称';
COMMENT ON COLUMN public.quotation.product_code IS '商品编码';
COMMENT ON COLUMN public.quotation.supply_place IS '供货地';
COMMENT ON COLUMN public.quotation.available_qty IS '可供货数量';
COMMENT ON COLUMN public.quotation.quantity_unit IS '数量单位';
COMMENT ON COLUMN public.quotation.quotation_type IS '报价类型';
COMMENT ON COLUMN public.quotation.unit_price IS '报价单价(含税)';
COMMENT ON COLUMN public.quotation.tax_rate IS '增值税税率(%)';
COMMENT ON COLUMN public.quotation.remark IS '报价备注信息';
COMMENT ON COLUMN public.quotation.auxiliary_material IS '报价辅助材料';
COMMENT ON COLUMN public.quotation.outdated IS '过期(失效)标记 (0:生效, 1:失效)';
COMMENT ON COLUMN public.quotation.deleted IS '已删除标记 (0:正常, 1:删除)';
COMMENT ON COLUMN public.quotation.create_time IS '创建时间';
COMMENT ON COLUMN public.quotation.update_time IS '更新时间';
COMMENT ON COLUMN public.quotation.ext_info IS '扩展信息';

ALTER TABLE public.quotation
    OWNER TO postgres;

CREATE TRIGGER trg_update_quotation_ut
    BEFORE UPDATE
    ON public.quotation
    FOR EACH ROW
EXECUTE PROCEDURE update_update_time();


TRUNCATE TABLE public.quotation RESTART IDENTITY;