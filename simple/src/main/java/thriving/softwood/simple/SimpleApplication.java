package thriving.softwood.simple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ThrivingSoftwood
 * @apiNote 演示外部属性加载
 */
@SpringBootApplication
public class SimpleApplication {

    static void main(String[] args) {

        // 创建应用上下文对象
        SpringApplication app = new SpringApplication(SimpleApplication.class);

        // springboot 启动时打印横幅的模式,OFF 为关闭
        // app.setBannerMode(Banner.Mode.CONSOLE);

        // 外部配置加载示例 begin (后面的方法会覆盖前面的同名属性)
        /* formatter:off
         * 01. 加载外部配置文件
         * Properties props = new Properties();
         * props.setProperty("spring.config.location", "classpath:/config/spring/");

         * 如下加载方式只适用于 properties 文件:
         * try (InputStream is =
         *     SimpleApplication.class.getClassLoader().getResourceAsStream("config/spring/application.yml")) {
         *     props.load(is);
         * } catch (Exception e) {
         *     logger.error("加载失败!", e);
         * }
         *
         * 是配置生效
         *
         * app.setDefaultProperties(props);
         * @formatter:on
         */

        // 02. 在配置类上加注解 @PropertySource;当且只当应用上下文刷新时才会生效,对于某些应用配置项来说太晚了以至于有时不会生效
        // 注解内容: @PropertySource(value="classpath:config/spring/application.properties")

        // 03. 默认的 application.yml/application.properties

        /**
         * 04. 内置随机数生成器, 官方原文是 `A RandomValuePropertySource that has properties only in random.*.`
         *
         * @formatter:off
         *
         * Spring Boot 在启动时，会自动向环境（Environment）中注入一个特殊的配置源，它的作用不是“读取”静态值，而是“动态生成”随机值。
         *
         * 简单来说，它就是一个**内置的随机数生成器**。只要你在配置文件（如 `application.yml`）中使用了以 `random.*` 开头的 key，这个 `RandomValuePropertySource` 就会拦截它，并生成一个随机值替换进去。
         *
         * ---
         *
         * ### 📘 深度解析：这是什么黑魔法？
         *
         * 在 Spring 的 `Environment` 抽象中，配置通常来自于 `.properties` 文件、环境变量或系统参数，这些都是**静态**的。
         *
         * 但 `RandomValuePropertySource` 是一个**动态**的 PropertySource。
         * *   **触发条件：** Key 必须以 `random.` 开头。
         * *   **工作原理：** 当 Spring 解析到 `${random.int}` 这种占位符时，它不会去文件里找一个叫 `random.int` 的配置项，而是委托给 `RandomValuePropertySource`，后者在内存中实时计算出一个随机数并返回。
         *
         * ### 💻 实战用法：它能产生什么？
         *
         * 你可以在 `application.yml` 或 `application.properties` 中直接使用以下语法：
         *
         * ```yaml
         * my-config:
         *   # 1. 随机字符串 (MD5 hash 格式)
         *   secret: ${random.value}
         *
         *   # 2. 随机 UUID
         *   uuid: ${random.uuid}
         *
         *   # 3. 随机整数 (int 范围)
         *   number: ${random.int}
         *
         *   # 4. 小于 10 的随机整数 (0-9)
         *   less-than-ten: ${random.int(10)}
         *
         *   # 5. 指定范围的随机整数 [1024, 65535] (包含1024, 不包含65535)
         *   range-number: ${random.int[1024,65535]}
         *
         *   # 6. 随机长整数
         *   big-number: ${random.long}
         * ```
         *
         * ### 🚀 典型应用场景 (Best Practices)
         *
         * 为什么我们需要在配置文件里生成随机数？通常有以下几个“专家级”用法：
         *
         * #### 1. 单元测试中的随机端口
         * 在编写集成测试时，为了避免端口冲突（比如 Jenkins 并发构建时），我们经常设置随机端口：
         * ```yaml
         * server:
         *   port: ${random.int[20000,30000]}
         * ```
         *
         * #### 2. 生成临时密钥/盐值 (Salt)
         * 如果应用启动时需要一个临时的内部通讯密钥，且不需要持久化，可以使用它：
         * ```yaml
         * app:
         *   security:
         *     jwt-secret: ${random.uuid}
         * ```
         *
         * #### 3. 负载均衡模拟/测试数据
         * 在开发环境中，模拟不同的延迟或 ID：
         * ```yaml
         * mock:
         *   delay-ms: ${random.int[100,500]} # 模拟 100ms 到 500ms 的延迟
         * ```
         *
         * ---
         *
         * ### ⚠️ 避坑指南 (Critical Warnings)
         *
         * 必须注意到关于它的两个**关键特性**，否则容易踩坑：
         *
         * #### 1. 解析时机是“上下文初始化时”
         * 这个随机值是在 Spring **加载配置文件并注入 Bean 属性的那一瞬间**生成的。
         * *   如果你将 `${random.int}` 注入给一个 **Singleton（单例）** Bean，那么这个值在应用整个生命周期内**保持不变**。
         * *   它**不是**每次你调用 getter 方法时都会变。
         *
         * #### 2. 它是伪随机 (Pseudo-random)
         * 它是基于 Java 标准库的 `java.util.Random` 实现的。
         * *   **安全警示：** 千万不要用它来生成高强度的生产环境私钥（Private Key）或密码学相关的 Seed。对于真正的安全需求，请在代码中使用 `SecureRandom`。
         *
         * ### 🧩 总结
         *
         * `"A RandomValuePropertySource that has properties only in random.*"` 这句话的翻译是：
         * > **Spring Boot 提供了一个专门处理 `random.*` 前缀的配置源，它就像一个魔法转换器，把配置文件里的占位符在启动时变成了真正的随机数值。**
         *
         * @formatter:on
         */

        /**
         * 05. 操作系统环境变量 `spring.config.location`(覆盖的太彻底,以至于其它配置文件失效)
         *
         * @formatter:off
         *
         * 为什么使用 `spring.config.location` 指定配置文件路径,会导致所有其他配置文件内容都失效
         *
         * 简单直接的结论是：**当你使用 `spring.config.location` 指定路径时，Spring Boot 内部的逻辑会将“默认搜索路径列表”直接覆盖（Overwrite），而不是追加（Append）。**
         *
         * 这就像你给快递员下指令：
         * *   **默认情况：** 快递员手里有一张清单，上面写着“去公司前台、去家里信箱、去物业柜子找找有没有包裹”。
         * *   **使用了 `spring.config.location`：** 你把这张清单撕了，给他一张新纸条，上面只写着“去地下室找”。于是，公司前台和家里的信箱就被完全忽略了。
         *
         * 下面我们深入底层源码逻辑和架构设计来彻底拆解这个问题。
         *
         * ---
         *
         * ### 📘 1. 核心机制：互斥的路径集合
         *
         * 在 Spring Boot 的配置加载器（`ConfigDataEnvironmentPostProcessor` 或旧版的 `ConfigFileApplicationListener`）中，维护了两个核心概念：
         *
         * 1.  **Default Locations (默认位置):**
         *     硬编码在源码里的列表：
         *     *   `classpath:/`
         *     *   `classpath:/config/`
         *     *   `file:./`
         *     *   `file:./config/`
         * 2.  **Explicit Location (显式位置):**
         *     用户通过命令行 `--spring.config.location` 传入的值。
         *
         * #### 底层逻辑伪代码
         * Spring Boot 在初始化环境时，逻辑大致如下（简化版）：
         *
         * ```java
         * // 伪代码演示加载逻辑
         * List<String> locationsToSearch;
         *
         * if (System.getProperty("spring.config.location") != null) {
         *     // ⚠️ 关键点：如果有显式指定 location，直接赋值，完全抛弃默认值
         *     locationsToSearch = parse(System.getProperty("spring.config.location"));
         * } else {
         *     // 只有在没指定 location 时，才使用默认的 4 个路径
         *     locationsToSearch = Arrays.asList(
         *         "file:./config/",
         *         "file:./",
         *         "classpath:/config/",
         *         "classpath:/"
         *     );
         * }
         *
         * // 接下来只在 locationsToSearch 列表里找文件
         * loadConfigs(locationsToSearch);
         * ```
         *
         * **这就是“失效”的根源：** 默认路径从未被加入到待搜索的集合中，所以原来放在 `src/main/resources/application.yml` 里的配置根本没机会被加载。
         *
         * ---
         *
         * ### 💻 2. 解决方案：如何实现“追加”而非“替换”？
         *
         * 如果你希望保留默认的 classpath 配置（作为兜底），同时引入外部配置（作为覆盖），Spring Boot 提供了另一个参数。
         *
         * #### ✅ 方案 A：使用 `spring.config.additional-location`
         *
         * 这是 Spring Boot 2.0 引入的神器。
         *
         * *   **含义：** 额外的搜索路径。
         * *   **逻辑：** `Effective Locations = Additional Locations + Default Locations`。
         * *   **优先级：** `additional-location` 加载的配置优先级**高于**默认配置（会覆盖同名 Key），但不会导致默认文件失效。
         *
         * **命令示例：**
         * ```bash
         * java -jar app.jar --spring.config.additional-location=/etc/myapp/custom.yml
         * ```
         *
         * #### ✅ 方案 B：使用 `spring.config.import` (Spring Boot 2.4+ 推荐)
         *
         * 在 Spring Boot 2.4 重构配置加载机制后，推荐在 `application.yml` 内部使用 import 机制。
         *
         * **在你的 `src/main/resources/application.yml` 中：**
         * ```yaml
         * # 这里的 import 就像编程语言的 import 一样
         * # 如果文件不存在，可以使用 optional: 前缀忽略报错
         * spring:
         *   config:
         *     import: "optional:file:/etc/myapp/prod-config.yml"
         * ```
         * 这种方式最优雅，因为它把“我要加载外部文件”这个意图显式地写在了默认配置里。
         *
         * ---
         *
         * ### 🚀 3. 进阶：配置优先级的“俄罗斯套娃”
         *
         * 为了让你更透彻地理解，我们需要区分 **“文件级覆盖”** 和 **“属性级覆盖”**。
         *
         * 1.  **Location Overriding (文件级):**
         *     *   `spring.config.location`: 只有你指定的路径生效。
         *     *   `spring.config.additional-location`: 你指定的路径 + 默认路径都生效。
         *
         * 2.  **Property Overriding (属性级):**
         *     当多个文件都被加载时（例如使用了 `additional-location`），如果它们有相同的 key（比如 `server.port`），谁赢？
         *
         *     **规则：后加载的覆盖先加载的。**
         *     Spring Boot 的加载顺序设计是：
         *     1.  先加载默认路径（Classpath 等）。
         *     2.  再加载 `additional-location` 指定的路径。
         *
         *     **结果：** `additional-location` 里的值会覆盖 Classpath 里的值。这正是我们想要的——外部配置优先级高于内部默认配置。
         *
         * ---
         *
         * ### ⚠️ 总结与导师建议
         *
         * *   **现象：** 使用 `--spring.config.location` 导致 `classpath` 下的 `application.yml` 失效。
         * *   **原因：** 该参数的设计初衷是**“定义完全自定义的配置环境”**，因此它执行的是**替换（Replace）**逻辑。
         * *   **修正：**
         *     *   如果你想**完全接管**配置（例如测试环境，不想受项目内配置干扰），继续用 `location`。
         *     *   如果你想**扩展/覆盖**部分配置（例如生产环境修改数据库密码），**请务必使用 `--spring.config.additional-location`**。
         *
         * 记住这个公式：
         * > **`location` = 独裁 (Only Me)**
         * > **`additional-location` = 合作 (Me + You)**
         *
         * @formatter:on
         */

        // 06. Java 系统属性指定 spring.config.location
        // System.setProperty("spring.config.location", "classpath:/config/spring/application.yml");
        // 等同于系统环境变量设置.
        // 在 Log4j2 初始化前，强行注入全局异步配置;下方行代码的效果 等同于 log4j2.component.properties 文件里的内容
        System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");

        // 07. 来自 JNDI 的属性java:comp/env
        // 使用 JNDI (java:comp/env) 通常意味着你的应用是打包成 WAR 包并在外部容器（如 Tomcat、WebLogic、JBoss）中运行的，
        // 而不是作为独立的 JAR 包运行。配置方式依赖于外部容器类型,此处不做详细说明.

        // 08. ServletContext初始化参数指定
        // 在 web-app --> context-param --> <param-name>spring.config.location</param-name>中指定

        // 09. ServletConfig初始化参数指定
        // 在 web-app --> servlet --> init-param --> <param-name>spring.config.location</param-name>中指定

        // 10. SPRING_APPLICATION_JSON来自（嵌入在环境变量或系统属性中的内联 JSON）的属性。

        // 11. 命令行直接指定参数 spring.config.location, 效果等同于系统环境变量设置,但是优先级最高

        /**
         * @formatter:off
         * 
         * 12. 测试类中使用的内容:
         *      1. properties在测试中使用属性。可在测试@SpringBootTest注解中用于测试应用程序的特定部分。
         *      2. @DynamicPropertySource在测试中添加注解。
         *      3. @TestPropertySource在测试中添加注释。
         *      4. $HOME/.config/spring-bootdevtools处于活动状态时，目录中的devtools 全局设置属性。
         *
         * @formatter:on
         */

        // 外部配置加载示例 end
        app.run(args);
    }

}
