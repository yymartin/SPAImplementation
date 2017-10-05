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
		SecretKey keyAES = MyKeyGenerator.generateSymmetricKey();
		byte[] keyOneTimePadding = MyKeyGenerator.getOneTimePaddingKey();
		
		byte[] cipherText = SymmetricEncryption.encryptAES(message, keyAES);
		String clearText = SymmetricEncryption.decryptAES(cipherText, keyAES);
		
		assertEquals(message, clearText);
		
		cipherText = SymmetricEncryption.encryptOneTimePadding(message, keyOneTimePadding);
		clearText = SymmetricEncryption.decryptOneTimePadding(cipherText, keyOneTimePadding);
		
		assertEquals(message, clearText);
	}
	
	@Test
	public void testSignatureVerification() {
		String message = "This is a message to sign!";
		SecretKey key = MyKeyGenerator.generateSymmetricKey();
		byte[] signature = SymmetricEncryption.sign(key, message);
		
		assertTrue(SymmetricEncryption.signatureVerification(key, message, signature));
	}
	
	@Test
	public void testHmacVerification() {
		String message = "This is a hmac message!";
		SecretKey key = MyKeyGenerator.generateHMacKey();
		byte[] hmac = SymmetricEncryption.generateHMac(message, key);
		
		assertTrue(SymmetricEncryption.verifyHMac(message, hmac, key));
	}

}
