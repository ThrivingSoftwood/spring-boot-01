package thriving.softwood.customer.first.biz.pojo.record;

/**
 * 开通供应商专属账号的请求体
 */
public record ProvisionSupplierUserReq(Integer supplierId, // 对应的供应商ID
    String loginAccount, // 开通的登录账号
    String plainPassword // 初始明文密码 (由前端SM4加密后传输，这里假设解密后)
) {
}