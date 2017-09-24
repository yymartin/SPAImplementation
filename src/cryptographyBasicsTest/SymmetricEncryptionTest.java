package cryptographyBasicsTest;

import static org.junit.Assert.*;

import javax.crypto.SecretKey;

import org.junit.Test;

import cryptographyBasics.MyKeyGenerator;
import cryptographyBasics.SymmetricEncryption;

public class SymmetricEncryptionTest {
	
	@Test
	public void testEncryptionAndDecryption() {
		String message = "This is a test message!";
		SecretKey key = MyKeyGenerator.generateSymmetricKey();
		
		byte[] cipherText = SymmetricEncryption.encrypt(message, key);
		String clearText = SymmetricEncryption.decrypt(cipherText, key);
		
		assertEquals(message, clearText);	
	}
	
	@Test
	public void testSignatureVerification() {
		String message = "This is a message to sign!";
		SecretKey key = MyKeyGenerator.generateSymmetricKey();
		byte[] signature = SymmetricEncryption.sign(key, message);
		
		assertTrue(SymmetricEncryption.signatureVerification(key, message, signature));
	}

}
