# 📙 后端技术架构与功能说明文档 (Backend Documentation)

> 初始需求:将采购单数据与入库单数据进行匹配,未入库和入库异常的标记出来
---

#### 📌 修订记录 (Revision History)

| 版本号        | 修订日期       | 修订内容摘要        | 修订人              | 审核人              |
|:-----------|:-----------|:--------------|:-----------------|:-----------------|
| **V1.0.0** | 2026-04-11 | 项目初步落成，文档初始化。 | ThrivingSoftwood | ThrivingSoftwood |
|            |            |               |                  |                  |
|            |            |               |                  |                  |

---

## 1. 技术架构 (Technical Architecture)

本系统后端是一个追求性能、可观测性与代码规范的**微服务化单体架构 (Modulith)**。

* **核心底座:** **JDK 25** + **Spring Boot 4.0.5**。
* **多模块架构 (Multi-Module):**
* `common-core`: 纯净工具类、常量、全局异常定义、Result 封装、Sm4/JWT 工具。
* `common-database`: MyBatis-Plus、多数据源动态切换配置、公共字段自动填充 (`MybatisPlusMetaObjectHandler`)。
* `common-framework`: AOP 切面日志、异步线程池配置（含虚拟线程）。
* `common-observability`: 基于 OTLP 的遥测基建。
* `common-web`: Web MVC 配置、CORS、全局异常拦截器、Token 拦截器。
* `kaishi`: 核心业务模块。
* **数据库与 ORM:** **MyBatis-Plus 3.5.16** + **Dynamic Datasource**。同时连接 `ksplus` (系统库) 和 `kaishi-2026` (
  业务库，SQL Server)。
* **密码学与安全 (Cryptography):**
* **传输层:** 国密 **SM4** (CBC/PKCS5Padding) 用于前后端密码的密文传输。
* **存储层:** 数据库使用 **BCrypt** 单向哈希存储密码，自带 Salt 抵御彩虹表攻击。
* **认证层:** 使用 Hutool v7 签发基于非对称加密 **RSA256** 的 JWT Token。
* **高并发模型 (Concurrency):**
* 区分了 CPU 密集型 `@PtAsync` (平台线程池 ThreadPoolTaskExecutor) 和 I/O 密集型 `@VtAsync` (虚拟线程
  SimpleAsyncTaskExecutor + 信号量限流)。
* **生产级可观测性 (Observability):**
* 摒弃 Logback，采用 **Log4j2 的全异步模式 (RingBuffer)** 提升 10 倍 I/O 性能。
* **Trace 传递:** 自定义 `MicrometerTracingDecorator`，确保在虚拟线程或异步任务切换时，TraceID 依然能够无缝传递，并通过
  `Log4j2OtlpConfig` 直接发送日志到 OpenTelemetry Collector。

## 2. 核心功能实现 (Functional Implementation)

### 2.1 字典数据全量预热 (Dictionary Pre-loading)

* **机制:** 借助 `ApplicationRunner` (`DictionaryPreloadRunner`)，在 Spring Boot 启动完成后，立刻从数据库读取几十张字典表（Btype,
  Department, Employee 等）。
* **缓存介质:** 注入到 **Caffeine** 本地缓存中。避免了高频查询时的数据库击穿，将 O(N) 的 DB 网络开销降为 O(1) 的内存读取。业务代码使用
  `@Cacheable` 优雅调用。

### 2.2 复杂采购追踪联表与聚合 (Complex Query & Aggregation)

* **痛点解决:** ERP 系统的 `dlybuy` (采购明细) 和 `dlystock` (入库明细) 表结构极其庞大且历史包袱重。
* **SQL 层面优化 (`DlyBuyExtendMapper.xml`):**
* 使用了 SQL Server 的 `CROSS APPLY` 语法，高效计算单条采购记录对应的入库总数 (`stocked_qty`)。
* 通过 `NOT EXISTS` 排除掉已在 `purchase_manual_finish` 表中被手动完结的记录。
* 使用 `UNION ALL` 将正常数据与手动完结数据进行聚合返回。
* **防 SQL 注入:** `SqlSecurityUtil` 采用严苛的正则校验动态排序字段 (`orderBySql`)，彻底堵死 Order By 注入漏洞。

### 2.3 鉴权与上下文传递 (Auth & ThreadLocal)

* **拦截器 (`JwtInterceptor`):** 拦截一切非白名单请求，验证 RS256 签名并检查过期时间。
* **上下文:** 解析出 `userId` 和 `loginAccount` 存入 `UserContext` (基于 `ThreadLocal`)。并在请求结束 `afterCompletion`
  时严格调用 `clear()`，防止 Tomcat 线程池复用导致的内存泄漏和数据串位漏洞。

## 3. 接口文档 (API Documentation)

### 🟢 0. 全局统一响应结构 (Global Response)

后端所有接口（无论成功还是失败）都会被包装在 `Result<T>` 中返回给前端。前端 `request.ts` 拦截器会自动解包并处理这个外层结构。

