package cryptographyBasicsTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.junit.Test;

import cryptographyBasics.AsymmetricEncryption;
import cryptographyBasics.MyKeyGenerator;

public class AsymmetricEncryptionTest {
	@Test
	public void testEncryptionAndDecryption() {
		BigInteger message = new BigInteger(1024, new SecureRandom());
		KeyPair keyPair = MyKeyGenerator.generateAsymmetricKey();
		
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		
		BigInteger cipherText = AsymmetricEncryption.encrypt(message, publicKey);
		BigInteger clearText = AsymmetricEncryption.decrypt(cipherText, privateKey);
		
		assertEquals(message, clearText);	
	}
	
	@Test
	public void testEncryptionAndDecryptionFromFile() {
		String address = "/Users/yoanmartin/Desktop";
		BigInteger message = new BigInteger(1024, new SecureRandom());
		MyKeyGenerator.generateAsymmetricKeyToFile(address);
		
		RSAPublicKey publicKey = (RSAPublicKey) MyKeyGenerator.getPublicKeyFromFile(address);
		RSAPrivateKey privateKey = (RSAPrivateKey) MyKeyGenerator.getPrivateKeyFromFile(address);
		
		BigInteger cipherText = AsymmetricEncryption.encrypt(message, publicKey);
		BigInteger clearText = AsymmetricEncryption.decrypt(cipherText, privateKey);
		
		assertEquals(message, clearText);
	}
	
	@Test
	public void testSignatureVerification() {
		BigInteger message = new BigInteger(1024, new SecureRandom());
		KeyPair keyPair = MyKeyGenerator.generateAsymmetricKey();
		
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		
		BigInteger signature = AsymmetricEncryption.sign(message, privateKey);
		
		assertTrue(AsymmetricEncryption.signatureVerification(signature, message, publicKey));
	}
	
	@Test
	public void testBlindSignature() {
		BigInteger message = new BigInteger(1024, new SecureRandom());
		KeyPair keyPair = MyKeyGenerator.generateAsymmetricKey();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		
		BigInteger r = AsymmetricEncryption.generateRForBlindSignature(publicKey.getModulus());

		BigInteger newMessage = AsymmetricEncryption.blind(message, r, publicKey);
		BigInteger signature = AsymmetricEncryption.sign(newMessage, privateKey);
		
		BigInteger newSignature = AsymmetricEncryption.unblind(signature, privateKey.getModulus(), r);
				
		assertTrue(AsymmetricEncryption.signatureVerification(newSignature, message, publicKey));
	}
}
