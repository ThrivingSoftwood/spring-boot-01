package thriving.softwood.common.framework.component.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

import thriving.softwood.common.framework.component.aspect.TraceAspect;

@AutoConfiguration
@Import(TraceAspect.class) // 显式导入切面
public class AopTraceConfig {}