| 字段名         | 类型      | 说明                                          |
|:------------|:--------|:--------------------------------------------|
| `success`   | Boolean | 业务是否成功（`true` 成功，`false` 失败）                |
| `code`      | Number  | 状态码（例如：`200` 成功，`401` Token失效，`500` 系统内部错误） |
| `msg`       | String  | 提示信息（例如："登录成功"、"服务器开小差了..."）                |
| `data`      | T (泛型)  | 实际的业务数据载荷（仅当 success 为 true 时有意义）           |
| `timestamp` | Number  | 服务器响应时间戳（毫秒），可用于日志追溯                        |

---

### 🔐 1. 认证模块接口 (Auth API)

负责系统的安全准入与密码管理。**注意：所有密码字段在网络传输前，必须使用国密 SM4 (CBC/PKCS5Padding) 算法进行加密**。

#### 1.1 用户登录

* **接口路径:** `/kaishi/auth/login`

* **请求方式:** `POST`

* **访问权限:** 无需 Token (白名单)

* **说明:** 校验用户账户与 SM4 加密后的密码，后端通过 BCrypt 比对，成功后签发 RS256 JWT Token。

  **请求参数 (Body - `LoginReq`)**

| 字段名                 | 类型     | 必填 | 说明                                      |
|:--------------------|:-------|:---|:----------------------------------------|
| `loginAccount`      | String | 是  | 登录账户名 (当前系统硬编码限制为 `"kaishi"`)           |
| `encryptedPassword` | String | 是  | 使用前台 `WEB_SM4_KEY/IV` 加密后的密码密文 (Hex 格式) |

**响应数据 (Data - `LoginResp`)**

| 字段名        | 类型     | 说明                                           |
|:-----------|:-------|:---------------------------------------------|
| `token`    | String | JWT 认证令牌 (RS256 签名，含 userId 与账号信息，有效期 12 小时) |
| `username` | String | 真实用户姓名（展示在右上角头像处）                            |

#### 1.2 修改密码

* **接口路径:** `/kaishi/auth/changePassword`

* **请求方式:** `POST`

* **访问权限:** 需头部携带 `Authorization: Bearer <Token>`

* **说明:** 校验旧密码，验证通过后使用 BCrypt 重新哈希并覆盖新密码。

  **请求参数 (Body - `PasswordReq` / `ChangePwdReq`)**

| 字段名              | 类型     | 必填 | 说明                             |
|:-----------------|:-------|:---|:-------------------------------|
| `loginAccount`   | String | 是  | 当前登录账户名                        |
| `oldPasswordEnc` | String | 是  | **SM4 加密**后的旧密码密文              |
| `newPasswordEnc` | String | 是  | **SM4 加密**后的新密码密文 (不能与旧密码明文一致) |

**响应数据**

* **Data:** `null` (仅依靠外层 `success: true` 判断)

---

### 📦 2. 采购单追踪模块接口 (Purchase Trace API)

处理核心的 ERP 采购与入库实绩对撞业务。

#### 2.1 获取采购追踪列表 (List Info)

* **接口路径:** `/kaishi/purchase/trace/list/info`

* **请求方式:** `POST`

* **说明:** 支持复杂的多条件分页查询，后端底层将 `DlyBuy` (采购明细) 和 `Dlystock` (入库实绩) 进行 `CROSS APPLY` 聚合，并辅以
  Caffeine 字典转译。

  **请求参数 (Body - `PurchaseOrderTraceDTO`)**

| 字段名              | 类型         | 必填 | 说明                                                                                             |
|:-----------------|:-----------|:---|:-----------------------------------------------------------------------------------------------|
| `pageNo`         | Number     | 否  | 页码 (默认 1)                                                                                      |
| `pageSize`       | Number     | 否  | 每页条数 (默认 50)                                                                                   |
| `queryPurchased` | Boolean    | 否  | **核心标识**：`true` 查全量已到货；`false` 查未到货/部分到货                                                       |
| `number`         | String     | 否  | 订单号 (模糊搜索)                                                                                     |
| `vchcode`        | Number     | 否  | 单据号 (精准搜索)                                                                                     |
| `dlyorder`       | Number     | 否  | 详单号 (精准搜索)                                                                                     |
| `minDate`        | String     | 否  | 单据起始日期 (`YYYY-MM-DD`)                                                                          |
| `maxDate`        | String     | 否  | 单据结束日期 (`YYYY-MM-DD`)                                                                          |
| `sortInfo`       | Array      | 否  | 排序规则对象数组。对象结构：`{ field: "字段名", flag: true(升序)/false(降序) }`。**后端通过 `SqlSecurityUtil` 正则拦截防注入**。 |
| *(其他扩充字段)*       | String/Num | 否  | `atypeid`, `btypeid`, `etypeid`, `ptypeId`, `blockno`, `usedtype`, `draft` 等，用于精准条件过滤。         |

