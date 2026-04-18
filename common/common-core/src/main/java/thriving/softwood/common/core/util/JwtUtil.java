package thriving.softwood.common.core.util;

import java.util.Map;

import org.springframework.stereotype.Component;

import cn.hutool.v7.core.convert.ConvertUtil;
import cn.hutool.v7.core.map.MapUtil;
import cn.hutool.v7.crypto.asymmetric.RSA;
import cn.hutool.v7.json.jwt.JWT;
import cn.hutool.v7.json.jwt.JWTPayload;
import cn.hutool.v7.json.jwt.JWTUtil;
import cn.hutool.v7.json.jwt.signers.JWTSigner;
import cn.hutool.v7.json.jwt.signers.JWTSignerUtil;
import thriving.softwood.common.core.exception.TokenException;

/*** 基于 Hutool v7 的 RS256 JWT 工具类 */

@Component
public class JwtUtil {

    private String privateKeyBase64;
    private String publicKeyBase64;
    private static RSA rsa;
    private static JWTSigner privateKeySigner;
    private static JWTSigner publicKeySigner;

    public static void init(String privateKeyBase64, String publicKeyBase64) {
        // 从 Base64 还原密钥对
        rsa = new RSA(privateKeyBase64, publicKeyBase64);
        // Hutool v7 API: 创建非对称签名器
        privateKeySigner = JWTSignerUtil.rs256(rsa.getPrivateKey());
        publicKeySigner = JWTSignerUtil.rs256(rsa.getPublicKey());
    }

    /**
     * 🌟 核心升级：签发 Token 时必须带入权限版本号 permVersion
     */
    public static String generateToken(Long userId, String loginAccount, String permVersion) {
        Long now = System.currentTimeMillis();
        // 过期时间：12小时,测试token 过期是否正常跳转到登录页时用 5 秒
        long expireTime = now + 1000 * 60 * 60 * 12;
        // long expireTime = now + 1000 * 5;

        // Hutool v7 API: MapUtil.ofKvs 构建载荷
        Map<String, Object> payload =
            MapUtil.ofKvs(false, "userId", userId, "loginAccount", loginAccount, "permVersion", permVersion, // 👈 注入版本号
                JWTPayload.ISSUED_AT, now / 1000, JWTPayload.EXPIRES_AT, expireTime / 1000);

        return JWTUtil.createToken(payload, privateKeySigner);
    }

    /**
     * 验证并解析 Token
     */
    public static JWTPayload verifyAndParse(String token) {
        // Hutool v7 API: 验证签名
        boolean isValid = JWTUtil.verify(token, publicKeySigner);
        if (!isValid) {
            throw new TokenException("JWT 签名验证失败，Token 被篡改或已损坏");
        }

        JWT jwt = JWTUtil.parseToken(token);
        JWTPayload payload = jwt.getPayload();

        // 验证过期时间 (参考 Hutool v7 文档第9页)
        long exp = ConvertUtil.toLong(payload.getClaim(JWTPayload.EXPIRES_AT)) * 1000;
        if (System.currentTimeMillis() > exp) {
            throw new TokenException("JWT 已过期，请重新登录");
        }

        return payload;
    }
}