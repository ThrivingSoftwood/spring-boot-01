// File: ./kaishi/src/main/java/thriving/softwood/kaishi/component/handler/UserDataPermissionHandler.java
package thriving.softwood.common.security.handler;

import org.springframework.stereotype.Component;
import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import cn.hutool.v7.core.text.StrUtil;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;

@Component
public class UserDataPermissionHandler implements MultiDataPermissionHandler {

    private final DataPermissionSqlBuilder sqlBuilder;

    public UserDataPermissionHandler(DataPermissionSqlBuilder sqlBuilder) {
        this.sqlBuilder = sqlBuilder;
    }

    @Override
    public Expression getSqlSegment(Table table, Expression where, String mappedStatementId) {
        // 1. 获取纯字符串条件
        String sqlSegment = sqlBuilder.buildSqlSegment(mappedStatementId);

        // 2. 如果为空，返回 null 放行
        if (StrUtil.isBlank(sqlSegment)) {
            return null;
        }

        // 3. 包装为 JSqlParser AST 表达式交给 MyBatis-Plus 拼接
        try {
            return CCJSqlParserUtil.parseCondExpression("(" + sqlSegment + ")");
        } catch (Exception e) {
            throw new RuntimeException("动态数据权限 SQL 解析异常", e);
        }
    }
}