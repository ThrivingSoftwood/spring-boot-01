package thriving.softwood.common.framework.component.postprocessor;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import cn.hutool.v7.core.text.StrUtil;
import thriving.softwood.common.core.cryptor.Sm4StringEncryptor;
import thriving.softwood.common.core.util.JwtUtil;
import thriving.softwood.common.core.util.Sm4Util;

public class SecurityInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment env = applicationContext.getEnvironment();

        // 1. 直接从 Environment 获取本地使用的 Key 和 IV
        String key = env.getProperty("security.local.key");
        String iv = env.getProperty("security.local.iv");

        if (StrUtil.isBlank(key) || StrUtil.isBlank(iv)) {
            throw new IllegalStateException("Local SM4 Init Error: Security keys missing in environment!");
        }

        // 2. 实例化加密器
        Sm4StringEncryptor encryptor = new Sm4StringEncryptor(key, iv);

        // 3. 将加密器手动注册到 BeanFactory，确保 Jasypt 能够通过名字找到它
        applicationContext.getBeanFactory().registerSingleton("jasyptStringEncryptor", encryptor);
        Sm4Util.initLocal(key, iv);

        // 4. 获取前端正在使用的 key 和 iv 并初始化
        key = env.getProperty("security.web.key");
        iv = env.getProperty("security.web.iv");
        if (StrUtil.isBlank(key) || StrUtil.isBlank(iv)) {
            throw new IllegalStateException("Web SM4 Init Error: Security keys missing in environment!");
        }
        Sm4Util.initWeb(key, iv);

        // 5. jwtUtil 初始化
        String publicKeyBase64 = env.getProperty("security.jwt.public-key-base64");
        String privateKeyBase64 = env.getProperty("security.jwt.private-key-base64");
        if (StrUtil.isBlank(publicKeyBase64) || StrUtil.isBlank(privateKeyBase64)) {
            throw new IllegalStateException("JWT Key Init Error: Security keys missing in environment!");
        }
        JwtUtil.init(privateKeyBase64, publicKeyBase64);
    }
}