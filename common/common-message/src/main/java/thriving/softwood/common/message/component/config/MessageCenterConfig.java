package thriving.softwood.common.message.component.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * 消息中心自动配置类 开启后将自动扫描该模块下的 SseConnectionManager, SysMessageSvc 等
 */
@AutoConfiguration
@ComponentScan("thriving.softwood.common.message")
public class MessageCenterConfig {}