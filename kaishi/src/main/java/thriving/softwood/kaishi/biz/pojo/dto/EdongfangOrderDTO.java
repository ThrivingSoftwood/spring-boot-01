package thriving.softwood.kaishi.biz.pojo.dto;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;
import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangOrders;

@Data
public class EdongfangOrderDTO extends EdongfangOrders implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long stubId;
}
