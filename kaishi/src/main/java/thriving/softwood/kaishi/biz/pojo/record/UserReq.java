// UserReq.java - 用户操作请求体
package thriving.softwood.kaishi.biz.pojo.record;

import java.util.List;

public record UserReq(Long id, Long deptId, Byte status, String newPasswordEnc, List<String> erpEmployeeTypeIds) {
}