package thriving.softwood.kaishi.biz.pojo.record;

/** 订单分页查询请求体 */
public record EdongfangOrderQryReq(
    // 接收前端的逗号分割字符串
    String eOrderIds, Integer status,
    // 🌟 是否仅查询已发货信息 (对应需求: 变身为发货信息页面)
    Boolean queryShipped) {
}