// File: component/handler/DataPermissionSqlBuilder.java
package thriving.softwood.common.security.handler;

import java.util.List;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import cn.hutool.v7.core.text.StrUtil;
import cn.hutool.v7.json.JSONUtil;
import thriving.softwood.common.core.pojo.dto.ConditionItemDTO;
import thriving.softwood.common.security.context.UserContext;
import thriving.softwood.common.security.pojo.dto.DataRuleDTO;
import thriving.softwood.common.security.spi.ResourceExtractor;

@Component
public class DataPermissionSqlBuilder {

    // 🌟 注入所有实现了 ResourceExtractor 接口的 Bean
    private final List<ResourceExtractor> extractors;

    public DataPermissionSqlBuilder(ObjectProvider<ResourceExtractor> extractorProvider) {
        extractors = extractorProvider.orderedStream().toList();
    }

    private static @NonNull StringBuilder getSqlFragmentBuilder(DataRuleDTO rule) {
        List<ConditionItemDTO> conditions = JSONUtil.toList(rule.getCustomSqlJson(), ConditionItemDTO.class);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < conditions.size(); i++) {
            ConditionItemDTO cond = conditions.get(i);
            if (!cond.getColumn().matches("^[a-zA-Z0-9_\\.]+$")) {
                throw new RuntimeException("检测到非法的数据列名");
            }
            String op = cond.getOperator().toUpperCase();
            if (!op.matches("^(>|<|=|>=|<=|IN|NOT IN)$")) {
                throw new RuntimeException("非法的逻辑操作符");
            }

            String rawValue = cond.getValue().replace("'", "");
            sb.append(cond.getColumn()).append(" ").append(op);

            if ("IN".equals(op) || "NOT IN".equals(op)) {
                String[] vals = rawValue.split(",");
                StringBuilder inSb = new StringBuilder("(");
                for (int j = 0; j < vals.length; j++) {
                    String item = vals[j].trim();
                    if (StrUtil.isNotBlank(item)) {
                        inSb.append("'").append(item.replace("'", "''")).append("'");
                        if (j < vals.length - 1) {
                            inSb.append(",");
                        }
                    }
                }
                inSb.append(")");
                sb.append(" ").append(inSb.toString());
            } else {
                sb.append(" '").append(rawValue.replace("'", "''")).append("'");
            }
            if (i < conditions.size() - 1) {
                sb.append(" AND ");
            }
        }
        return sb;
    }

    public String buildSqlSegment(String mappedStatementId) {
        if (UserContext.underSystemMode() || UserContext.userId() == null
            || Boolean.TRUE.equals(UserContext.godMode())) {
            return null;
        }

        // 🌟 遍历所有的 Extractor 寻找匹配的资源名
        String resourceName = null;
        for (ResourceExtractor extractor : extractors) {
            resourceName = extractor.extract(mappedStatementId);
            if (StrUtil.isNotBlank(resourceName)) {
                break;
            }
        }

        if (StrUtil.isBlank(resourceName)) {
            return null;
        }

        // 🌟 使用 DataRuleDTO
        List<DataRuleDTO> rules = UserContext.dataRules(resourceName);
        if (rules == null || rules.isEmpty()) {
            return null;
        }

        StringBuilder finalScopeSql = new StringBuilder();
        for (DataRuleDTO rule : rules) {
            if (rule.getScopeType() == 1) {
                return null;
            }

            String singleRuleSql = buildFragmentForRule(rule);
            if (StrUtil.isNotBlank(singleRuleSql)) {
                if (!finalScopeSql.isEmpty()) {
                    finalScopeSql.append(" OR ");
                }
                finalScopeSql.append("(").append(singleRuleSql).append(")");
            }
        }
        return finalScopeSql.isEmpty() ? null : finalScopeSql.toString();
    }

    private String buildFragmentForRule(DataRuleDTO rule) {
        String employeeTypeId = UserContext.employeeTypeId();
        String departmentTypeId = UserContext.departmentTypeId();

        switch (rule.getScopeType()) {
            case 2:
                return "a.etypeid = '" + employeeTypeId + "'";
            case 3:
                return "a.projectid = '" + departmentTypeId + "'";
            case 4:
                return "a.projectid IN (SELECT typeid FROM department WHERE parid ='" + departmentTypeId
                    + "' OR typeid = '" + departmentTypeId + "')";
            case 5:
                if (StrUtil.isNotBlank(rule.getCustomSqlJson())) {
                    return getSqlFragmentBuilder(rule).toString();
                }
        }
        return null;
    }
}