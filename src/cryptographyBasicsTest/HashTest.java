package cryptographyBasicsTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import cryptographyBasics.Hash;

public class HashTest {
	
	@Test
	public void test() {
			String password = "This is a password!";
			String hashPBKDF2 = Hash.generatePBKDF2WithHmacSHA1Hash(password);

			boolean test = Hash.validatePassword("This is a password!", hashPBKDF2);
			assertTrue(test);
			
			test = Hash.validatePassword("password1", hashPBKDF2);
			assertFalse(test);
		}
}