package cryptographyBasics;

import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Random;

public class AsymmetricEncryption {	
	public static BigInteger encrypt(String message, RSAPublicKey key) {
		BigInteger m = new BigInteger(message.getBytes());
		BigInteger e = key.getPublicExponent();
		BigInteger N = key.getModulus();
		
		return m.modPow(e, N);	
	}
	
	public static String decrypt(BigInteger cipherText, RSAPrivateKey key) {
		BigInteger d = key.getPrivateExponent();
		BigInteger N = key.getModulus();
		
		return new String(cipherText.modPow(d, N).toByteArray());
	}
	
	//Implementation of Sign(ssk, msg) (page 6)
	public static BigInteger sign(RSAPrivateKey key, BigInteger message) {
		BigInteger d = key.getPrivateExponent();
		BigInteger N = key.getModulus();
		
		return message.modPow(d, N);	
	}
	
	//Implementation of SigVerify(svk, msg, sig) (page 6)
	public static boolean signatureVerification(RSAPublicKey key, String message, BigInteger signature) {
		BigInteger e = key.getPublicExponent();
		BigInteger N = key.getModulus();
		
		String clearText = new String(signature.modPow(e, N).toByteArray());
		
		return clearText.equals(message);
	}

	public static BigInteger blind(String message, RSAPublicKey publicKey, BigInteger r) {
		BigInteger messageAsInteger = new BigInteger(message.getBytes());
		BigInteger e = publicKey.getPublicExponent();
		BigInteger N = publicKey.getModulus();

		return messageAsInteger.multiply(r.modPow(e, N)).mod(N);
	}
	
	public static BigInteger unblind(BigInteger signature, RSAPrivateKey privateKey, BigInteger r) {
		BigInteger N = privateKey.getModulus();
		BigInteger rInverse = r.modInverse(N);
		
		return signature.multiply(rInverse).mod(N);
	}

	
	public static BigInteger generateRForBlindSignature(BigInteger mod) {
		BigInteger r;
		Random rand = new Random();
		do {
			r = new BigInteger(512, rand);
		} while(r.gcd(mod) == BigInteger.valueOf(1));
		
		return r;
	}
}
