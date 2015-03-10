package cz.cuni.mff.xrg.odcs.commons.app.util;

import java.io.File;
import java.io.IOException;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

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
        return doFinal(Cipher.ENCRYPT_MODE, plainText);
    }

    private String doFinal(int opmode, String input) {
        String result = input;

        if (key != null && StringUtils.isNotBlank(input)) {
            try {
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(opmode, key);

                result = new String(cipher.doFinal(input.getBytes("utf8")), "utf8");
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        return result;
    }

    @Override
    public String decrypt(String cipherText) {
        return doFinal(Cipher.DECRYPT_MODE, cipherText);
    }

}
