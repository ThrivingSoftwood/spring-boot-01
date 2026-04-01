package thriving.softwood.common.framework.component.postprocessor;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import thriving.softwood.common.core.cryptor.Sm4StringEncryptor;
import thriving.softwood.common.core.util.Sm4Util;

public class Sm4EncryptionInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment env = applicationContext.getEnvironment();

        // 1. 直接从 Environment 获取原始的 Key 和 IV
        String key = env.getProperty("security.sm4.key");
        String iv = env.getProperty("security.sm4.iv");

        if (key == null || iv == null) {
            throw new IllegalStateException("SM4 Init Error: Security keys missing in environment!");
        }

        // 2. 实例化加密器
        Sm4StringEncryptor encryptor = new Sm4StringEncryptor(key, iv);

        // 3. 将加密器手动注册到 BeanFactory，确保 Jasypt 能够通过名字找到它
        applicationContext.getBeanFactory().registerSingleton("jasyptStringEncryptor", encryptor);
        Sm4Util.init(key, iv);
    }
}