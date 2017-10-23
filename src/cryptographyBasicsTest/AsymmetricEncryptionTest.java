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
		MyKeyGenerator.generateAsymmetricKeyToFile(address, "test");
		
		RSAPublicKey publicKey = (RSAPublicKey) MyKeyGenerator.getPublicKeyFromFile(address,"test");
		RSAPrivateKey privateKey = (RSAPrivateKey) MyKeyGenerator.getPrivateKeyFromFile(address,"test");
		
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
					
		assertTrue(AsymmetricEncryption.signatureVerification(message, signature, publicKey));
	}
	
	@Test
	public void testBlindSignature() {
		KeyPair keypair = MyKeyGenerator.generateAsymmetricKey();
		RSAPublicKey publicKey = (RSAPublicKey) keypair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keypair.getPrivate();
		BigInteger N = publicKey.getModulus();

		
		BigInteger message = new BigInteger(1024, new SecureRandom());
		BigInteger r = AsymmetricEncryption.generateRForBlindSignature(N);
		
		BigInteger messageBlinded = AsymmetricEncryption.blind(message, r, publicKey);
		BigInteger signature = AsymmetricEncryption.sign(messageBlinded, privateKey);
		BigInteger signatureUnBlinded = AsymmetricEncryption.unblind(signature, N, r);
				
		assertTrue(AsymmetricEncryption.signatureVerification(message, signatureUnBlinded, publicKey));
	}
}
