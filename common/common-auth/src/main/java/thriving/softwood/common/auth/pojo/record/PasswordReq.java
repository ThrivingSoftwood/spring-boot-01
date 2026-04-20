package thriving.softwood.common.auth.pojo.record;

public record PasswordReq(String loginAccount, String oldPasswordEnc, String newPasswordEnc) {
}