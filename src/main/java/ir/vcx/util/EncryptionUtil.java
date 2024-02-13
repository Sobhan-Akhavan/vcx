package ir.vcx.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class EncryptionUtil {

    private static final String AES = "AES";
    private static final byte[] key = new byte[]{'T', 'h', 'e', 'B', 'e', 's', 't', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y'};

    private static Key generateKey() {
        return new SecretKeySpec(key, AES);
    }

    public static byte[] encryptData(byte[] data) throws Exception {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public static byte[] decryptData(byte[] encryptedData) throws Exception {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encryptedData);
    }
}
