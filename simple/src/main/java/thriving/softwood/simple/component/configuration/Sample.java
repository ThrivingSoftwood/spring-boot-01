package thriving.softwood.simple.component.configuration;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author ThrivingSoftwood ConfigurationProperties 支持 JSR-303 数据校验,但是需要引入对应的实现依赖
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "sample")
@Validated
@PropertySource("classpath:/config/data/sample.properties")
public class Sample {
    Integer id;
    String name;
    List<String> hobbies;
    List<String> pets;
    Map<Integer, String> ageWithEvent;
    Map<Integer, String> ageWithGirl;
    @NotBlank
    String testNotNull;
}
