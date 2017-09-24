package cryptographyBasics;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class AsymmetricEncryption {
	public static byte[] encrypt(String clearText, PublicKey publicKey){
		Cipher encryptorAlgorithm;
		byte[] encryptedByte = null;
		try {
			encryptorAlgorithm = Cipher.getInstance("RSA");
			encryptorAlgorithm.init(Cipher.ENCRYPT_MODE, publicKey);
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

	public static String decrypt(byte[] cipherText, PrivateKey privateKey){
		Cipher decryptorAlgorithm;
		byte[] decryptedByte = null;
		try {
			decryptorAlgorithm = Cipher.getInstance("RSA");
			decryptorAlgorithm.init(Cipher.DECRYPT_MODE, privateKey);
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
		Cipher encryptorAlgorithm;
		byte[] signature = null;
		try {
			encryptorAlgorithm = Cipher.getInstance("RSA");
			encryptorAlgorithm.init(Cipher.ENCRYPT_MODE, secretSigningKey);
			signature = encryptorAlgorithm.doFinal(message.getBytes());
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
		return signature;
	}
	
	//Implementation of SigVerify(svk, msg, sig) (page 6)
	public static boolean signatureVerification(PublicKey secretVerificationKey, String message, byte[] signature) {
		Cipher decryptorAlgorithm;
		byte[] decryptedByte = null;
		try {
			decryptorAlgorithm = Cipher.getInstance("RSA");
			decryptorAlgorithm.init(Cipher.DECRYPT_MODE, secretVerificationKey);
			decryptedByte = decryptorAlgorithm.doFinal(signature);
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
		
		String clearText = new String(decryptedByte);
		
		return clearText.equals(message);
	}
}
