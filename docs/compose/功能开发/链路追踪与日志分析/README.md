# Spring Boot 4 + OpenTelemetry 可观测性实战总结

## 1. Maven 依赖管理

本项目采用 **BOM (Bill of Materials)** 模式管理版本，核心依赖主要集中在 `common-observability` 模块中。

### 1.1 父工程版本控制 (`pom.xml`)

定义全局版本号，确保 Spring Boot 4、Micrometer 和 OpenTelemetry 组件兼容。

```xml

<properties>
    <jdk.version>25</jdk.version>
    <springboot.version>4.0.2</springboot.version>
    <otel.log4j.appender.version>2.24.0-alpha</otel.log4j.appender.version>
    <disruptor.version>4.0.0</disruptor.version>
</properties>
```

### 1.2 核心模块依赖 (`common-observability/pom.xml`)

这里实现了日志框架的替换（Logback -> Log4j2）以及 OTel 的集成。

```xml

<dependencies>
    <!-- 1. 日志门面与高性能队列 -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
    </dependency>
    <dependency>
        <groupId>com.lmax</groupId>
        <artifactId>disruptor</artifactId> <!-- Log4j2 异步核心 -->
    </dependency>

    <!-- 2. Log4j2 集成 (排除默认 Logback) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-log4j2</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-logging</artifactId>
            </exclusion>
        </exclusions>
    </dependency>

    <!-- 3. Micrometer Tracing 桥接 OpenTelemetry -->
    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-tracing-bridge-otel</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-micrometer-tracing-opentelemetry</artifactId>
    </dependency>

    <!-- 4. 数据导出器 (Exporters) -->
    <dependency>
        <groupId>io.opentelemetry</groupId>
        <artifactId>opentelemetry-exporter-otlp</artifactId> <!-- 发送给 Collector -->
    </dependency>
    <dependency>
        <groupId>io.opentelemetry</groupId>
        <artifactId>opentelemetry-exporter-zipkin</artifactId> <!-- 备用/调试 -->
    </dependency>

    <!-- 5. 关键组件：Log4j2 OTel Appender -->
    <!-- 允许 Log4j2 直接将 LogEvent 转换为 OTel LogRecord -->
    <dependency>
        <groupId>io.opentelemetry.instrumentation</groupId>
        <artifactId>opentelemetry-log4j-appender-2.17</artifactId>
        <version>${otel.log4j.appender.version}</version>
    </dependency>
</dependencies>
```

---

## 2. 应用端配置

### 2.1 Java 配置注入

解决 Log4j2 初始化早于 Spring Context 的问题，手动将 Spring 管理的 `OpenTelemetry` 实例注入到 Log4j 系统中。

```java

@Configuration
@RequiredArgsConstructor
public class Log4j2OtlpConfig {
    private final OpenTelemetry openTelemetry;

    @PostConstruct
    public void start() {
        // 核心：激活 OTel Appender
        OpenTelemetryAppender.install(openTelemetry);
    }
}
```

### 2.2 Log4j2 生产配置 (`log4j2-prod.xml`)

配置异步 Appender 以保证业务性能，并使用 OpenTelemetry Appender 进行上报。

```xml

<Appenders>
    <!-- OTel 核心 Appender -->
    <OpenTelemetry name="OTEL_CORE">
        <!-- 即使加了 Layout，Alpha 版依赖仍可能发送 Map 结构 -->
        <PatternLayout pattern="%m"/>
    </OpenTelemetry>

    <!-- Async 包装器：防止网络抖动阻塞业务线程 -->
    <Async name="OTEL_ASYNC" blocking="false" bufferSize="4096">
        <AppenderRef ref="OTEL_CORE"/>
    </Async>
</Appenders>

<Loggers>
<Root level="INFO" includeLocation="false">
    <AppenderRef ref="OTEL_ASYNC"/>
</Root>
</Loggers>
```

---

## 3. 遇到的核心挑战

### 3.1 问题描述

在日志上报至 Elasticsearch 时，出现 **Type Mismatch** 错误：
> `failed to parse field [body] ... IllegalArgumentException: Expected text ... but found START_OBJECT`

### 3.2 深度排查 (Mock Debugging)