**响应数据 (Data - `Array<DlyBuyVO>`)**

| 字段名             | 类型     | 说明                                                     |
|:----------------|:-------|:-------------------------------------------------------|
| `total`         | Number | 符合条件的总记录数 (仅附加在第一条数据上，供分页器使用)                          |
| `statusName`    | String | **[计算字段]** 状态名称 ("未到货", "部分到货", "超量到货", "已完成")         |
| `statusTag`     | String | **[计算字段]** 前端 UI 标签类型 ("warning", "danger", "success") |
| `owedQty`       | Number | **[计算字段]** 欠交数量 (`buyQty` - `stockedQty`)              |
| `date`          | String | 单据日期                                                   |
| `number`        | String | 进销存单据编号                                                |
| `vchCode`       | Number | 单据号                                                    |
| `dlyOrder`      | Number | 单据序号 (详单号)                                             |
| `btypeFullname` | String | **[字典转译]** 供应商全称                                       |
| `etypeFullname` | String | **[字典转译]** 经手人 (职员) 名称                                 |
| `ptypeFullname` | String | **[字典转译]** 商品/存货名称                                     |
| `unitFullname`  | String | **[字典转译]** 单位名称                                        |
| `blockNo`       | String | 批号/栋号                                                  |
| `buyQty`        | Number | 采购数量 (原表 `Qty`)                                        |
| `price`         | Number | 折前单价                                                   |
| `discountTotal` | Number | 折后总金额 (实际应付)                                           |
| `stockedQty`    | Number | **[动态聚合]** 实际已入库汇总数量                                   |
| `summary`       | String | 进销存单据摘要                                                |
| `comment`       | String | 行备注 (若为手动结清，则会追加显示手动结清的 `extInfo`)                     |
| `redWord`       | String | **[字典转译]** 红冲标记 ("是"/"否")                              |

---

#### 2.2 获取采购入库详情对撞记录 (Detail List)

* **接口路径:** `/kaishi/purchase/trace/list/detail`

* **请求方式:** `POST`

* **说明:** 用于点击“详情”时，查询特定采购明细记录对应的**所有历史入库实绩**。前端通常默认传 `pageNo: 1, pageSize: 500`
  取全部实绩。

  **请求参数 (Body - `PurchaseOrderTraceDTO`)**

| 字段名        | 类型     | 必填    | 说明     |
|:-----------|:-------|:------|:-------|
| `vchcode`  | Number | **是** | 主单据号   |
| `dlyorder` | Number | **是** | 详单号    |
| `pageNo`   | Number | 否     | 默认 1   |
| `pageSize` | Number | 否     | 默认 500 |

**响应数据 (Data - `Array<PurchaseTraceVO>`)**
*说明：返回数组的每一项代表一次真实的入库动作。由于对撞了 A 表(采购) 和 B 表(入库)，大量字段会带有前缀。*

| 字段名                      | 类型         | 说明                                  |
|:-------------------------|:-----------|:------------------------------------|
| `statusName` / `owedQty` | String/Num | **[计算字段]** 同列表接口，状态名称与欠交数           |
| `number` / `summary`     | String     | 进销存单据编号与摘要                          |
| `aVchcode`/`aDlyOrder`   | Number     | **[A表-采购]** 采购单号与详单号                |
| `aDate`                  | String     | **[A表-采购]** 采购日期                    |
| `aBtypeidFullname`       | String     | **[A表-采购]** 供应商全称                   |
| `aPtypeIdFullname`       | String     | **[A表-采购]** 存货商品名称                  |
| `aQty` / `aPrice`        | Number     | **[A表-采购]** 采购计划数 / 单价              |
| `bDate`                  | String     | **[B表-入库]** 本次实际入库日期 (可能为空，代表无入库记录) |
| `bVchcode`/`bDlyOrder`   | Number     | **[B表-入库]** 本次入库单号 / 详单号            |
| `bQty`                   | Number     | **[B表-入库]** 本次入库数量                  |
| `bKtypeidFullname`       | String     | **[B表-入库]** 入库仓库名称                  |

---

#### 2.3 手动置为完成 (Manual Finish)

* **接口路径:** `/kaishi/purchase/trace/manual/finish`

* **请求方式:** `POST`

* **说明:** 针对未完全到货但线下已结清/折让的订单，通过该接口写入附表，使其在系统中视为“已完成”。

  **请求参数 (Body - `FinishPurchaseReq` / `ManualFinishReq`)**

| 字段名        | 类型     | 必填 | 说明                       |
|:-----------|:-------|:---|:-------------------------|
| `vchType`  | Number | 是  | 单据类型 (前端固定传 `34` 代表采购订单) |
| `vchCode`  | Number | 是  | 采购单号                     |
| `dlyOrder` | Number | 是  | 采购详单号                    |
| `extInfo`  | String | 否  | 操作备注/原因 (例如："线下折让结清")    |

**响应数据**

* **Data:** `null` (操作成功依赖外层 `Result` 的 `success: true`)