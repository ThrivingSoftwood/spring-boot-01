package thriving.softwood.common.auth.pojo.record;

public record LoginReq(String loginAccount, String encryptedPassword) {
}