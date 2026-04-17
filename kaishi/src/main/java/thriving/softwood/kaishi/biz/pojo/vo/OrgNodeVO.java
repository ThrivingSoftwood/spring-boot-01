// OrgNodeVO.java - 混合架构树节点
package thriving.softwood.kaishi.biz.pojo.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class OrgNodeVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 统一转为 String (兼容本地 Long 和 ERP String)
     */
    private String id;
    private String parentId;
    /**
     * 部门名 或 员工姓名
     */
    private String name;
    /**
     * 1: 部门, 2: 用户
     */
    private Integer nodeType;
    /** 状态 */
    private Byte status;
    /** 扩展标识 (本地的 loginAccount 或 ERP 的 UserCode) **/
    private String extCode;
    /** ERP 的 typeid (仅用于前端核对) */
    private String sourceId;
    /** 前端控制：是否禁止勾选 */
    private boolean disabled;
    /** 新增：该节点（或部门下）的用户数 */
    private Integer userCount = 0;

    private List<OrgNodeVO> children = new ArrayList<>();
}