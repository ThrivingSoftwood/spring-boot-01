// File: ./kaishi/src/main/java/thriving/softwood/kaishi/component/annotation/ReplacePlaceholder.java
package thriving.softwood.kaishi.component.annotation;

import java.lang.annotation.*;

/**
 * 🚀 标记该 Mapper 方法使用字符串替换的方式注入数据权限 适用于 JSqlParser 无法解析的复杂方言 SQL
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ReplacePlaceholder {
    /** 占位符字符串，在 XML 中书写，默认如下 */
    String value() default "/*_DATA_PERMISSION_*/";
}