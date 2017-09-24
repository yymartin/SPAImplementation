package cryptographyBasics;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class SymmetricEncryption {
	public static byte[] encrypt(String clearText, SecretKey key) {
		Cipher encryptorAlgorithm;
		byte[] encryptedByte = null;
		try {
			encryptorAlgorithm = Cipher.getInstance("AES");
			encryptorAlgorithm.init(Cipher.ENCRYPT_MODE, key);
			encryptedByte = encryptorAlgorithm.doFinal(clearText.getBytes());
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

	public static String decrypt(byte[] cipherText, SecretKey key) {
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
		return new String(decryptedByte);
	}
	
	//Implementation of Sign(ssk, msg) equivalent for MAC (page 6)
	public static byte[] sign(SecretKey secretSigningKey, String message) {
		return encrypt(message, secretSigningKey);
	}
	
	//Implementation of SigVerify(svk, msg, sig) equivalent for MAC (page 6)
	public static boolean signatureVerification(SecretKey secretVerificationKey, String message, byte[] signature) {
		String clearText = decrypt(signature, secretVerificationKey);
		return message.equals(clearText);
	}
}
