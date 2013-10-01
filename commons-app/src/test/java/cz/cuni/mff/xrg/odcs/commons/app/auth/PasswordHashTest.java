package cz.cuni.mff.xrg.odcs.commons.app.auth;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static cz.cuni.mff.xrg.odcs.commons.app.auth.PasswordHash.createHash;
import static cz.cuni.mff.xrg.odcs.commons.app.auth.PasswordHash.validatePassword;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the basic functionality of the PasswordHash class.
 *
 * @author Jan Vojt
 */
public class PasswordHashTest {

	@Test
	public void testCreateHash() throws NoSuchAlgorithmException, InvalidKeySpecException {
		String hash = createHash("p\r\nassw0Rd!");
		assertNotNull(hash);
		assertFalse(hash.isEmpty());
	}
	
	@Test
	public void testHashesForEquality() throws NoSuchAlgorithmException, InvalidKeySpecException {
		String password = "s0m3P4ss//rd54";
		String hash = createHash(password);
		String secondHash = createHash(password);
		
		assertNotEquals(hash, secondHash);
	}
	
	@Test
	public void testWrongPassword() throws NoSuchAlgorithmException, InvalidKeySpecException {
		String password = "s0m3P4ss//rd54";
		String wrongPassword = "wrongpassword";
		String hash = createHash(password);
	
		assertFalse(validatePassword(wrongPassword, hash));
	}
	
	@Test
	public void testPasswordValidation() throws NoSuchAlgorithmException, InvalidKeySpecException {
		String password = "s0m3P4ss//rd54";
		String hash = createHash(password);
		assertTrue(validatePassword(password, hash));
	}
	
}