// File: ./kaishi/src/main/java/thriving/softwood/kaishi/component/interceptor/PlaceholderPermissionInterceptor.java
package thriving.softwood.common.security.interceptor;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;

import cn.hutool.v7.core.text.StrUtil;
import thriving.softwood.common.security.annotation.ReplacePlaceholder;
import thriving.softwood.common.security.handler.AbstractDataPermissionSqlBuilder;

/**
 * 🚀 基于占位符的数据权限字符串替换拦截器 (完美适配分页插件版)
 */
public class PlaceholderPermissionInterceptor implements InnerInterceptor {

    private final AbstractDataPermissionSqlBuilder sqlBuilder;
    private final Map<String, String> placeholderCache = new ConcurrentHashMap<>();

    public PlaceholderPermissionInterceptor(AbstractDataPermissionSqlBuilder sqlBuilder) {
        this.sqlBuilder = sqlBuilder;
    }

    /**
     * 🌟 适配 SELECT 查询（拦截在分页插件之前）
     */
    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds,
        ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        // 核心逻辑抽离，提前在此处修改 BoundSql 中的 SQL，让后续的分页插件拿到替换后的安全 SQL
        processReplace(ms, boundSql);
    }

    /**
     * 🌟 适配 UPDATE/DELETE 查询
     */
    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
        MappedStatement ms = mpSh.mappedStatement();
        BoundSql boundSql = mpSh.boundSql();

        processReplace(ms, boundSql);
    }

    /**
     * 核心字符串替换逻辑
     */
    private void processReplace(MappedStatement ms, BoundSql boundSql) {
        String mappedStatementId = ms.getId();

        // 1. 缓存获取占位符
        String placeholder = placeholderCache.computeIfAbsent(mappedStatementId, id -> {
            try {
                int lastDot = id.lastIndexOf('.');
                String className = id.substring(0, lastDot);
                String methodName = id.substring(lastDot + 1);

                Class<?> clazz = Class.forName(className);
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.getName().equals(methodName)) {
                        ReplacePlaceholder annotation = method.getAnnotation(ReplacePlaceholder.class);
                        return annotation != null ? annotation.value() : "NONE";
                    }
                }
            } catch (Exception ignored) {
            }
            return "NONE";
        });

        if ("NONE".equals(placeholder)) {
            return;
        }

        String originalSql = boundSql.getSql();
        // 如果 XML 中的 SQL 包含我们定义的占位符（如 /*_DATA_PERMISSION_*/）
        if (originalSql.contains(placeholder)) {
            String permissionCond = sqlBuilder.buildSqlSegment(mappedStatementId);

            // 提权放行时，直接把占位符抹掉
            String replacement = StrUtil.isNotBlank(permissionCond) ? " AND (" + permissionCond + ") " : "";

            String newSql = originalSql.replace(placeholder, replacement);

            // 反射写回修改后的 SQL
            MetaObject metaObject = SystemMetaObject.forObject(boundSql);
            metaObject.setValue("sql", newSql);
        }
    }
}