package cryptographyBasics;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * @author yoanmartin
 * Library containing cryptographic functions concerning symmetric encryption
 */
public class SymmetricEncryption {

	/**
	 * Function which encrypt a message using one time padding encryption
	 * @param message The BigInteger to be encrypted
	 * @param key The key as a byte array
	 * @return The message encrypted as a byte array
	 */
	public static byte[] encryptOneTimePadding(byte[] message, byte[] key) {
		byte[] encoded = new byte[key.length];

		for(int i = 0; i < message.length; i++) {
			encoded[i] = (byte) (message[i] ^ key[i]);
		}	

		return encoded;
	}

	/**
	 * Function which decrypt a message using one time padding encryption
	 * @param cipherText The message encrypted as a byte array
	 * @param key The key as a byte array
	 * @return The decrypted message as a BigInteger
	 */
	public static byte[] decryptOneTimePadding(byte[] cipherText, byte[] key) {
		byte[] decrypted = new byte[cipherText.length];

		for(int i = 0; i < decrypted.length; i++) {
			decrypted[i] = (byte) (cipherText[i] ^ key[i]);
		}

		return decrypted;
	}

	/**
	 * Function which encrypt a message using AES algorithm
	 * @param message The BigInteger to be encrypted
	 * @param key The AES key
	 * @return The message encrypted as a byte array
	 */
	public static byte[] encryptAES(byte[] message, SecretKey key) {
		Cipher encryptorAlgorithm;
		byte[] encryptedByte = null;
		try {
			encryptorAlgorithm = Cipher.getInstance("AES");
			encryptorAlgorithm.init(Cipher.ENCRYPT_MODE, key);
			encryptedByte = encryptorAlgorithm.doFinal(message);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encryptedByte;
	}

	/**
	 * Function which decrypt a message using AES algorithm
	 * @param cipherText The message encrypted as a byte array
	 * @param key The AES key
	 * @return The decrypted message as a BigInteger
	 */
	public static byte[] decryptAES(byte[] cipherText, SecretKey key) {
		Cipher decryptorAlgorithm;
		byte[] decryptedByte = null;
		try {
			decryptorAlgorithm = Cipher.getInstance("AES");
			decryptorAlgorithm.init(Cipher.DECRYPT_MODE, key);
			decryptedByte = decryptorAlgorithm.doFinal(cipherText);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return decryptedByte;
	}

	/**
	 * Function which sign a message using HMacAES
	 * @param secretSigningKey The AES key
	 * @param message The BigInteger to be signed
	 * @return The signed message as a byte array
	 */
	public static byte[] sign(SecretKey secretSigningKey, BigInteger message) {
		return encryptAES(message.toByteArray(), secretSigningKey);
	}

	/**
	 * Function which verify the signature of a HMacAES
	 * @param secretVerificationKey The HMacAES key
	 * @param message The original message
	 * @param signature The signed message
	 * @return True if the verification is correct, False otherwise
	 */
	public static boolean signatureVerification(SecretKey secretVerificationKey, BigInteger message, byte[] signature) {
		BigInteger clearText = new BigInteger(decryptAES(signature, secretVerificationKey));
		return message.equals(clearText);
	}

	/**
	 * Function which sign a message using HMacSHA256
	 * @param message The message to be signed
	 * @param key The HMacSHA256 key
	 * @return The signed message as a byte array
	 */
	public static byte[] generateHMac(BigInteger message, SecretKey key) {
		byte[] finalHmac = null;
		try {
			Mac generator = Mac.getInstance("HmacSHA256");
			generator.init(key);
			finalHmac = generator.doFinal(message.toByteArray());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return finalHmac;
	}
	
	/**
	 * Function which verify the signature of a HMacSHA256
	 * @param message The original message
	 * @param hmac The signed message
	 * @param key The HMacSHA256 key
	 * @return True if the verification is correct, False otherwise
	 */
	public static boolean verifyHMac(BigInteger message, byte[] hmac, SecretKey key) {
		byte[] hmacGenerated = generateHMac(message, key);
		return Arrays.equals(hmac, hmacGenerated);
	}
}
