package cryptographyBasics;

import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Random;

public class AsymmetricEncryption {	
	public static BigInteger encrypt(BigInteger message, RSAPublicKey key) {
		BigInteger m = message;
		BigInteger e = key.getPublicExponent();
		BigInteger N = key.getModulus();
		
		return m.modPow(e, N);	
	}
	
	public static BigInteger decrypt(BigInteger cipherText, RSAPrivateKey key) {
		BigInteger d = key.getPrivateExponent();
		BigInteger N = key.getModulus();
		
		return cipherText.modPow(d, N);
	}
	
	//Implementation of Sign(ssk, msg) (page 6)
	public static BigInteger sign(RSAPrivateKey key, BigInteger message) {
		BigInteger d = key.getPrivateExponent();
		BigInteger N = key.getModulus();
		
		return message.modPow(d, N);	
	}
	
	//Implementation of SigVerify(svk, msg, sig) (page 6)
	public static boolean signatureVerification(RSAPublicKey key, BigInteger message, BigInteger signature) {
		BigInteger e = key.getPublicExponent();
		BigInteger N = key.getModulus();
		
		BigInteger clearText = signature.modPow(e, N);
		
		return clearText.equals(message);
	}

	public static BigInteger blind(BigInteger message, RSAPublicKey publicKey, BigInteger r) {
		BigInteger e = publicKey.getPublicExponent();
		BigInteger N = publicKey.getModulus();

		return message.multiply(r.modPow(e, N)).mod(N);
	}
	
	public static BigInteger unblind(BigInteger signature, BigInteger N, BigInteger r) {
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
