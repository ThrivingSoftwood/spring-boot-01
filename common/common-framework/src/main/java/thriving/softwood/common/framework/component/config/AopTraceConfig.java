package thriving.softwood.common.framework.component.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

import thriving.softwood.common.framework.component.aspect.AsyncTraceAspect;
import thriving.softwood.common.framework.component.aspect.ScheduleTraceAspect;

/**
 * @author ThrivingSoftwood
 * @since version 2026-01-23
 */
@AutoConfiguration
// 显式导入切面
@Import({AsyncTraceAspect.class, ScheduleTraceAspect.class})
public class AopTraceConfig {}