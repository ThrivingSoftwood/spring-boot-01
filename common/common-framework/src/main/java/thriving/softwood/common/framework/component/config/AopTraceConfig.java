package thriving.softwood.common.framework.component.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

import thriving.softwood.common.framework.component.aspect.TraceAspect;

/**
 * @author ThrivingSoftwood
 * @since version 2026-01-23
 */
@AutoConfiguration
@Import(TraceAspect.class) // 显式导入切面
public class AopTraceConfig {}