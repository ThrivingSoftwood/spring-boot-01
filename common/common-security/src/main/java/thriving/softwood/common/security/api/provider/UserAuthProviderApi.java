package thriving.softwood.common.security.api.provider;

import thriving.softwood.common.security.pojo.dto.UserAuthInfoDTO;

/**
 * 身份数据提供者接口 (SPI) 由 common-auth 模块实现，提供从 DB/Cache 获取用户实时权限的能力
 */
public interface UserAuthProviderApi {
    /**
     * 获取用户全链路权限上下文
     */
    UserAuthInfoDTO getAuthInfo(Long userId);
}