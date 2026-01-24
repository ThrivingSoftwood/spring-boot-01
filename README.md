***

# 🌲 Thriving Softwood (Spring Boot 01)

> **Next-Gen Java Scaffolding**  
> 基于 **JDK 25** + **Spring Boot 4.0** 的高性能微服务脚手架，原生集成 **虚拟线程 (Virtual Threads)** 与 **全链路追踪**
> 体系。

![Java](https://img.shields.io/badge/Java-25-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.1-green)
![Hutool](https://img.shields.io/badge/Hutool-v7-blue)
![Virtual Threads](https://img.shields.io/badge/Virtual_Threads-Enabled-blueviolet)
![Log4j2](https://img.shields.io/badge/Log4j2-Async-red)

## 📖 项目简介 (Introduction)

本项目不仅仅是一个简单的 Web 工程，而是一个**探索未来 Java 并发模式**的实验场。它采用了 DDD (领域驱动设计)
分层思想，核心目标是解决高并发场景下的**资源隔离**、**可观测性**痛点以及**极致的开发体验**。

核心亮点：

* **精细化双模并发**：不仅支持 VT/PT 切换，更引入了 `SPT`/`PMT`/`VMT` 线程染色机制，一眼识别线程属性。
* **智能链路追踪**：跨线程、跨服务的 TraceID/SpanID 自动传递与**变异**（父子线程 ID 语义化关联），支持 Log4j2 异步日志。
* **纯净工具链**：集成 **Hutool v7**、**MapStruct** 与 **Guava**，摒弃臃肿，追求极致性能。
* **生产级日志**：Log4j2 全异步模式 (Disruptor) + 生产级 JSON 布局优化 + 敏感数据脱敏基础。

## 🏗 模块架构 (Module Structure)

项目采用多模块 Maven 架构 (BOM 模式)，职责划分如下：

```text
spring-boot-01
├── common                  # 公共父模块 (BOM & Dependency Management)
│   ├── common-core         # [基石] 纯净工具类 (Hutool/Guava)、通用常量、枚举 (无 Web 依赖)
│   ├── common-logging      # [之眼] MDC 上下文管理、TraceID 染色、Log4j2 Disruptor 扩展
│   ├── common-framework    # [引擎] 异步配置、MapStruct 整合、AOP 切面、MdcTaskDecorator
│   └── common-web          # [门户] Web 过滤器 (TraceFilter)、全局异常处理、Jackson 配置
└── simple                  # [演练] 业务 Demo、MyBatis 模版、配置加载演示
```

## 🚀 核心特性 (Key Features)

### 1. 进化版双模并发 (Evolutionary Dual-Mode Concurrency)

为了最大化利用 JDK 25 特性并保证可观测性，我们建立了一套完善的**线程染色体系**。系统根据任务类型自动标记线程身份，并在日志中体现：

| 标识      | 全称                    | 说明         | 适用场景                   |
|:--------|:----------------------|:-----------|:-----------------------|
| **SPT** | Sync Platform Thread  | **同步主线程**  | HTTP 请求入口，Tomcat 核心线程  |
| **PMT** | Platform Multi-thread | **平台异步线程** | CPU 密集型 (加密、图像处理)      |
| **VMT** | Virtual Multi-thread  | **虚拟异步线程** | I/O 密集型 (DB 查询、RPC 调用) |
| **STS** | Sub-Thread Service    | **子任务线程**  | 线程池中实际执行任务的工作线程        |

**代码与日志对照：**

```java
// 1. 同步主线程 (日志前缀: sync-xxxx)
// log.info("Request received");

// 2. 虚拟线程异步任务 (日志前缀: async-v-xxxx)
@VtAsync
public void processIO() {
    //具体逻辑
}

// 3. 平台线程异步任务 (日志前缀: async-p-xxxx)
@PtAsync
public void processCPU() {
    // 具体逻辑
}
```

### 2. 智能全链路追踪 (Smart Distributed Tracing)

系统内置了完整的链路追踪闭环，解决了异步任务日志“串号”和“丢失”的问题，并增强了 ID 的语义。

* **入口 (TraceFilter)**: 请求进入时生成 `traceId`，标记为 `sync-` 开头。
* **传递 (MdcTaskDecorator)**:
* **上下文拷贝**: 任务提交时自动捕获主线程 MDC。
* **ID 变异**: 根据子线程类型（虚拟/平台），将 TraceID 前缀自动变更为 `async-v` 或 `async-p`，不仅追踪链路，还能追踪**线程切换路径
  **。
* **自愈 (TraceUtil)**: 即使主线程无 TraceID，子线程也会自动生成“孤儿链路 ID”以保证可追溯。
* **输出**:
* **Dev**: 控制台彩色高亮 `[%X{traceId}][%X{spanId}]`。
* **Prod**: 结构化 JSON (`log4j2-json-layout.json`)，集成 `Disruptor` 高性能队列。

### 3. 现代工具链集成 (Modern Toolchain)

* **Hutool v7**: 引入下一代 Hutool 工具库，模块化按需引入 (`hutool-core`, `hutool-crypto` 等)。
* **MapStruct**: 编译时生成 Bean 转换代码，性能远超 BeanUtils 反射。
* **Jackson Pro**: 深度定制 Jackson，支持 Java 8/JDK 25 新日期 API，优化序列化性能。

## 🛠 开发指南 (Development Guide)

### 环境要求

* **JDK**: 25 (开启 Preview 特性)
* **Maven**: 3.9+

### 快速上手

1. **依赖管理**: 所有内部模块版本由 Root POM 的 `dependencyManagement` 统一管控，子模块无需指定 version。
2. **配置加载**:

* `AutoConfiguration.imports`: 模块化自动装配。
* `log4j2-dev.xml`: 开发环境详细日志（含行号）。
* `log4j2-json-layout.json`: 生产环境极致性能日志（无行号）。

### MyBatis 最佳实践

请参考 `simple/.../mapper/0000_AncestorMapper.java`，其中包含了动态 SQL 的所有高级模版（`choose`, `trim`, `bind` 等）。

## 📝 待办事项 (Roadmap)

- [x] 基础架构搭建 (JDK 25 + Spring Boot 4)
- [x] **并发体系升级** (引入 SPT/PMT/VMT 线程染色)
- [x] **日志链路追踪** (MDC + TraceID 变异 + Log4j2 Disruptor)
- [x] **工具库标准化** (Hutool v7 + MapStruct + Guava)
- [ ] **Redis 缓存集成** (需适配 VT 模式下的连接池)
- [ ] **MyBatis 拦截器** (自动填充 create_time/update_time)
- [ ] **全局异常处理** (GlobalExceptionHandler 增强)
- [ ] **RocketMQ/Kafka 接入** (TraceID 跨进程透传)

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
```

---
Copyright © 2026 Thriving Softwood Team.