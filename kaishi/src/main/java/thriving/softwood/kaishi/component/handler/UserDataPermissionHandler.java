package thriving.softwood.kaishi.component.handler;

import java.util.List;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.extension.plugins.handler.DataPermissionHandler;

import cn.hutool.v7.core.text.StrUtil;
import cn.hutool.v7.json.JSONUtil;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import thriving.softwood.common.core.pojo.dto.ConditionItemDTO;
import thriving.softwood.kaishi.context.UserContext;
import thriving.softwood.kaishi.infrastructure.db.master.entity.base.SysDataRule;

@Component
public class UserDataPermissionHandler implements DataPermissionHandler {

    @Override
    public Expression getSqlSegment(Expression where, String mappedStatementId) {
        // 1. 上帝模式放行 (超级管理员看所有数据)
        if (UserContext.godMode()) {
            return where;
        }

        // 2. 根据 mappedStatementId 推断资源名 (这里以 DlyBuyExtendMapper 为例)
        String resourceName = extractResourceName(mappedStatementId);
        if (StrUtil.isBlank(resourceName)) {
            return where;
        }

        // 3. 从上下文(缓存)获取该用户在此资源上的所有数据规则
        // 注意：一个用户可能有多个角色，所以可能是个规则列表 (List<SysDataRule>)
        List<SysDataRule> rules = UserContext.dataRules(resourceName);
        if (rules == null || rules.isEmpty()) {
            return where; // 无规则拦截，看业务要求是“默认全放开”还是“默认全拒绝”。这里演示全放开。
        }

        try {
            Expression finalScopeExpr = null;

            // 4. 遍历所有规则，将其翻译为 AST 表达式，并用 OR 连接 (多角色权限叠加)
            for (SysDataRule rule : rules) {
                Expression singleRuleExpr = buildExpressionForRule(rule);
                if (singleRuleExpr != null) {
                    if (finalScopeExpr == null) {
                        finalScopeExpr = singleRuleExpr;
                    } else {
                        // 多个规则用 OR 连接（例如：既能看本部门，又能看特定仓库）
                        finalScopeExpr = new OrExpression(finalScopeExpr, singleRuleExpr);
                    }
                } else if (rule.getScopeType() == 1) {
                    // 如果存在 scope_type = 1 (全部数据)，直接短路返回，拥有最高权限
                    return where;
                }
            }

            // 5. 将原生 WHERE 与 权限过滤条件用 AND 连接
            // 例如: WHERE (原条件) AND ( 权限条件A OR 权限条件B )
            if (finalScopeExpr == null) {
                return where;
            }

            // 加上括号保证优先级： (finalScopeExpr)
            Expression wrappedScopeExpr = CCJSqlParserUtil.parseCondExpression("(" + finalScopeExpr.toString() + ")");

            return where == null ? wrappedScopeExpr : new AndExpression(where, wrappedScopeExpr);

        } catch (Exception e) {
            throw new RuntimeException("动态数据权限 SQL 解析异常", e);
        }
    }

    /**
     * 核心翻译引擎：将单个 SysDataRule 翻译为 AST Expression
     */
    private Expression buildExpressionForRule(SysDataRule rule) throws Exception {
        String userId = UserContext.userId().toString();
        String deptId = UserContext.deptId().toString();

        String sqlFragment = "";

        switch (rule.getScopeType()) {
            case 2: // 本人数据 (假设表里存员工ID的字段一般别名叫 a.etypeid)
                sqlFragment = "a.etypeid = '" + userId + "'";
                break;
            case 3: // 本部门数据
                sqlFragment = "a.projectid = '" + deptId + "'";
                break;
            case 4: // 本部门及以下 (需要借助 SQL 的 LIKE 和 dept 表)
                // 复杂业务建议通过前端传入 deptIds 列表使用 IN 查询
                sqlFragment = "a.projectid IN (SELECT id FROM sys_dept WHERE ancestors LIKE '%" + deptId + "%' OR id = "
                    + deptId + ")";
                break;
            case 5: // 🌟 可视化自定义构建器 (JSON 转 SQL)
                if (StrUtil.isNotBlank(rule.getCustomSqlJson())) {
                    List<ConditionItemDTO> conditions =
                        JSONUtil.toList(rule.getCustomSqlJson(), ConditionItemDTO.class);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < conditions.size(); i++) {
                        ConditionItemDTO cond = conditions.get(i);
                        // 防注入校验（只能包含字母、数字、下划线、点）
                        if (!cond.getColumn().matches("^[a-zA-Z0-9_\\.]+$")) {
                            throw new RuntimeException("非法列名");
                        }

                        // 组装片段，如: a.ktypeid IN ('01', '02')
                        sb.append(cond.getColumn()).append(" ").append(cond.getOperator());
                        if ("IN".equalsIgnoreCase(cond.getOperator())
                            || "NOT IN".equalsIgnoreCase(cond.getOperator())) {
                            sb.append(" (").append(cond.getValue()).append(")");
                        } else {
                            sb.append(" ").append(cond.getValue());
                        }

                        if (i < conditions.size() - 1) {
                            sb.append(" AND ");
                        }
                    }
                    sqlFragment = sb.toString();
                }
                break;
        }

        if (StrUtil.isBlank(sqlFragment)) {
            return null;
        }
        return CCJSqlParserUtil.parseCondExpression(sqlFragment);
    }

    private String extractResourceName(String mappedStatementId) {
        // 简单示例：如果 mappedStatementId 包含 DlyBuyExtendMapper，返回 DlyBuy
        if (mappedStatementId.contains("DlyBuyExtendMapper")) {
            return "DlyBuy";
        }
        // 你可以通过自定义注解或约定好的 Mapper 命名规则来精确提取
        return null;
    }
}