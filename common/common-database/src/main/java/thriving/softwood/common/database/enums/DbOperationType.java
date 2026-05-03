package thriving.softwood.common.database.enums;

/**
 * 网络请求返回 status code 的含义枚举
 * 
 * @author ThrivingSoftwood
 * @since version 2023-07-11
 */
public enum DbOperationType {
    SELECT(0, "查询"), INSERT(1, " 增加"), UPDATE(2, " 更新"), DELETE(3, " 删除");

    private final Integer code;
    private final String description;

    /**
     * 构造器
     * 
     * @author ThrivingSoftwood
     * @since version 2023-07-11
     * @param code 状态码
     * @param description 英文描述
     */
    DbOperationType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer code() {
        return code;
    }

    public String description() {
        return description;
    }
}
