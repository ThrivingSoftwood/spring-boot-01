package thriving.softwood.customer.first.biz.pojo.dto;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;
import thriving.softwood.customer.first.infrastructure.db.edongfang.entity.base.EdongfangProductParams;

@Data
public class EdongfangProductParamDTO extends EdongfangProductParams implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer dealType;
}
