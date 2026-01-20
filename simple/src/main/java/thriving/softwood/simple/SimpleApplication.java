package thriving.softwood.simple;

import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ThrivingSoftwood
 */
@SpringBootApplication
public class SimpleApplication {

    static void main(String[] args) {
        // 🚀 CodeOmni 核心操作 1：在 Log4j2 初始化前，强行注入全局异步配置
        // 这行代码的效果 等同于 log4j2.component.properties 文件里的内容
        System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
        SpringApplication ctx = new SpringApplication(SimpleApplication.class);
        Properties props = new Properties();
        props.setProperty("spring.config.location", "classpath:/configuration/spring/");
        ctx.setDefaultProperties(props);
        ctx.run(args);
    }

}
