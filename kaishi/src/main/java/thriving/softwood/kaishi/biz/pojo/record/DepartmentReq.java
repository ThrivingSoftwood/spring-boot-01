package thriving.softwood.kaishi.biz.pojo.record;

import java.util.List;

public record DepartmentReq(Long id, Integer sortOrder, Byte status, List<String> typeIds) {
}