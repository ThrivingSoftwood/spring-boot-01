// OrgNodeVO.java - 混合架构树节点
package thriving.softwood.common.auth.pojo.vo;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class AncestorOrganizationNodeVO<T extends AncestorOrganizationNodeVO<T>> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 统一转为 String (兼容本地 Long 和 ERP String)
     */
    protected String id;
    protected String parentId;
    /**
     * 部门名 或 员工姓名
     */
    protected String name;
    /**
     * 1: 部门, 2: 用户
     */
    protected Integer nodeType;
    /** 状态 */
    protected Byte status;
    /** 扩展标识 (本地的 loginAccount 或 ERP 的 UserCode) **/
    protected String loginCode;
    /** 前端控制：是否禁止勾选 */
    protected boolean disabled;
    /** 新增：该节点（或部门下）的用户数 */
    protected Integer userCount = 0;

    protected List<T> children = new ArrayList<>();
}