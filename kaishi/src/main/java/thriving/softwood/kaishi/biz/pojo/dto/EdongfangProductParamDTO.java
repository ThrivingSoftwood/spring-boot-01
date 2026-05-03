package thriving.softwood.kaishi.biz.pojo.dto;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;
import thriving.softwood.kaishi.infrastructure.db.edongfang.entity.base.EdongfangProductParams;

@Data
public class EdongfangProductParamDTO extends EdongfangProductParams implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer dealType;
}
