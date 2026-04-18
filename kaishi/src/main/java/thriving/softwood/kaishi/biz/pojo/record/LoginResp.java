package thriving.softwood.kaishi.biz.pojo.record;

import java.util.Set;

public record LoginResp(String token, String username, Set<String> permissions) {
}