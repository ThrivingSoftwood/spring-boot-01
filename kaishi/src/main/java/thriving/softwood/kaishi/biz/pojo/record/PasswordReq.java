package thriving.softwood.kaishi.biz.pojo.record;

public record PasswordReq(String loginAccount, String oldPasswordEnc, String newPasswordEnc) {
}