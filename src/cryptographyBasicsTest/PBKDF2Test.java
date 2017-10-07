package cryptographyBasicsTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import cryptographyBasics.Hash;

public class PBKDF2Test {
	
	@Test
	public void test() {
			String password = "This is a password!";
			String hash = Hash.generatePBKDF2WithHmacSHA1Hash(password);

			boolean test = Hash.validatePassword("This is a password!", hash);
			assertTrue(test);
			
			test = Hash.validatePassword("password1", hash);
			assertFalse(test);
		}
}
