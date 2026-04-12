package thriving.softwood.common.core.pojo.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class SortItemDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String field;
    /** true: asc, false: desc */
    private Boolean flag;
}