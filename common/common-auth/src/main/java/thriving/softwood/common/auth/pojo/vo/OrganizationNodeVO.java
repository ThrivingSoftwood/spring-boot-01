// OrgNodeVO.java - 混合架构树节点
package thriving.softwood.common.auth.pojo.vo;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;

/**
 * 锚定默认实现
 * 
 * @author ThrivingSoftwood
 */
@Data
public class OrganizationNodeVO extends AncestorOrganizationNodeVO<OrganizationNodeVO> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}