package thriving.softwood.kaishi.biz.pojo.record;

public record DataRuleReq(Long id, String targetResource, String ruleName, Integer scopeType, String customSqlJson) {
}