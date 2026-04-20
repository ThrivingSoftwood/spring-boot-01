package thriving.softwood.common.auth.api.system;

import java.util.List;

import thriving.softwood.common.auth.pojo.record.DataRuleReq;
import thriving.softwood.common.auth.pojo.vo.SysDataRuleVO;

public interface DataRuleApi {

    List<SysDataRuleVO> allDataRules();

    void saveOrUpdate(DataRuleReq req);

    void logicDelete(Long dataRuleId);

}
