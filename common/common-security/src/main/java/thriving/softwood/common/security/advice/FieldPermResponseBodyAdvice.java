// File:
// ./common/common-web/src/main/java/thriving/softwood/common/web/component/advice/FieldPermResponseBodyAdvice.java
package thriving.softwood.common.security.advice;

import java.lang.reflect.Field;
import java.util.Collection;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import cn.hutool.v7.core.reflect.FieldUtil;
import thriving.softwood.common.core.result.Result;
import thriving.softwood.common.security.annotation.FieldPerm;
import thriving.softwood.common.security.context.UserContext;

/**
 * 🚀 独立于底层 JSON 框架的终极字段级权限擦除器
 *
 * 作用：在 Spring MVC 将 Controller 返回的 Java 对象交给 HttpMessageConverter (如 Jackson) 序列化之前， 扫描对象上的 @FieldPerm
 * 注解，若当前用户无此权限，则利用反射将该字段物理置为 null，彻底防止抓包泄露。
 *
 * @author CodeOmni
 */
@RestControllerAdvice
public class FieldPermResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 🛡️ 精准拦截：只拦截外层包装为统一 Result 对象的响应
        return Result.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
        Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
        ServerHttpResponse response) {

        if (body instanceof Result<?> result && result.getData() != null) {

            // 🌟 1. 上帝模式绝对放行，跳过反射，保证最高性能
            if (Boolean.TRUE.equals(UserContext.godMode())) {
                return body;
            }

            Object data = result.getData();

            // 🌟 2. 智能分发：处理集合或单体对象
            if (data instanceof Collection<?> coll) {
                for (Object item : coll) {
                    processFields(item);
                }
            } else {
                processFields(data);
            }
        }
        return body;
    }

    /**
     * 利用 Hutool V7 反射清理无权限字段
     */
    private void processFields(Object obj) {
        if (obj == null) {
            return;
        }

        // Hutool V7: 获取该类的所有字段（包含父类继承下来的字段），内部带有缓存机制，性能极高
        Field[] fields = FieldUtil.getFields(obj.getClass());

        for (Field field : fields) {
            // 🌟 3. 检查是否有我们定义的 @FieldPerm 注解
            FieldPerm permAnnotation = field.getAnnotation(FieldPerm.class);
            if (permAnnotation != null) {
                String requiredPermCode = permAnnotation.value();

                // 🌟 4. 如果当前用户的权限集合中不包含这个 requiredPermCode，则物理置空
                if (UserContext.permissions() == null || !UserContext.permissions().contains(requiredPermCode)) {
                    // Hutool V7: 安全无感地将对象该字段设为 null，无视 private 修饰符
                    FieldUtil.setFieldValue(obj, field, null);
                }
            }
        }
    }
}