package thriving.softwood.common.file.component.config;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 扩展配置
 *
 * @author ThrivingSoftwood
 * @since 2026-01-29
 */
@AutoConfiguration
public class FileWebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:${user.dir}/uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取你在 application.yml 中配置的上传根目录
        // 假设你的物理路径是 C:/uploads/ 或 /home/kaishi/uploads/
        String absolutePath = new File(uploadDir).getAbsolutePath();

        // 🌟 核心魔法：将虚拟路径映射到物理路径
        // 注意：物理路径必须以 "file:" 开头，且最后必须带斜杠
        String fileLocation = "file:" + absolutePath + File.separator;

        registry.addResourceHandler("/kaishi/file/preview/**").addResourceLocations(fileLocation).setCachePeriod(86400); // 顺便开启一天缓存，性能起飞
    }
}