// File: ./kaishi/src/main/java/thriving/softwood/kaishi/component/handler/DataPermissionSqlBuilder.java
package thriving.softwood.kaishi.component.handler;

import java.util.List;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import cn.hutool.v7.core.text.StrUtil;
import cn.hutool.v7.json.JSONUtil;
import thriving.softwood.common.core.pojo.dto.ConditionItemDTO;
import thriving.softwood.kaishi.context.UserContext;
import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysDataRule;

/**
 * 🚀 数据权限 SQL 片段生成引擎（纯字符串输出，与解析器解耦）
 */
@Component
public class DataPermissionSqlBuilder {

    /**
     * 生成权限 SQL 字符串，如果不拦截则返回 null
     */
    public String buildSqlSegment(String mappedStatementId) {
        // 1. 系统模式、未登录、上帝模式直接放行
        if (UserContext.underSystemMode() || UserContext.userId() == null
            || Boolean.TRUE.equals(UserContext.godMode())) {
            return null;
        }

        String resourceName = extractResourceName(mappedStatementId);
        if (StrUtil.isBlank(resourceName)) {
            return null;
        }

        List<SysDataRule> rules = UserContext.dataRules(resourceName);
        if (rules == null || rules.isEmpty()) {
            return null;
        }

        StringBuilder finalScopeSql = new StringBuilder();
        for (SysDataRule rule : rules) {
            if (rule.getScopeType() == 1) {
                return null; // 全部数据，直接放行
            }

            String singleRuleSql = buildFragmentForRule(rule);
            if (StrUtil.isNotBlank(singleRuleSql)) {
                if (!finalScopeSql.isEmpty()) {
                    finalScopeSql.append(" OR ");
                }
                finalScopeSql.append("(").append(singleRuleSql).append(")");
            }
        }

        if (finalScopeSql.isEmpty()) {
            return null;
        }
        return finalScopeSql.toString();
    }

    private String buildFragmentForRule(SysDataRule rule) {
        String employeeTypeId = UserContext.employeeTypeId(); // 需确保 UserContext 中有此方法
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

    private static @NonNull StringBuilder getSqlFragmentBuilder(SysDataRule rule) {
        List<ConditionItemDTO> conditions = JSONUtil.toList(rule.getCustomSqlJson(), ConditionItemDTO.class);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < conditions.size(); i++) {
            ConditionItemDTO cond = conditions.get(i);

            // 1. 列名与操作符校验（保持原有的防注入策略）
            if (!cond.getColumn().matches("^[a-zA-Z0-9_\\.]+$")) {
                throw new RuntimeException("检测到非法的数据列名");
            }
            String op = cond.getOperator().toUpperCase();
            if (!op.matches("^(>|<|=|>=|<=|IN|NOT IN)$")) {
                throw new RuntimeException("非法的逻辑操作符");
            }

            // 🌟 2. 核心修复：预处理 Value
            // 先去掉用户可能误传的所有单引号，再进行转义，防止重复转义
            String rawValue = cond.getValue().replace("'", "");

            sb.append(cond.getColumn()).append(" ").append(op);

            if ("IN".equals(op) || "NOT IN".equals(op)) {
                // 3. 处理 IN 逻辑：按逗号拆分，并为每个元素包裹单引号
                String[] vals = rawValue.split(",");
                StringBuilder inSb = new StringBuilder("(");
                for (int j = 0; j < vals.length; j++) {
                    String item = vals[j].trim();
                    if (StrUtil.isNotBlank(item)) {
                        // SQL Server 标准转义：replace("'", "''") 依然保留以防万一
                        inSb.append("'").append(item.replace("'", "''")).append("'");
                        if (j < vals.length - 1) {
                            inSb.append(",");
                        }
                    }
                }
                inSb.append(")");
                sb.append(" ").append(inSb.toString());
            } else {
                // 4. 处理单值逻辑：直接包裹
                sb.append(" '").append(rawValue.replace("'", "''")).append("'");
            }

            if (i < conditions.size() - 1) {
                sb.append(" AND ");
            }
        }
        return sb;
    }

    /**
     * 获取目标表 todo 后续有需要扩展成字典表
     * 
     * @param mappedStatementId
     * @return
     */
    private String extractResourceName(String mappedStatementId) {
        if (mappedStatementId.contains("DlyBuyExtendMapper")) {
            return "DlyBuy";
        }
        return null;
    }
}