package thriving.softwood.common.core.cryptor;

import java.nio.charset.StandardCharsets;

import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.v7.core.codec.binary.Hex;
import cn.hutool.v7.crypto.KeyUtil;
import cn.hutool.v7.crypto.Mode;
import cn.hutool.v7.crypto.Padding;
import cn.hutool.v7.crypto.symmetric.SM4;

public class Sm4StringEncryptor implements StringEncryptor {
    private static final Logger logger = LoggerFactory.getLogger(Sm4StringEncryptor.class);
    private final SM4 sm4;

    public Sm4StringEncryptor(String key, String iv) {
        sm4 = new SM4(Mode.CBC, Padding.PKCS5Padding, Hex.decode(key), Hex.decode(iv));
    }

    /**
     * 生成 SM4 密钥 (16字节) 并转为 Hex 字符串 在 v7 中，使用 KeyUtil 更符合规范
     */
    public static String generateRandomHex() {
        // 生成 128 位 (16 字节) 的 SM4 专用密钥
        byte[] keyBytes = KeyUtil.generateKey("SM4", 128).getEncoded();
        return Hex.encodeStr(keyBytes);
    }

    @Override
    public String encrypt(String plainText) {
        return sm4.encryptHex(plainText, StandardCharsets.UTF_8);
    }

    @Override
    public String decrypt(String cipherText) {
        return sm4.decryptStr(cipherText, StandardCharsets.UTF_8);
    }

}