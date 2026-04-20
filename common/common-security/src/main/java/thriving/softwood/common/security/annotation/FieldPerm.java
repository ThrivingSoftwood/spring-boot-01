// File: ./common/common-core/src/main/java/thriving/softwood/common/core/annotation/FieldPerm.java
package thriving.softwood.common.security.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface FieldPerm {
    String value();
}