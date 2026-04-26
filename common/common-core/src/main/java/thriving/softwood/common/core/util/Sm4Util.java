package thriving.softwood.common.core.util;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.v7.core.codec.binary.Hex;
import cn.hutool.v7.crypto.KeyUtil;
import cn.hutool.v7.crypto.Mode;
import cn.hutool.v7.crypto.Padding;
import cn.hutool.v7.crypto.symmetric.SM4;

/**
 * @author ThrivingSoftwood
 * @since 2023-07-15 19:35:22
 */

public class Sm4Util {
    private static final Logger logger = LoggerFactory.getLogger(Sm4Util.class);

    private static SM4 LOCAL_SM4 = null;
    private static SM4 WEB_SM4 = null;

    public static void initLocal(String key, String iv) {
        LOCAL_SM4 = new SM4(Mode.CBC, Padding.PKCS5Padding, Hex.decode(key), Hex.decode(iv));
    }

    public static void initWeb(String key, String iv) {
        WEB_SM4 = new SM4(Mode.CBC, Padding.PKCS5Padding, Hex.decode(key), Hex.decode(iv));
    }

    /**
     * 生成 SM4 密钥 (16字节) 并转为 Hex 字符串 在 v7 中，使用 KeyUtil 更符合规范
     */
    public static String generateRandomHex() {
        // 生成 128 位 (16 字节) 的 SM4 专用密钥
        byte[] keyBytes = KeyUtil.generateKey("SM4", 128).getEncoded();
        return Hex.encodeStr(keyBytes);
    }

    public static String encLocal(String plainText) {
        return LOCAL_SM4.encryptHex(plainText, StandardCharsets.UTF_8);
    }

    public static String decLocal(String cipherText) {
        return LOCAL_SM4.decryptStr(cipherText, StandardCharsets.UTF_8);
    }

    public static String encWeb(String plainText) {
        return WEB_SM4.encryptHex(plainText, StandardCharsets.UTF_8);
    }

    public static String decWeb(String cipherText) {
        return WEB_SM4.decryptStr(cipherText, StandardCharsets.UTF_8);
    }

}
