/*******************************************************************************
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package eu.unifiedviews.commons.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

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
    public void encryptBytes() throws UnsupportedEncodingException {
        byte[] plainText = "plainText".getBytes("utf8");

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
