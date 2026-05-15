// OrgNodeVO.java - 混合架构树节点
package thriving.softwood.customer.first.biz.pojo.vo;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;
import thriving.softwood.common.auth.pojo.vo.AncestorOrganizationNodeVO;

@Data
public class OrgNodeVO extends AncestorOrganizationNodeVO<OrgNodeVO> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /** ERP 的 typeid (仅用于前端核对) */
    private String sourceId;
}