package cryptographyBasics;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class AsymmetricEncryption {
	
	//The key is public
	public static byte[] encrypt(String clearText, Key key){
		Cipher encryptorAlgorithm;
		byte[] encryptedByte = null;
		try {
			encryptorAlgorithm = Cipher.getInstance("RSA");
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
	
	//The key is private
	public static String decrypt(byte[] cipherText, Key key){
		Cipher decryptorAlgorithm;
		byte[] decryptedByte = null;
		try {
			decryptorAlgorithm = Cipher.getInstance("RSA");
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
	
	//Implementation of Sign(ssk, msg) (page 6)
	public static byte[] sign(PrivateKey secretSigningKey, String message) {
		byte[] signature = encrypt(message, secretSigningKey);
		
		return signature;
	}
	
	//Implementation of SigVerify(svk, msg, sig) (page 6)
	public static boolean signatureVerification(PublicKey secretVerificationKey, String message, byte[] signature) {
		String clearText = decrypt(signature, secretVerificationKey);
		
		return clearText.equals(message);
	}
}
