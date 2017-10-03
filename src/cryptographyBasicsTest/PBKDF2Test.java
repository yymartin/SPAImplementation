package cryptographyBasicsTest;

import static org.junit.Assert.*;

import org.junit.Test;

import cryptographyBasics.PBKDF2;

public class PBKDF2Test {
	
	@Test
	public void test() {
			String password = "This is a password!";
			String hash = PBKDF2.generateHash(password);

			boolean test = PBKDF2.validatePassword("This is a password!", hash);
			assertTrue(test);
			
			test = PBKDF2.validatePassword("password1", hash);
			assertFalse(test);
		}
}
