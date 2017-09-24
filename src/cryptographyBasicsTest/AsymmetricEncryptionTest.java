package cryptographyBasicsTest;

import static org.junit.Assert.*;

import java.security.KeyPair;

import org.junit.Test;

import cryptographyBasics.AsymmetricEncryption;
import cryptographyBasics.MyKeyGenerator;

public class AsymmetricEncryptionTest {
	@Test
	public void testEncryptionAndDecryption() {
		String message = "This is a test message!";
		KeyPair keyPair = MyKeyGenerator.generateAssymetricKey();
		
		byte[] cipherText = AsymmetricEncryption.encrypt(message, keyPair.getPublic());
		String clearText = AsymmetricEncryption.decrypt(cipherText, keyPair.getPrivate());
		
		assertEquals(message, clearText);	
	}
	
	@Test
	public void testSignatureVerification() {
		String message = "This is a message to sign!";
		KeyPair keyPair = MyKeyGenerator.generateAssymetricKey();
		byte[] signature = AsymmetricEncryption.sign(keyPair.getPrivate(), message);
		
		assertTrue(AsymmetricEncryption.signatureVerification(keyPair.getPublic(), message, signature));
	}
}
