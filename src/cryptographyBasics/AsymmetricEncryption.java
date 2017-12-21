package cryptographyBasics;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * @author yoanmartin
 * Library containing cryptographic functions concerning asymmetric encryption
 */
public class AsymmetricEncryption {	
	/**
	 * Function which encrypt a message using RSA algorithm
	 * @param message The BigInteger to encrypt
	 * @param key The RSA public key
	 * @return The BigInteger encrypted
	 */
	public static BigInteger encrypt(BigInteger message, RSAPublicKey key) {
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return new BigInteger(cipher.doFinal(message.toByteArray()));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Function which decrypt a message using RSA algorithm
	 * @param cipherText The encrypted BigInteger
	 * @param key The RSA private key
	 * @return The BigInteger decrypted
	 */
	public static BigInteger decrypt(BigInteger cipherText, RSAPrivateKey key) {
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, key);
			return new BigInteger(cipher.doFinal(cipherText.toByteArray()));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static BigInteger sign(BigInteger message, RSAPrivateKey key) {
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return new BigInteger(cipher.doFinal(message.toByteArray()));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e1) {
			e1.printStackTrace();
		}

		return null;
	}

	public static boolean signatureVerification(BigInteger message, BigInteger signature, RSAPublicKey key) {
		BigInteger clearText = null;
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, key);
			clearText = new BigInteger(cipher.doFinal(signature.toByteArray()));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e1) {
			e1.printStackTrace();
		}

		return clearText.equals(message);
	}


	/**
	 * Function which sign a message using RSA algorithm
	 * @param message The BigInteger to sign
	 * @param key The RSA private key
	 * @return The BigInteger signed
	 */
	public static BigInteger blindSign(BigInteger message, RSAPrivateKey key) {
		BigInteger d = key.getPrivateExponent();
		BigInteger N = key.getModulus();

		return message.modPow(d, N);	
	}


	/**
	 * Function which verify the signature of a message
	 * @param message The unsigned BigInteger
	 * @param signature The signed BigInteger
	 * @param key The RSA public key
	 * @return True if the verification is correct, False otherwise
	 */
	public static boolean blindSignatureVerification(BigInteger message, BigInteger signature, RSAPublicKey key) {
		//		BigInteger clearText = null;
		//		try {
		//			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		//		    cipher.init(Cipher.DECRYPT_MODE, key);
		//		    clearText = new BigInteger(cipher.doFinal(signature.toByteArray()));
		//		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e1) {
		//			e1.printStackTrace();
		//		}

		BigInteger e = key.getPublicExponent();
		BigInteger N = key.getModulus();

		BigInteger clearText = signature.modPow(e, N);

		return clearText.equals(message);
	}

	/**
	 * Function which blind a message in the case of RSA blind signature
	 * @param message The BigInteger to blind
	 * @param r The BigInteger used as the blind factor
	 * @param publicKey The RSA public key
	 * @return The BigInteger blinded
	 */
	public static BigInteger blind(BigInteger message, BigInteger r, RSAPublicKey publicKey) {
		BigInteger e = publicKey.getPublicExponent();
		BigInteger N = publicKey.getModulus();

		return message.multiply(r.modPow(e, N)).mod(N);
	}

	/**
	 * Function which unblind a message in the case of RSA blind signature
	 * @param signature The BigInteger signed by the authority
	 * @param N The modulus of the RSA key
	 * @param r The BigInteger used as blind factor
	 * @return The BigInteger unblinded
	 */
	public static BigInteger unblind(BigInteger signature, BigInteger N, BigInteger r) {
		BigInteger rInverse = r.modInverse(N);

		return signature.multiply(rInverse).mod(N);
	}


	/**
	 * Function which generate a blind factor in the case of RSA blind signature
	 * @param mod The modulus of the RSA key
	 * @return The BigInteger blind factor
	 */
	public static BigInteger generateRForBlindSignature(BigInteger mod) {
		BigInteger r;
		do {
			r = new BigInteger(1024, new SecureRandom());
		} while(r.gcd(mod) == BigInteger.ONE);

		return r;
	}
}
