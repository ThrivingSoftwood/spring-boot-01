package thriving.softwood.sample.pojo.vo;

import java.io.Serial;
import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AncestorVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String traceId;
    private String message;
    private Long timestamp;
}