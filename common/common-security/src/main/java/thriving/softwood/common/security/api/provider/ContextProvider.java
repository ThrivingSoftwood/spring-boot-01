// File: biz/service/AuthDataProviderImpl.java
package thriving.softwood.common.security.api.provider;

import org.springframework.stereotype.Service;

import thriving.softwood.common.security.context.UserContext;
import thriving.softwood.common.web.spi.UserContextProvider;

/**
 * 🚀 核心大脑：实现 common-security 的数据提供者接口
 */
@Service
public class ContextProvider implements UserContextProvider {

    @Override
    public Long getUserId() {
        return UserContext.userId();
    }
}