package eu.unifiedviews.commons.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.bind.DatatypeConverter;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class CryptographyTest {

    private static Cryptography cryptography;

    @BeforeClass
    public static void beforeClass() throws URISyntaxException, IOException {
        File cryptographyKeyFile = new File(CryptographyTest.class.getClassLoader().getResource("cryptography/cryptography.key").toURI());

        cryptography = new Cryptography(cryptographyKeyFile.getAbsolutePath());
    }

    @Test
    public void encryptNullBytes() {
        byte[] plainText = null;
        byte[] cipherText = cryptography.encrypt(plainText);

        Assert.assertNull(cipherText);
    }

    @Test
    public void encryptEmptyBytes() {
        byte[] plainText = new byte[] {};

        encryptBytes(plainText);
    }

    private void encryptBytes(byte[] plainText) {
        byte[] cipherText = cryptography.encrypt(plainText);
        byte[] plainText2 = cryptography.decrypt(cipherText);

        Assert.assertArrayEquals(plainText, plainText2);
    }

    @Test
    public void encryptBytes() {
        byte[] plainText = DatatypeConverter.parseBase64Binary("plainText");

        encryptBytes(plainText);
    }

    @Test
    public void decryptNullBytes() {
        byte[] cipherText = null;
        byte[] plainText = cryptography.decrypt(cipherText);

        Assert.assertNull(plainText);
    }

    @Test
    public void encryptNullString() {
        String plainText = null;
        String cipherText = cryptography.encrypt(plainText);

        Assert.assertNull(cipherText);
    }

    @Test
    public void encryptEmptyString() {
        String plainText = "";

        encryptString(plainText);
    }

    private void encryptString(String plainText) {
        String cipherText = cryptography.encrypt(plainText);
        String plainText2 = cryptography.decrypt(cipherText);

        Assert.assertEquals(plainText, plainText2);
    }

    @Test
    public void encryptString() {
        String plainText = "plainText";

        encryptString(plainText);
    }

    @Test
    public void decryptNullString() {
        String cipherText = null;
        String plainText = cryptography.decrypt(cipherText);

        Assert.assertNull(plainText);
    }

}
