// File: ./kaishi/src/main/java/thriving/softwood/kaishi/biz/pojo/record/sync/ErpSyncReq.java
package thriving.softwood.kaishi.biz.pojo.record;

import java.util.List;

public record ErpSyncReq(List<String> erpEmployeeTypeIds, String newPasswordEnc, List<String> deptTypeIds) {
}