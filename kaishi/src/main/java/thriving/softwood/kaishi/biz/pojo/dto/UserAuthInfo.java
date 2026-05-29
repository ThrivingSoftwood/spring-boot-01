package thriving.softwood.kaishi.biz.pojo.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import thriving.softwood.common.security.pojo.dto.UserAuthInfoDTO;

/**
 * 用户全链路权限上下文 DTO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserAuthInfo extends UserAuthInfoDTO implements Serializable {
    private static final long serialVersionUID = 1L;
}