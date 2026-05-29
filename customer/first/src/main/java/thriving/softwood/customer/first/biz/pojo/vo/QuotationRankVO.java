package thriving.softwood.customer.first.biz.pojo.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 动态排名视图对象
 */
@Data
public class QuotationRankVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String supplierCode;
    private String productCode;
    private BigDecimal unitPrice;
    private String updateTime;

    // 🌟 核心：由数据库 ROW_NUMBER() 动态生成的排名
    private Integer rankNum;
}