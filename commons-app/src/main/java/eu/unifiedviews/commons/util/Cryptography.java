package eu.unifiedviews.commons.util;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class Cryptography {

    private Key key;

    /**
     * @param cryptographyKeyFile
     *            Absolute file path with 128 bit cryptography key in it.
     * @throws IOException
     *             If cryptography key or cryptography key file is wrong.
     */
    public Cryptography(String cryptographyKeyFile) throws IOException {
        if (StringUtils.isNotBlank(cryptographyKeyFile)) {
            key = new SecretKeySpec(FileUtils.readFileToByteArray(new File(cryptographyKeyFile)), "AES");
        }
    }

    /**
     * @param plainText
     *            byte array for encryption.
     * @return Encrypted byte array if cryptography is turned on, input byte array otherwise.
     */
    public byte[] encrypt(byte[] plainText) {
        return doFinal(plainText, Cipher.ENCRYPT_MODE);
    }

    private byte[] doFinal(byte[] input, int opmode) {
        byte[] result = input;

        if (key != null && ArrayUtils.isNotEmpty(input)) {
            try {
                result = doFinal(opmode, input);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        return result;
    }

    private byte[] doFinal(int opmode, byte[] input) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(opmode, key, new IvParameterSpec(key.getEncoded()));

        return cipher.doFinal(input);
    }

    /**
     * @param cipherText
     *            byte array for decryption.
     * @return Decrypted byte array if cryptography is turned on, input byte array otherwise.
     */
    public byte[] decrypt(byte[] cipherText) {
        return doFinal(cipherText, Cipher.DECRYPT_MODE);
    }

    /**
     * @param plainText
     *            Base64 encoded string for encryption.
     * @return Encrypted string encoded with Base64 if cryptography is turned on, input string otherwise.
     */
    public String encrypt(String plainText) {
        String result = plainText;

        if (key != null && StringUtils.isNotBlank(plainText)) {
            try {
                result = DatatypeConverter.printBase64Binary(doFinal(Cipher.ENCRYPT_MODE, DatatypeConverter.parseBase64Binary(plainText)));
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        return result;
    }

    /**
     * @param cipherText
     *            Base64 encoded string for decryption.
     * @return Decrypted string encoded with Base64 if cryptography is turned on, input string otherwise.
     */
    public String decrypt(String cipherText) {
        String result = cipherText;

        if (key != null && StringUtils.isNotBlank(cipherText)) {
            try {
                result = DatatypeConverter.printBase64Binary(doFinal(Cipher.DECRYPT_MODE, DatatypeConverter.parseBase64Binary(cipherText)));
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        return result;
    }

}
