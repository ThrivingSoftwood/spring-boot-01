package thriving.softwood.common.auth.pojo.record;

public record DataRuleReq(Long id, String targetResource, String ruleName, Integer scopeType, String customSqlJson) {
}