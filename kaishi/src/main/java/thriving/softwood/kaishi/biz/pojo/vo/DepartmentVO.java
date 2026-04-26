package thriving.softwood.kaishi.biz.pojo.vo;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import thriving.softwood.kaishi.infrastructure.db.kaishi2026.entity.base.Department;

/**
 * <p>
 * 
 * </p>
 *
 * @author ThrivingSoftwood
 * @since 2026-04-06
 */
@Data
public class DepartmentVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private String typeid;
    private String parid;
    private String fullName;
    private boolean synced;

    // 🌟 核心：子节点集合，用于前端 Element Plus 的树形表格渲染
    private List<DepartmentVO> children = new ArrayList<>();

    public DepartmentVO(Department department, Boolean synced) {
        typeid = department.getTypeid();
        parid = department.getParid();
        fullName = department.getFullName();
        this.synced = synced;
    }

}
