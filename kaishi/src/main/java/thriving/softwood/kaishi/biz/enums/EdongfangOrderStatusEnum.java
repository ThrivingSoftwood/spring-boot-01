package thriving.softwood.kaishi.biz.enums;

public enum EdongfangOrderStatusEnum {
    NEW_ORDER(0, "新建"), SHIPPED(5, "已发货"), CANCELED(-2, "取消"), REFUSED(-1, "拒收"), SIGNED(1, "签收"),
    IN_EXCHARGING(4, "退换货中");

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
    EdongfangOrderStatusEnum(int code, String description) {
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
