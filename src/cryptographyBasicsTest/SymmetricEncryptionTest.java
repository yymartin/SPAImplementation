package cryptographyBasicsTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.security.SecureRandom;

import javax.crypto.SecretKey;

import org.junit.Test;

import cryptographyBasics.MyKeyGenerator;
import cryptographyBasics.SymmetricEncryption;

public class SymmetricEncryptionTest {
	
	@Test
	public void testEncryptionAndDecryption() {
		BigInteger message = new BigInteger(512, new SecureRandom());
		SecretKey keyAES = MyKeyGenerator.generateSymmetricKey();
		byte[] keyOneTimePadding = MyKeyGenerator.getOneTimePaddingKey(message.toByteArray().length);
		
		byte[] cipherText = SymmetricEncryption.encryptAES(message, keyAES);
		BigInteger clearText = SymmetricEncryption.decryptAES(cipherText, keyAES);
		
		assertEquals(message, clearText);
		
		cipherText = SymmetricEncryption.encryptOneTimePadding(message, keyOneTimePadding);
		clearText = SymmetricEncryption.decryptOneTimePadding(cipherText, keyOneTimePadding);
		
		assertEquals(message, clearText);
	}
	
	@Test
	public void testEncryptionAndDecryptionFromFile() {
		String address = "/Users/yoanmartin/Desktop";
		
		BigInteger message = new BigInteger(512, new SecureRandom());
		MyKeyGenerator.generateSymmetricKeyToFile(address);
		
		SecretKey key = MyKeyGenerator.getSymmetricKeyFromFile(address);
		byte[] cipherText = SymmetricEncryption.encryptAES(message, key);
		BigInteger clearText = SymmetricEncryption.decryptAES(cipherText, key);
		
		assertEquals(message, clearText);
		
	}
	
	@Test
	public void testSignatureVerification() {
		BigInteger message = new BigInteger(1024, new SecureRandom());
		SecretKey key = MyKeyGenerator.generateSymmetricKey();
		byte[] signature = SymmetricEncryption.sign(key, message);
		
		assertTrue(SymmetricEncryption.signatureVerification(key, message, signature));
	}
	
	@Test
	public void testHmacVerification() {
		BigInteger message = new BigInteger(1024, new SecureRandom());
		SecretKey key = MyKeyGenerator.generateHMacKey();
		byte[] hmac = SymmetricEncryption.generateHMac(message, key);
		
		assertTrue(SymmetricEncryption.verifyHMac(message, hmac, key));
	}

}
