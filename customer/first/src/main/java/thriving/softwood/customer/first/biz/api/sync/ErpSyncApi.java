package thriving.softwood.customer.first.biz.api.sync;

import java.util.List;

import thriving.softwood.customer.first.biz.pojo.record.ErpSyncReq;
import thriving.softwood.customer.first.biz.pojo.vo.DepartmentVO;
import thriving.softwood.customer.first.biz.pojo.vo.OrgNodeVO;

public interface ErpSyncApi {
    void syncUsers(ErpSyncReq req);

    void syncDepartments(List<String> typeIds);

    List<OrgNodeVO> treeUnsyncedUsers();

    List<DepartmentVO> treeUnsyncedDepartments();
}
