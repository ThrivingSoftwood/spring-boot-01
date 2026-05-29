package thriving.softwood.common.security.pojo.vo;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SysDeptVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    protected Long id;
    protected Long parentId;
    protected String ancestors;
    protected String deptName;
    protected Integer sortOrder;
    protected Byte status;
    protected String extInfo;

    protected List<SysDeptVO> children = new ArrayList<>();

    public SysDeptVO(SysDeptVO other) {
        this.id = other.id;
        this.parentId = other.parentId;
        this.ancestors = other.ancestors;
        this.deptName = other.deptName;
        this.sortOrder = other.sortOrder;
        this.status = other.status;
        this.extInfo = other.extInfo;
        this.children = other.children;
    }
}
