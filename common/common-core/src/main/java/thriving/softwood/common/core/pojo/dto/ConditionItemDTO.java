package thriving.softwood.common.core.pojo.dto;

import java.io.Serial;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConditionItemDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String column;
    private String operator;
    private String value;
}
