package thriving.softwood.customer.first.biz.pojo.record;

import java.util.List;

/** 订单批量操作请求体 */
public record EdongfangOrderReq(
    // PUT/POST 接收 JSON 数组
    List<String> eOrderIds) {
}