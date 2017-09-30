package cryptographyBasicsTest;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.junit.Test;

import cryptographyBasics.AsymmetricEncryption;
import cryptographyBasics.MyKeyGenerator;

public class AsymmetricEncryptionTest {
	@Test
	public void testEncryptionAndDecryption() {
		String message = "This is a test message!";
		KeyPair keyPair = MyKeyGenerator.generateAssymetricKey();
		
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		
		BigInteger cipherText = AsymmetricEncryption.encrypt(message, publicKey);
		String clearText = AsymmetricEncryption.decrypt(cipherText, privateKey);
		
		assertEquals(message, clearText);	
	}
	
	@Test
	public void testSignatureVerification() {
		String message = "This is a message to sign!";
		KeyPair keyPair = MyKeyGenerator.generateAssymetricKey();
		
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		
		BigInteger signature = AsymmetricEncryption.sign(privateKey, new BigInteger(message.getBytes()));
		
		assertTrue(AsymmetricEncryption.signatureVerification(publicKey, message, signature));
	}
	
	@Test
	public void testBlindSignature() {
		String message = "This is a blind test message!";
		KeyPair keyPair = MyKeyGenerator.generateAssymetricKey();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		
		BigInteger r = AsymmetricEncryption.generateRForBlindSignature(publicKey.getModulus());

		BigInteger newMessage = AsymmetricEncryption.blind(message, publicKey, r);
		BigInteger signature = AsymmetricEncryption.sign(privateKey, newMessage);
		
		BigInteger newSignature = AsymmetricEncryption.unblind(signature, privateKey, r);
				
		assertTrue(AsymmetricEncryption.signatureVerification(publicKey, message, newSignature));
	}
}
