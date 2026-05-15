package thriving.softwood.customer.first.biz.pojo.dto;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import thriving.softwood.customer.first.infrastructure.db.kaishi2026.entity.base.Dlyndx;

@Data
@NoArgsConstructor
public class DlyndxDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private Long vchcode;
    private String number;
    private String summary;

    public DlyndxDTO(Dlyndx dlyndx) {
        if (null == dlyndx) {
            throw new IllegalArgumentException("进销存索引对象为空! is null");
        }
        vchcode = dlyndx.getVchcode();
        number = dlyndx.getNumber();
        summary = dlyndx.getSummary();
    }
}
