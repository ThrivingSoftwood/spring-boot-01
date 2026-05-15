// File: ./cust0001/src/main/java/thriving/softwood/cust0001/biz/pojo/record/sync/ErpSyncReq.java
package thriving.softwood.customer.first.biz.pojo.record;

import java.util.List;

public record ErpSyncReq(List<String> erpEmployeeTypeIds, String newPasswordEnc, List<String> deptTypeIds) {
}