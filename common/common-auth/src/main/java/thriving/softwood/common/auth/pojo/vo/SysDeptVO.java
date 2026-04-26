package thriving.softwood.common.auth.pojo.vo;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import thriving.softwood.common.auth.infrastructure.db.master.entity.base.SysDept;

/**
 * <p>
 * 
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-15
 */
@Data
public class SysDeptVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long parentId;
    private String ancestors;
    private String deptName;
    private Integer sortOrder;
    private Byte status;
    private String extInfo;
    private String oriDepartmentTypeid;

    // 🌟 核心：子节点集合，用于前端 Element Plus 的树形表格渲染
    private List<SysDeptVO> children = new ArrayList<>();

    public SysDeptVO(SysDept dept, String oriDepartmentTypeid) {
        id = dept.getId();
        parentId = dept.getParentId();
        ancestors = dept.getAncestors();
        deptName = dept.getDeptName();
        sortOrder = dept.getSortOrder();
        status = dept.getStatus();
        extInfo = dept.getExtInfo();
        this.oriDepartmentTypeid = oriDepartmentTypeid;
    }
}
