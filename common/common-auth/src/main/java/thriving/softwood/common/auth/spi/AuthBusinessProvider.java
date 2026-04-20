// File: biz/spi/AuthBusinessProvider.java
package thriving.softwood.common.auth.spi;

import java.util.Map;

import thriving.softwood.common.auth.pojo.record.DepartmentReq;

/**
 * 🚀 身份管理业务回调 SPI 让外部业务模块（如 kaishi）向 common-auth 注入特定的业务扩展数据
 */
public interface AuthBusinessProvider {
    /** 根据登录账号获取对应的业务人员 ID (如 ERP 的 EmployeeTypeId) */
    default String getEmployeeTypeId(String loginAccount) {
        return null;
    }

    /** 根据部门 ID 获取对应的业务部门 ID (如 ERP 的 DepartmentTypeId) */
    default String getDepartmentTypeId(Long deptId) {
        return null;
    }

    void associateDelete(Long userId);

    void associateDelete(DepartmentReq req);

    Map<Long, String> loadAssocDeptIds();
}