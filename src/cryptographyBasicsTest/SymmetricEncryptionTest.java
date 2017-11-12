package cryptographyBasicsTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import javax.crypto.SecretKey;

import org.junit.Test;

import cryptographyBasics.MyKeyGenerator;
import cryptographyBasics.SymmetricEncryption;

public class SymmetricEncryptionTest {
	
	@Test
	public void testEncryptionAndDecryption() {		
		BigInteger message = new BigInteger(1023, new SecureRandom());

		SecretKey keyAES = MyKeyGenerator.generateAESKey();
		byte[] keyOneTimePadding = MyKeyGenerator.getOneTimePaddingKey(message.toByteArray().length);
		
		byte[] cipherText = SymmetricEncryption.encryptAES(message.toByteArray(), keyAES);
		byte[] clearText = SymmetricEncryption.decryptAES(cipherText, keyAES);
		assertEquals(message, new BigInteger(clearText));
		
		cipherText = SymmetricEncryption.encryptOneTimePadding(message.toByteArray(), keyOneTimePadding);
		clearText = SymmetricEncryption.decryptOneTimePadding(cipherText, keyOneTimePadding);
		assertEquals(message, new BigInteger(clearText));
	}
	
	@Test
	public void testEncryptionAndDecryptionFromFile() {
		String address = "/Users/yoanmartin/Desktop";
		
		BigInteger message = new BigInteger(1024, new SecureRandom());
		MyKeyGenerator.generateAESKeyToFile(address, "test");
		
		SecretKey key = MyKeyGenerator.getAESKeyFromFile(address, "test");
		byte[] cipherText = SymmetricEncryption.encryptAES(message.toByteArray(), key);
		BigInteger clearText = new BigInteger(SymmetricEncryption.decryptAES(cipherText, key));
		
		assertEquals(message, clearText);
		
		MyKeyGenerator.generateOneTimePaddingKeyToFile(address, "test", message.toByteArray().length);
		byte[] keyOneTimePadding = MyKeyGenerator.getOneTimePaddingKeyFromFile(address, "test");
		cipherText = SymmetricEncryption.encryptOneTimePadding(message.toByteArray(), keyOneTimePadding);
		clearText = new BigInteger(SymmetricEncryption.decryptOneTimePadding(cipherText, keyOneTimePadding));
		assertEquals(message, clearText);	
	}
	
	@Test
	public void testEncryptionAndDecryptionFromPassword() {
		byte[] messageBytes = new byte[1024];
		Random rand = new Random();
		rand.nextBytes(messageBytes);
		
		BigInteger message = new BigInteger(messageBytes);
		BigInteger password = new BigInteger(1024, new SecureRandom());		
		SecretKey key = MyKeyGenerator.generateAESKeyFromPassword(password);
		byte[] cipherText = SymmetricEncryption.encryptAES(message.toByteArray(), key);
		BigInteger clearText = new BigInteger(SymmetricEncryption.decryptAES(cipherText, key));
		
		assertEquals(message, clearText);
		
		byte[] keyOneTimePadding = MyKeyGenerator.generateOneTimePaddingKeyFromPassword(password);
		cipherText = SymmetricEncryption.encryptOneTimePadding(message.toByteArray(), keyOneTimePadding);
		clearText = new BigInteger(SymmetricEncryption.decryptOneTimePadding(cipherText, keyOneTimePadding));
		assertEquals(message, clearText);	
	}
	
	
	@Test
	public void testHmacVerification() {
		BigInteger message = new BigInteger(1024, new SecureRandom());
		SecretKey key = MyKeyGenerator.generateHMacKey();
		byte[] hmac = SymmetricEncryption.generateHMac(message, key);
		
		assertTrue(SymmetricEncryption.verifyHMac(message, hmac, key));
		
		String address = "/Users/yoanmartin/Desktop";
		MyKeyGenerator.generateHMacKeyToFile(address, "test");
		key = MyKeyGenerator.getHMacKeyFromFile(address, "test");
		
		hmac = SymmetricEncryption.generateHMac(message, key);
		assertTrue(SymmetricEncryption.verifyHMac(message, hmac, key));
		
		BigInteger password = new BigInteger(1024, new SecureRandom());
		key = MyKeyGenerator.generateHMacKeyFromPassword(password);
		hmac = SymmetricEncryption.generateHMac(message, key);
		assertTrue(SymmetricEncryption.verifyHMac(message, hmac, key));		
	}

}
