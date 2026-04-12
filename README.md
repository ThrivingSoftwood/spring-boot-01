***

# 🌲 Thriving Softwood (Spring Boot 01)

> **Next-Gen Java Scaffolding**  
> 基于 **JDK 25** + **Spring Boot 4.0** 的高性能微服务脚手架，原生集成 **虚拟线程 (Virtual Threads)** 与 *
*标准化全栈可观测性 (Observability)** 体系。

![Java](https://img.shields.io/badge/Java-25-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.5-green)
![Hutool](https://img.shields.io/badge/Hutool-v7-blue)
![OpenTelemetry](https://img.shields.io/badge/OTel-Standard-blueviolet)
![Zipkin](https://img.shields.io/badge/Zipkin-Persistence-orange)
![Elasticsearch](https://img.shields.io/badge/Elasticsearch-8.19.10-blue)

## 📖 项目简介 (Introduction)

本项目不仅仅是一个简单的 Web 工程，而是一个**探索未来 Java 并发与观测模式**的实验场。它深度整合了 JDK 25 的先进特性与
Micrometer Tracing 标准，旨在解决分布式系统在引入虚拟线程后的**逻辑链条断裂**、**多线程拓扑混乱**及**跨服务调用不可信**
等痛点。

核心亮点：

* **精细化双模并发**：原生支持平台线程（PT）与虚拟线程（VT）的物理隔离。**VT 采用信号量限流模式**，追求零队列损耗。
* **全链路边界突破**：适配 Spring Boot 4.0.5，实现 `RestClient/RestTemplate` 自动注入 W3C `traceparent`，打通微服务间信任链。
* **国密与立体安全**：集成 SM4 国密传输、BCrypt 存储与 RS256 签名的 JWT 认证机制，保障数据全生命周期安全。
* **极速本地缓存架构**：基于 `ApplicationRunner` 与 `Caffeine` 实现核心业务字典的零延迟全量预热。
* **极致性能观测**：自研 `MicrometerTracingDecorator`，通过 **惰性日志 (Lazy Logging)** 与 **并行链路修正**，兼顾低开销与高透明度。

## 🏗 模块架构 (Module Structure)

项目采用多模块 Maven 架构 (BOM 模式)，职责划分如下：

```text
spring-boot-01
├── common                  # 公共父模块 (BOM & Dependency Management)
│   ├── common-core         # [基石] 纯净工具类(Hutool/Guava)、SM4/JWT 核心引擎、全局 Result
│   ├── common-database     # [存储] MyBatis-Plus 增强、多数据源(DS)路由、自动填充处理器
│   ├── common-observability# [之眼] OTel SDK 配置、Log4j2 OTLP 桥接、ES Ingest Pipeline 治理
│   ├── common-framework    # [引擎] 混合异步配置(Semaphore VT)、AOP 日志拦截、代理自愈
│   └── common-web          # [门户] 全局异常捕获、JWT 拦截与上下文管理、Web 链路增强
├── algorithm               # [算法] 核心算法练习与性能压测模块
├── sample                  # [演练] 多层级并行异步调用 (Fan-out) 及 Trace 链路追踪演示
└── business-app            # [业务] 核心业务应用落地层 (剥离具体业务标识的纯净微服务实现)
```

## 🚀 核心特性 (Key Features)

### 1. 极致异步性能管控 (High-Performance Async Governance)

系统针对任务性质，通过自定义注解 `@PtAsync` 与 `@VtAsync` 驱动底层执行器：

* **信号量驱动的 VT (Semaphore-based VT)**：在 `AsyncConfig` 中弃用重型线程池队列，采用 `SimpleAsyncTaskExecutor` 配合
  `concurrencyLimit`。利用信号量机制直接控制虚拟线程并发数，**移除阻塞队列锁竞争**，实现零队列损耗的极致吞吐。
* **代理顺序精准调优 (Proxy Order Tuning)**：严格定义 `@EnableAsync` (Order: Lowest-1) 与 `TraceAspect` (Order: Lowest)
  的执行顺序。确保线程切换先于切面运行，完美解决子线程内部 Span 命名的失效问题。

### 2. 工业级全栈观测管道 (Resilient Observability Pipeline)

* **并发链路修复 (Parallel View Fix)**：修正 `MicrometerTracingDecorator`。通过在装饰器中显式保留 `nextSpan()` 逻辑，确保
  Zipkin 能够正确识别并行异步分支（Fan-out），生成精准的甘特图拓扑。
* **全异步日志引擎 (RingBuffer Logging)**：抛弃传统 Logback，基于 LMAX Disruptor 实现 Log4j2 全异步日志，大幅降低高并发下的磁盘
  I/O 阻塞。
* **监控命名标准化**：
    * **Web 层**：利用 `WebSpanNameInterceptor` 直接重命名入口 Span (如 `Controller.Method`)。
    * **业务层**：优化 `TraceAspect` 与 `ServiceLoggingAspect` 切面，实时捕获异步方法与业务层的入参/耗时，实现精细化拓扑呈现。

### 3. 多层级立体安全防御 (Multi-layer Security)

* **混合密码学策略 (Hybrid Cryptography)**：
    * **传输层**：基于 `Jasypt` 和定制的 `Sm4Util`，采用**国密 SM4 (CBC/PKCS5Padding)** 算法处理前后端的高敏感数据传输。
    * **存储层**：采用 `BCrypt` 算法进行密码单向哈希，内置盐值防御彩虹表攻击。
    * **认证层**：基于 Hutool v7 构建 `JwtUtil`，采用非对称加密 `RS256` 签发 Token。
* **防内存泄漏与串权 (Context Isolation)**：`JwtInterceptor` 提取 Token 载荷存入基于 `ThreadLocal` 的 `UserContext`，并在
  `afterCompletion` 阶段执行严格的 `clear()` 动作，彻底杜绝 Tomcat 线程复用导致的越权漏洞。

### 4. 高性能数据访问与缓存 (Data Access & Caching)

* **零延迟字典树预热 (Zero-Latency Dictionary)**：系统通过 `DictionaryPreloadRunner` 钩子，在应用启动瞬间将高频访问的业务字典数据全量灌入
  **Caffeine L1 Cache**，配合 `@Cacheable` 实现纳秒级转译。
* **动态数据源与防爆破**：集成 `Dynamic Datasource` 轻松跨越主从或异构数据库；注入 MyBatis-Plus
  `BlockAttackInnerInterceptor` 拦截无条件的全表更新/删除，守住生产环境最后一道防线。
* **SQL 级智能防护**：提供 `SqlSecurityUtil` 以强正则模式拦截动态 `ORDER BY` 参数，彻底免疫 SQL 排序注入。

## 🛠️ 故障排查与调试工具 (Debugging Toolbox)

* **AOP 代理自愈**：采用 **“构造器注入 + Setter 注入自身代理”** 模式，配合 `@Lazy` 解决循环依赖，确保 Service
  内部调用依然能触发异步与链路增强。
* **MyBatis-Plus AST 生成器**：定制化 `FastAutoGenerator` 引擎，一键生成基于 `AncestorDbEntity` 的规范化 CRUD 骨架。
* **GZIP 流量探测**：自研 Python `mock_es` 脚本，支持实时解压并打印 OTel Collector 发出的 Bulk 流量，辅助定位 Payload 结构。

## 📝 待办事项 (Roadmap)

**DONE**

- [x] 基础架构搭建 (JDK 25 + Spring Boot 4)
- [x] **混合并发体系升级** (信号量限流模式 VT)
- [x] **并行链路追踪修正** (Micrometer Tracing 拓扑修复)
- [x] **跨服务边界突破** (RestClient/RestTemplate 自动透传)
- [x] **可观测性命名治理** (Web 拦截器与 AOP 命名规范化)
- [x] **安全与缓存底座** (SM4/RS256 混合加密与 Caffeine 预热机制)

**TODO**

- [ ] **链路安全防御 (Trace Security)** (网关层 TraceID 清理与防御)
- [ ] **业务上下文透传 (Baggage)** (租户/用户信息全链路透传)
- [ ] **索引生命周期管理 (ILM)** (自动清理过期日志)
- [ ] **虚拟线程底层优化** (在 Micrometer 升级后修改并发任务底层容器,从 ThreadLocal 切换到 ScopeValue)

---

### 🤝 维护说明 (For AI Assistant)

*由于本人经常有开发起来昏天暗地,等到反应过来时修改内容过多导致无法将所有修改点一一列出的毛病,特此提供如下内容协助自己使用
AI 进行代码分析并更新当前文档：*

1. 提示词

```prompt
> *当你（AI）读取此文档时，请注意：*
> 1. *新增功能模块时，请更新 **模块架构** 树。*
> 2. *新增中间件支持时，请在 **核心特性** 中增加相应章节。*
> 3. *始终保持 **待办事项** 的状态更新。*

请结合修改内容diff.txt,为我将优化和修改的内容补充到我提供的 README.md 中,使其更加完善和读者友好.只完整输出完善后的 README.md 即可

我正在搭建一个我正在自研的 springboot 架构,我将为你提供我完整项目结构和 pom 依赖配置,请你仔细理解后输出"已知悉".

1. 项目结构

2. 所有 pom 配置如附件

```

2. bash 命令

```bash
    # 升级漏洞 begin
    # 查找版本控制
    mvn dependency:tree -Dverbose -Dincludes=groupId:artifactId
    # 查看最终生效的 pom
    mvn help:effective-pom -Doutput=effective.xml
    # 升级漏洞 end
    
    # pgsql 相关
    sudo -u postgres /Library/PostgreSQL/17/bin/pg_ctl -D /Library/PostgreSQL/17/data start
    sudo -u postgres /Library/PostgreSQL/17/bin/pg_ctl -D /Library/PostgreSQL/17/data stop
    sudo -u postgres /Library/PostgreSQL/17/bin/pg_ctl -D /Library/PostgreSQL/17/data restart
    
    # 先列出所有变更文件，然后用grep过滤掉不想看的文件
    git diff HEAD --name-only | \
    grep -v 'package-lock.json' | \
    grep -v 'mybatis' | \
    grep -v 'spring' | \
    grep -v '\.properties$' > files_to_diff.txt
    
    # 将想要查看的文件中已更改内容导出到 diff.txt
    git diff HEAD -- $(cat files_to_diff.txt) > diff.txt

    # 显示项目结构
   tree -I "target|node_modules|.git|out|*.iml|logs|package-info.java|mvnw*|*.md|.git*" --dirsfirst
   
   # 输出 pom.xml 文件内容
   find . -name "pom.xml" ! -path "*/target/*" ! -path "*/.idea/*" ! -path "*/.mvn/*" | while read -r file; do
       echo "File: $file"
       echo "\n\`\`\`xml"
       cat "$file"
       echo "\n\`\`\`"
   done

   # 输出所有 pom.xml 外的配置文件内容(注意数据脱敏)
   find . -type f \( -name "*.xml" -o -name "*.yml" -o -name "*.properties" \) ! -name "pom.xml" ! -name "*Mapper.xml" ! -path "*/target/*" ! -path "*/.idea/*" ! -path "*/.mvn/*" | while read -r file; do
       echo "File: $file"
       echo "\n\`\`\`xml"
       cat "$file"
       echo "\n\`\`\`"
   done

   # 输出所有 imports 文件内容
   find . -type f -name "*.imports" ! -path "*/target/*" ! -path "*/.idea/*" ! -path "*/.mvn/*" | while read -r file; do
       echo "\n\n"
       echo "File: $file"
       echo "\`\`\`"
       cat "$file"
       echo "\n\`\`\`"
       echo ""
   done
   
   # 输出所有java 文件的内容(注意关键信息不要硬编码以及其他数据脱敏)
   find . -type f -name "*.java" ! -name "package-info.java" ! -path "*/target/*" ! -path "*/test/*" | while read -r file; do
       echo "\n\n"
       echo "File: $file"
       echo "\`\`\`java"
       cat "$file"
       echo "\n\`\`\`"
       echo ""
   done
   
   # 输出所有 mapper.xml 文件的内容(注意关键信息不要硬编码以及其他数据脱敏)
   find . -type f -name "*Mapper.xml" ! -path "*/target/*" ! -path "*/test/*" | while read -r file; do
       echo "\n\n"
       echo "File: $file"
       echo "\`\`\`java"
       cat "$file"
       echo "\n\`\`\`"
       echo ""
   done
   
   # 输出所有 yml 配置文件内容
   find . -type f \( -name "*.yml" -o -name "*.properties" \) ! -name "pom.xml" ! -path "*/target/*" ! -path "*/.idea/*" ! -path "*/.mvn/*" | while read -r file; do
       echo "File: $file"
       echo "\n\`\`\`yml"
       cat "$file"
       echo "\n\`\`\`"
   done
   
   # 从 docker 中获取 docker-compose.yml
   docker run --rm -v /var/run/docker.sock:/var/run/docker.sock ghcr.io/red5d/docker-autocompose ${dockerId}
   # elasticsearch-20260127-8.19.10
   docker run --rm -v /var/run/docker.sock:/var/run/docker.sock ghcr.io/red5d/docker-autocompose 04eb5da4358c429df8b1818edfc497c8fef01f6cf2854823181c617700f467a0
   # kibana-20260127-8.19.10
   docker run --rm -v /var/run/docker.sock:/var/run/docker.sock ghcr.io/red5d/docker-autocompose e92fd3c6a3c57c938a3c7202c6a3f656324282cc22bf32040dbd042000760f4a
   # zipkin-20260126-latest
   docker run --rm -v /var/run/docker.sock:/var/run/docker.sock ghcr.io/red5d/docker-autocompose bb8d94ba96ea5d06dfb1c770d5562dcc5dd9df293a4f7043b4e57a0df0aff766
   # otel-20260126-latest
   docker run --rm -v /var/run/docker.sock:/var/run/docker.sock ghcr.io/red5d/docker-autocompose d97198db9f7995603fffca84003575079b1bb6fb2e5ea193560f7094adb3fd4f
   # redis-20260110-latest
   docker run --rm -v /var/run/docker.sock:/var/run/docker.sock ghcr.io/red5d/docker-autocompose e55a2f2d69345cc6b23b50136326ba596743cae4667504310d4eb7567d54e1a9
   # mysql-20251224-9.5
   docker run --rm -v /var/run/docker.sock:/var/run/docker.sock ghcr.io/red5d/docker-autocompose d95815199296fdcd7a82ed231c5368c0820a60c0b478c53301d9ccaf43af458e
```

---
Copyright © 2026 Thriving Softwood Team.  
*Last Updated: 2026.01.29 - 柳燊(ThrivingSoftwood)*
