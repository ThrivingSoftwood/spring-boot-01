***

# 🌲 Thriving Softwood (Spring Boot 01)

> **Next-Gen Java Scaffolding**  
> 基于 **JDK 25** + **Spring Boot 4.0** 的高性能微服务脚手架，原生集成 **虚拟线程 (Virtual Threads)** 与 *
*标准化全栈可观测性 (Observability)** 体系。

![Java](https://img.shields.io/badge/Java-25-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.1-green)
![Hutool](https://img.shields.io/badge/Hutool-v7-blue)
![OpenTelemetry](https://img.shields.io/badge/OTel-Standard-blueviolet)
![Zipkin](https://img.shields.io/badge/Zipkin-Persistence-orange)
![Elasticsearch](https://img.shields.io/badge/Elasticsearch-8.17-blue)

## 📖 项目简介 (Introduction)

本项目不仅仅是一个简单的 Web 工程，而是一个**探索未来 Java 并发与观测模式**的实验场。它深度整合了 JDK 25 的先进特性与
Micrometer Tracing 标准，旨在解决分布式系统在引入虚拟线程后的**逻辑链条断裂**、**数据持久化**及**性能瓶颈**痛点。

核心亮点：

* **精细化双模并发**：原生支持平台线程（PT）与虚拟线程（VT）的平滑切换，提供自定义异步注解 `@PtAsync` 与 `@VtAsync`，实现
  CPU/IO 任务物理隔离。
* **标准化观测底座**：彻底弃用手动维护 TraceID 的旧模式，全面拥抱 **Micrometer Tracing + OpenTelemetry (OTLP)**，支持 W3C
  标准链路追踪。
* **全内容日志追踪**：不仅能看链路图，更能通过 **Kibana** 实现“日志+链路”深度融合。支持将业务日志通过 OTLP 管道实时持久化至
  Elasticsearch。
* **智能跨线程上下文**：自研 `MicrometerTracingDecorator`，实现嵌套多线程场景下的 **[pSpanId -> spanId]** 语义化衔接日志。

## 🏗 模块架构 (Module Structure)

项目采用多模块 Maven 架构 (BOM 模式)，职责划分如下：

```text
spring-boot-01
├── common                  # 公共父模块 (BOM & Dependency Management)
│   ├── common-core         # [基石] 纯净工具类 (Hutool/Guava)、通用常量、枚举
│   ├── common-observability# [之眼] OTel SDK 配置、Log4j2 OTLP 桥接、Elasticsearch 索引管理
│   ├── common-framework    # [引擎] 混合异步配置、MapStruct 整合、Micrometer 装饰器
│   └── common-web          # [门户] Web 响应头增强 (WebTraceFilter)、全局异常处理、Jackson 定制
└── simple                  # [演练] 业务实现、多层级异步调用演示、持久化验证
```

## 🚀 核心特性 (Key Features)

### 1. 进化版双模并发 (Evolutionary Dual-Mode Concurrency)

系统根据任务性质（CPU/IO 密集型）自动选择最优线程模型，并通过 `MicrometerTracingDecorator` 保证链路不丢失。

| 标识      | 异步注解       | 说明      | 推荐场景                       |
|:--------|:-----------|:--------|:---------------------------|
| **PMT** | `@PtAsync` | 平台线程池执行 | CPU 密集型 (加密、复杂运算、图像处理)     |
| **VMT** | `@VtAsync` | 虚拟线程执行  | IO 密集型 (数据库查询、RPC 调用、文件读写) |

### 2. 工业级可观测性管道 (Unified Observability Pipeline)

系统构建了一套 **“应用端 -> Collector -> 存储端 -> UI端”** 的标准化观测流水线：

* **数据产生**：基于 `Log4j2 OTLP Appender` 与 Spring Boot 4 `ObservationRegistry`。
* **中继分流 (OTel Collector)**：通过 OTLP 协议接收数据，分流 Trace 至 Zipkin，分流 Logs 至 Elasticsearch。
* **语义化追踪**：在线程切换时自动打印衔接日志，例如：
  `🧵 Thread Dispatch: [span-1 -> span-2] Task submitted.` 完美勾勒跨线程因果关系。
* **持久化与展示**：
* **Tracing**: 数据存入 ES，通过 Zipkin UI 查看拓扑图与耗时分析。
* **Logging**: 数据存入 ES Data Stream，通过 Kibana 实现全文本检索与 TraceID 关联查询。

### 3. AOP 代理自愈机制

针对 Service 内部异步方法失效的经典痛点，采用 **“构造器注入必需品 + Setter 注入自身代理”** 的架构模式，配合 `@Lazy`
完美解决循环依赖并激活异步 AOP 增强。

## 📝 待办事项 (Roadmap)

- [x] 基础架构搭建 (JDK 25 + Spring Boot 4)
- [x] **并发体系升级** (平台线程与虚拟线程分流)
- [x] **标准化链路追踪** (Micrometer Tracing + OTLP)
- [x] **日志全内容持久化** (Elasticsearch + Kibana 可视化)
- [ ] **Redis 缓存集成** (适配虚拟线程连接池)
- [ ] **分布式监控指标** (接入 Prometheus/Grafana)
- [ ] **异常自诊断系统** (基于 TraceID 的错误堆栈自动聚类)

---

### 🤝 维护说明 (For AI Assistant)

*由于本人经常有开发起来昏天暗地,等到反应过来时修改内容过多导致无法将所有修改点一一列出的毛病,特此提供如下内容协助自己使用
AI 进行代码分析 并更新当前文档：*

1. 提示词

```prompt
> *当你（AI）读取此文档时，请注意：*
> 1. *新增功能模块时，请更新 **模块架构** 树。*
> 2. *新增中间件支持时，请在 **核心特性** 中增加相应章节。*
> 3. *始终保持 **待办事项** 的状态更新。*

请结合修改内容diff.txt,为我将优化和修改的内容补充到我提供的 README.md 中,使其更加完善和读者友好.只完整输出完善后的 README.md 即可

```

2. bash 命令

```bash
    
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
   find . -type f \( -name "*.xml" -o -name "*.yml" -o -name "*.properties" \) ! -name "pom.xml" ! -path "*/target/*" ! -path "*/.idea/*" ! -path "*/.mvn/*" | while read -r file; do
       echo "File: $file"
       echo "\n\`\`\`xml"
       cat "$file"
       echo "\n\`\`\`"
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
   
   # 从 docker 中获取 docker-compose.yml
   docker run --rm -v /var/run/docker.sock:/var/run/docker.sock ghcr.io/red5d/docker-autocompose ${dockerId}
```

---
Copyright © 2026 Thriving Softwood Team.