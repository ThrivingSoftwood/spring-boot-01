// File: pojo/dto/DataRuleDTO.java
package thriving.softwood.common.security.pojo.dto;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * 纯净的数据规则载体 (解耦 SysDataRule 实体)
 */
@Data
public class DataRuleDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String targetResource;
    private Byte scopeType;
    private String customSqlJson;
}