// UserReq.java - 用户操作请求体
package thriving.softwood.common.auth.pojo.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class UserDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    protected Long id;
    protected String loginAccount;
    protected String username;
    protected Long deptId;
    protected Byte status;
    protected String newPasswordEnc;
    protected String extInfo;
    protected List<Long> roleIds;
}