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
 * @author Eastean
 * @since 2023-07-15 19:35:22
 */

public class Sm4Util {
    private static final Logger logger = LoggerFactory.getLogger(Sm4Util.class);

    private static SM4 SM4_ENCRYPTOR = null;

    public static void init(String key, String iv) {
        SM4_ENCRYPTOR = new SM4(Mode.CBC, Padding.PKCS5Padding, Hex.decode(key), Hex.decode(iv));
    }

    /**
     * 生成 SM4 密钥 (16字节) 并转为 Hex 字符串 在 v7 中，使用 KeyUtil 更符合规范
     */
    public static String generateRandomHex() {
        // 生成 128 位 (16 字节) 的 SM4 专用密钥
        byte[] keyBytes = KeyUtil.generateKey("SM4", 128).getEncoded();
        return Hex.encodeStr(keyBytes);
    }

    public static String encrypt(String plainText) {
        return SM4_ENCRYPTOR.encryptHex(plainText, StandardCharsets.UTF_8);
    }

    public static String decrypt(String cipherText) {
        return SM4_ENCRYPTOR.decryptStr(cipherText, StandardCharsets.UTF_8);
    }
}