为了确认 Java 端发出的真实数据格式（排除配置干扰），编写了 Python 脚本 `mock_es.py` 伪装成 ES 服务，拦截 OTel Collector
的请求并解压 GZIP。

**排查结果：**
Java 端发送的 LogRecord Body 是结构化的 Map：

```
"body": {
"text": "✅ [1. Main Thread] 任务完成..."
}
```

而 Elasticsearch 索引 Mapping 期望 `body` 是 String (`text` 类型)，导致写入失败。

---

## 4. 最终解决方案 (ES Ingest Pipeline)

相比在 Collector 层使用复杂脚本清洗数据，我们在 **存储层 (Elasticsearch)** 实施了更稳健的兼容方案。

### 4.1 方案架构

1. **Collector**: 保持“哑管道”模式，只负责转发，不做复杂转换。
2. **Elasticsearch**: 使用 `Ingest Pipeline` 拦截写入请求，动态修正数据格式。

### 4.2 实施步骤

**步骤一：定义清洗管道 (Ingest Pipeline)**
无论上游发送的是 String 还是 Map，统一转换为 Map 结构。

```
PUT _ingest/pipeline/fix_body_pipeline
{
  "description": "兼容性修复：确保 body 永远是 Object 结构",
  "processors": [
    {
      "script": {
        "lang": "painless",
        "source": """
          // 如果收到的是纯字符串，将其包装为 map，避免类型冲突
          if (ctx.body instanceof String) {
            Map m = new HashMap();
            m.put('message', ctx.body);
            ctx.body = m;
          }
        """
      }
    }
  ]
}
```

**步骤二：应用索引模版 (Index Template)**
使用 `flattened` 类型处理日志体，防止字段爆炸；将 Trace ID 设为 `keyword` 优化查询。

```
PUT _index_template/logs-global-template
{
  "index_patterns": ["logs-*"],
  "template": {
    "settings": {
      "index.default_pipeline": "fix_body_pipeline", // 绑定清洗管道
      "number_of_shards": 1
    },
    "mappings": {
      "properties": {
        "@timestamp": { "type": "date" },
        "trace_id":   { "type": "keyword" },
        "span_id":    { "type": "keyword" },
        // 关键：允许 body 内部结构动态变化而不报错
        "body":       { "type": "flattened" }
      }
    }
  }
}
```

### 4.3 Collector 最终配置 (`otel-collector.yml`)

配置回归简洁，生产就绪。

```yaml
receivers:
  otlp:
    protocols:
      http: { endpoint: "0.0.0.0:4318" }
      grpc: { endpoint: "0.0.0.0:4317" }

processors:
  batch: { }

exporters:
  zipkin:
    endpoint: "http://host.docker.internal:9411/api/v2/spans"
  elasticsearch:
    endpoints: [ "http://host.docker.internal:9200" ]
    tls: { insecure: true }
    logs_index: "logs-app"

service:
  pipelines:
    traces:
      receivers: [ otlp ]
      processors: [ batch ]
      exporters: [ zipkin ]
    logs:
      receivers: [ otlp ]
      processors: [ batch ]
      exporters: [ elasticsearch ]
```

---

## 5. 成果总结

1. **全链路打通**：成功实现了 Spring Boot 应用 -> OTel Collector -> Elasticsearch/Zipkin 的完整数据流。
2. **高健壮性**：通过 Elasticsearch 的 `flattened` 映射和 `Ingest Pipeline`，系统现在能够容忍日志格式的动态变化（String/Map
   均可），彻底解决了依赖库版本差异导致的格式兼容问题。
3. **调试能力沉淀**：掌握了使用 Python Mock Server 拦截并分析 OTel GZIP 流量的调试方法。

[待完善]

1. 配置 Elasticsearch 索引生命周期管理 (ILM) 策略，设定 Hot/Warm 阶段以自动清理陈旧日志，防止磁盘空间溢出。
2. 在 Kibana 中构建可观测性仪表盘（Dashboard），集成 Tracing 聚合图表与 Logs 实时流分析视图。
3. 持续跟进 Prometheus 指标采集模块与 Spring Boot Actuator 的深度集成，补全“指标（Metrics）”维度的观测能力。