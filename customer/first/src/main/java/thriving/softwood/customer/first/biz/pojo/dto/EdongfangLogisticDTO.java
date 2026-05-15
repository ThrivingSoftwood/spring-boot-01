package thriving.softwood.customer.first.biz.pojo.dto;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;
import thriving.softwood.customer.first.infrastructure.db.edongfang.entity.base.EdongfangLogistics;

@Data
public class EdongfangLogisticDTO extends EdongfangLogistics implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long stubId;
}
