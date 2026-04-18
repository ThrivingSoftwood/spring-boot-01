package thriving.softwood.kaishi.biz.api.system;

import java.util.List;

import thriving.softwood.kaishi.biz.pojo.record.DataRuleReq;
import thriving.softwood.kaishi.biz.pojo.vo.SysDataRuleVO;

public interface DataRuleApi {

    List<SysDataRuleVO> allDataRules();

    void saveOrUpdate(DataRuleReq req);

    void logicDelete(Long dataRuleId);

}
