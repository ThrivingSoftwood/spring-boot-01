package thriving.softwood.kaishi.biz.api.sync;

import java.util.List;

import thriving.softwood.kaishi.biz.pojo.record.ErpSyncReq;
import thriving.softwood.kaishi.biz.pojo.vo.DepartmentVO;
import thriving.softwood.kaishi.biz.pojo.vo.OrgNodeVO;

public interface ErpSyncApi {
    void syncUsers(ErpSyncReq req);

    void syncDepartments(List<String> typeIds);

    List<OrgNodeVO> treeUnsyncedUsers();

    List<DepartmentVO> treeUnsyncedDepartments();
}
