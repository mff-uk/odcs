package eu.unifiedviews.commons.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.bind.DatatypeConverter;

import org.junit.Assert;
import org.junit.Test;

public class CryptographyTest {

    @Test
    public void encryptDecryptBytes() throws IOException, URISyntaxException {
        File cryptographyKeyFile = new File(getClass().getClassLoader().getResource("cryptography/cryptography.key").toURI());
        Cryptography cryptography = new Cryptography(cryptographyKeyFile.getAbsolutePath());
        byte[] plainText = DatatypeConverter.parseBase64Binary("plainText");
        byte[] cipherText = cryptography.encrypt(plainText);
        byte[] plainText2 = cryptography.decrypt(cipherText);

        Assert.assertArrayEquals(plainText, plainText2);
    }

    @Test
    public void encryptDecryptString() throws IOException, URISyntaxException {
        File cryptographyKeyFile = new File(getClass().getClassLoader().getResource("cryptography/cryptography.key").toURI());
        Cryptography cryptography = new Cryptography(cryptographyKeyFile.getAbsolutePath());
        String plainText = DatatypeConverter.printBase64Binary(DatatypeConverter.parseBase64Binary("plainText"));
        String cipherText = cryptography.encrypt(plainText);
        String plainText2 = cryptography.decrypt(cipherText);

        Assert.assertTrue(plainText.equals(plainText2));
    }

}
