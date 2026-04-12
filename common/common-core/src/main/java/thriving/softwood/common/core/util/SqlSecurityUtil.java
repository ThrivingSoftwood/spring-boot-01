package thriving.softwood.common.core.util;

import java.util.regex.Pattern;

public class SqlSecurityUtil {

    // 正则规则：仅允许大小写字母、数字、下划线，且最多只能包含一个点号（用于表别名如 a.Vchcode）
    private static final Pattern SORT_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+(\\.[a-zA-Z0-9_]+)?$");

    /**
     * 校验排序字段是否安全
     */
    public static void checkSortField(String field) {
        if (field == null || field.trim().isEmpty()) {
            return;
        }
        if (!SORT_PATTERN.matcher(field).matches()) {
            // 一旦包含非法字符（如分号、空格、单引号），直接抛出异常拦截！
            throw new IllegalArgumentException("检测到非法的排序字段: " + field);
        }
    }
}