package thriving.softwood.kaishi.biz.pojo.vo;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysDataRule;

/**
 * <p>
 * 
 * </p>
 *
 * @author meta-thriving
 * @since 2026-04-17
 */
@Data
@NoArgsConstructor
public class SysDataRuleVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public SysDataRuleVO(SysDataRule sysDataRule) {
        this.id = sysDataRule.getId();
        this.targetResource = sysDataRule.getTargetResource();
        this.ruleName = sysDataRule.getRuleName();
        this.scopeType = sysDataRule.getScopeType();
        this.customSqlJson = sysDataRule.getCustomSqlJson();
        this.extInfo = sysDataRule.getExtInfo();
    }

    private Long id;
    private String targetResource;
    private String ruleName;
    private Byte scopeType;
    private String customSqlJson;
    private String extInfo;
}
