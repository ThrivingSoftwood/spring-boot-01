这是一个非常明智的决定。一份结构清晰、模块化的 README 文档，不仅是你项目的“门面”，更是我们未来协作的“协议”。

这份文档采用了 **“模块化 + 功能清单”** 的结构。后续当你增加新功能（比如 Redis 缓存、MyBatis 拦截器）时，只需在对应的章节追加内容即可，非常易于维护。

请在项目根目录下创建 `README.md` 并粘贴以下内容：

---

# 🌲 Thriving Softwood (Spring Boot 01)

> **Next-Gen Java Scaffolding**  
> 基于 **JDK 25** + **Spring Boot 4.0** 的高性能微服务脚手架，原生集成 **虚拟线程 (Virtual Threads)** 与 **全链路追踪**
> 体系。

![Java](https://img.shields.io/badge/Java-25-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.1-green)
![Virtual Threads](https://img.shields.io/badge/Virtual_Threads-Enabled-blue)
![Log4j2](https://img.shields.io/badge/Log4j2-Async-red)

## 📖 项目简介 (Introduction)

本项目不仅仅是一个简单的 Web 工程，而是一个**探索未来 Java 并发模式**的实验场。它采用了 DDD (领域驱动设计)
分层思想，核心目标是解决高并发场景下的**资源隔离**与**可观测性**痛点。

核心亮点：

* **双模并发架构**：同时支持 **VT (虚拟线程)** 与 **PT (平台线程)**，通过注解语义化驱动。
* **无感链路追踪**：跨线程、跨服务的 TraceID/SpanID 自动传递，支持 Log4j2 异步日志。
* **极致性能**：Log4j2 全异步模式 + 生产级 JSON 布局优化。

## 🏗 模块架构 (Module Structure)

项目采用多模块 Maven 架构，职责划分如下：

```text
spring-boot-01
├── common                  # 公共父模块 (BOM)
│   ├── common-core         # [基石] 纯净的工具类、枚举、常量 (无 Spring Web 依赖)
│   ├── common-logging      # [之眼] MDC 管理、TraceID 生成、Log4j2 扩展
│   ├── common-framework    # [引擎] 异步配置、AOP 切面、自定义注解、自动装配
│   └── common-web          # [门户] Web 过滤器 (TraceFilter)、全局异常处理
└── simple                  # [演练] 业务 Demo、MyBatis 模版、配置加载演示
```

## 🚀 核心特性 (Key Features)

### 1. 双模异步并发 (Dual-Mode Concurrency)

为了最大化利用 JDK 25 特性，我们拒绝“一刀切”的线程池策略，而是将任务分为两类：

| 特性       | 虚拟线程 (VT)                           | 平台线程 (PT)                         |
|:---------|:------------------------------------|:----------------------------------|
| **适用场景** | **I/O 密集型** (DB查询, HTTP调用, 文件读写)    | **CPU 密集型** (加密解密, 复杂计算, 大数据处理)   |
| **底层实现** | `SimpleAsyncTaskExecutor` (Virtual) | `ThreadPoolTaskExecutor` (Pooled) |
| **注解驱动** | `@VtAsync`                          | `@PtAsync`                        |
| **日志标识** | `[sub-vt-xxxxxx]`                   | `[sub-pt-xxxxxx]`                 |

**使用样例：**

```java

@Service
public class OrderService {

    // 🚀 这种任务用虚拟线程，吞吐量极高
    @VtAsync
    public CompletableFuture<String> queryRemoteOrder() {
        return remoteApi.get();
    }

    // 🧱 这种任务用平台线程，防止阻塞 CPU
    @PtAsync
    public void generateReportImage() {
        imageUtil.process();
    }
}
```

### 2. 全链路追踪 (Distributed Tracing)

系统内置了完整的链路追踪闭环，解决了异步任务日志“串号”和“丢失”的问题。

* **入口 (TraceFilter)**: 请求进入时生成 `traceId`，写入 Response Header (`X-Trace-Id`)。
* **传递 (MdcTaskDecorator)**: 无论是 VT 还是 PT，任务提交时自动拷贝主线程 MDC 上下文。
* **自愈 (TraceUtil)**: 即使主线程无 TraceID，子线程也会自动生成“孤儿链路 ID”以保证可追溯。
* **输出**:
    * **Dev**: 彩色高亮日志 `[%X{traceId}][%X{spanId}]`。
    * **Prod**: 结构化 JSON (`log4j2-json-layout.json`)，字段已按 ELK 最佳实践排序。

### 3. 生产级日志 (Production Logging)

* **全异步模式**: 启用了 `Log4jContextSelector`，性能是 Logback 的 10 倍以上。
* **环境隔离**:
    * `dev`: 输出到控制台，带行号，方便调试。
    * `prod`: 输出到文件，JSON 格式，关闭行号 (`includeLocation="false"`) 以获取极致性能。

## 🛠 开发指南 (Development Guide)

### 环境要求

* **JDK**: 25 (开启 Preview 特性)
* **Maven**: 3.9+

### 配置加载机制

项目演示了 Spring Boot 配置加载的高级用法（在 `SimpleApplication.java` 中有详细注释）：

* `AutoConfiguration.imports`: 模块化自动装配。
* `application-dev/prod.yml`: 多环境配置。
* `RandomValuePropertySource`: 随机数配置演示。

### MyBatis 最佳实践

请参考 `simple/.../mapper/0000_AncestorMapper.java`，其中包含了动态 SQL 的所有高级模版（`choose`, `trim`, `bind` 等）。

## 📝 待办事项 (Roadmap)

- [x] 基础架构搭建 (JDK 25 + Spring Boot 4)
- [x] 异步并发体系 (VT/PT)
- [x] 日志链路追踪 (MDC + Log4j2)
- [ ] **Redis 缓存集成** (需适配 VT 模式下的连接池)
- [ ] **MyBatis 拦截器** (自动填充 create_time/update_time)
- [ ] **全局异常处理** (GlobalExceptionHandler)
- [ ] **RocketMQ/Kafka 接入** (TraceID 跨进程透传)

---

### 🤝 维护说明 (For AI Assistant)

> *当你（AI）读取此文档时，请注意：*
> 1. *新增功能模块时，请更新 **模块架构** 树。*
> 2. *新增中间件支持时，请在 **核心特性** 中增加相应章节。*
> 3. *始终保持 **待办事项** 的状态更新。*

---
Copyright © 2026 Thriving Softwood Team.