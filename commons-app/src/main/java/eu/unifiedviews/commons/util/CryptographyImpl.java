package eu.unifiedviews.commons.util;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import eu.unifiedviews.util.Cryptography;

public class CryptographyImpl implements Cryptography {

    private Key key;

    /**
     * @param cryptographyKeyFile
     *            Absolute file path with 128 bit cryptography key in it.
     * @throws IOException
     *             If cryptography key or cryptography key file is wrong.
     */
    public CryptographyImpl(String cryptographyKeyFile) throws IOException {
        if (StringUtils.isNotBlank(cryptographyKeyFile)) {
            key = new SecretKeySpec(FileUtils.readFileToByteArray(new File(cryptographyKeyFile)), "AES");
        }
    }

    @Override
    public String encrypt(String plainText) {
        String result = plainText;

        if (StringUtils.isNotBlank(plainText)) {
            try {
                result = Base64.encodeBase64String(doFinal(Cipher.ENCRYPT_MODE, plainText.getBytes("utf8")));
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        return result;
    }

    private byte[] doFinal(int opmode, byte[] input) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] result = input;

        if (key != null) {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(opmode, key);

            result = cipher.doFinal(input);
        }

        return result;
    }

    @Override
    public String decrypt(String cipherText) {
        String result = cipherText;

        if (StringUtils.isNotBlank(cipherText)) {
            try {
                result = new String(doFinal(Cipher.DECRYPT_MODE, Base64.decodeBase64(cipherText)), "utf8");
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        return result;
    }

}
