package thriving.softwood.common.auth.pojo.dto;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;

@Data
public class DepartmentDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    protected Long id;
    protected Long parentId;
    protected String deptName;
    protected Integer sortOrder;
    protected Byte status;
    protected String extInfo;
}