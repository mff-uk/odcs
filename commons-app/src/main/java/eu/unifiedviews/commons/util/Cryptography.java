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

public class Cryptography {

    private final Key key;

    /**
     * @param cryptographyKeyFile
     *            Absolute file path with 128 bit cryptography key in it.
     * @throws IOException
     *             If cryptography key or cryptography key file is wrong.
     */
    public Cryptography(String cryptographyKeyFile) throws IOException {
        key = new SecretKeySpec(FileUtils.readFileToByteArray(new File(cryptographyKeyFile)), "AES");
    }

    /**
     * @param plainText
     *            byte array for encryption.
     * @return Encrypted byte array if plainText is not null, null otherwise.
     */
    public byte[] encrypt(byte[] plainText) {
        return doFinal(plainText, Cipher.ENCRYPT_MODE);
    }

    private byte[] doFinal(byte[] input, int opmode) {
        byte[] result = input;

        if (input != null) {
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
     * @return Decrypted byte array if cipherText is not null, null otherwise.
     */
    public byte[] decrypt(byte[] cipherText) {
        return doFinal(cipherText, Cipher.DECRYPT_MODE);
    }

    /**
     * @param plainText
     *            Base64 encoded string for encryption.
     * @return Encrypted string encoded with Base64 if plainText is not null, null otherwise.
     */
    public String encrypt(String plainText) {
        return doFinal(plainText, Cipher.ENCRYPT_MODE);
    }

    private String doFinal(String input, int opmode) {
        String result = input;

        if (input != null) {
            try {
                result = DatatypeConverter.printBase64Binary(doFinal(opmode, DatatypeConverter.parseBase64Binary(input)));
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        return result;
    }

    /**
     * @param cipherText
     *            Base64 encoded string for decryption.
     * @return Decrypted string encoded with Base64 if cipherText is not null, null otherwise.
     */
    public String decrypt(String cipherText) {
        return doFinal(cipherText, Cipher.DECRYPT_MODE);
    }

}
