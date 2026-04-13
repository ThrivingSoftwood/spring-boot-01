package thriving.softwood.sample.util;

import cn.hutool.v7.crypto.asymmetric.RSA;
import cn.hutool.v7.crypto.digest.BCrypt;
import thriving.softwood.common.core.util.Sm4Util;

public class KaishiKeyGenerator {
    static void main() {
        String userPassword = "#";
        String dbPassword = "#";
        gen(userPassword, dbPassword);

    }

    private static void gen(String userPassword, String dbPassword) {
        String svcKeySm4 = Sm4Util.generateRandomHex();
        String svcIvSm4 = Sm4Util.generateRandomHex();
        Sm4Util.initLocal(svcKeySm4, svcIvSm4);
        String webKeySm4 = Sm4Util.generateRandomHex();
        String webIvSm4 = Sm4Util.generateRandomHex();
        RSA rsa = new RSA();
        String privateKeyBase64 = rsa.getPrivateKeyBase64();
        String publicKeyBase64 = rsa.getPublicKeyBase64();
        String encryptedDbPassword = Sm4Util.encLocal(dbPassword);
        String bcryptedUserPassword = BCrypt.hashpw(userPassword, BCrypt.gensalt());
        IO.println("userPassword: " + userPassword);
        IO.println("dbPassword: " + dbPassword);
        IO.println("privateKeyBase64: " + privateKeyBase64);
        IO.println("publicKeyBase64: " + publicKeyBase64);
        IO.println("encryptedDbPassword: " + encryptedDbPassword);
        IO.println("svcKeySm4: " + svcKeySm4);
        IO.println("svcIvSm4: " + svcIvSm4);
        IO.println("webKeySm4: " + webKeySm4);
        IO.println("webIvSm4: " + webIvSm4);
        IO.println("bcryptedUserPassword: " + bcryptedUserPassword);
    }
}
