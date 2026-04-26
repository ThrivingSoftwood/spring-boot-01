package thriving.softwood.common.message.enums;

/**
 * 网络请求返回 status code 的含义枚举
 * 
 * @author ThrivingSoftwood
 * @since version 2023-07-11
 */
public enum MessageTypeEnum {
    BIZ_WARNING(1, "业务预警"), SYSTEM_NOTIFICATION(2, "系统通知"), PENDING_APPROVALS(3, "审批待办"), RUNTIME_ERROR(4, "运行异常");

    private final Byte typeCode;
    private final String typeDesc;

    /**
     * 构造器
     * 
     * @author ThrivingSoftwood
     * @since version 2023-07-11
     * @param typeCode 状态码
     * @param typeDesc 英文描述
     */
    MessageTypeEnum(int typeCode, String typeDesc) {
        this.typeCode = (byte)typeCode;
        this.typeDesc = typeDesc;
    }

    public Byte typeCode() {
        return typeCode;
    }

    public String typeDesc() {
        return typeDesc;
    }
}
