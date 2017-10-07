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

public class SymmetricEncryption {
	
	public static byte[] encryptOneTimePadding(BigInteger clearText, byte[] key) {
		byte[] textInByte = clearText.toByteArray();
		
		if(textInByte.length > key.length) {
			throw new IllegalArgumentException("Message is too long");
		}
		
		byte[] encoded = new byte[key.length+1];
		
		//The last byte of the encryption is the actual binary size of the message
		String diff = Integer.toString(textInByte.length);
		encoded[key.length] = Byte.valueOf(diff);
		
		if(key.length != textInByte.length) {
			byte[] newTextInByte = new byte[key.length];
			
			//copy the old array in the new one
			for(int i = 0; i < textInByte.length; i++) {
				newTextInByte[i] = textInByte[i];
			}
			
			//add 0's in the remaining places
			for(int i = textInByte.length; i < newTextInByte.length; i++) {
				newTextInByte[i] = 0;
			}
			
			//XOR operation
			for(int i = 0; i < newTextInByte.length; i++) {
				encoded[i] = (byte) (newTextInByte[i] ^ key[i]);
			}
		} else {
			for(int i = 0; i < textInByte.length - 1; i++) {
				encoded[i] = (byte) (textInByte[i] ^ key[i]);
			}
		}	

		return encoded;
	}
	
	public static BigInteger decryptOneTimePadding(byte[] cipherText, byte[] key) {
		//We get the correct size of the message
		Byte lastByte = cipherText[cipherText.length-1];
		int finalLength = lastByte.intValue();
		
		byte[] decrypted = new byte[finalLength];

		for(int i = 0; i < finalLength; i++) {
			decrypted[i] = (byte) (cipherText[i] ^ key[i]);
		}
				
        return new BigInteger(decrypted);
	}

	public static byte[] encryptAES(BigInteger clearText, SecretKey key) {
		Cipher encryptorAlgorithm;
		byte[] encryptedByte = null;
		try {
			encryptorAlgorithm = Cipher.getInstance("AES");
			encryptorAlgorithm.init(Cipher.ENCRYPT_MODE, key);
			encryptedByte = encryptorAlgorithm.doFinal(clearText.toByteArray());
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

	public static BigInteger decryptAES(byte[] cipherText, SecretKey key) {
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
		return new BigInteger(decryptedByte);
	}

	//Implementation of Sign(ssk, msg) equivalent for MAC (page 6)
	public static byte[] sign(SecretKey secretSigningKey, BigInteger message) {
		return encryptAES(message, secretSigningKey);
	}

	//Implementation of SigVerify(svk, msg, sig) equivalent for MAC (page 6)
	public static boolean signatureVerification(SecretKey secretVerificationKey, BigInteger message, byte[] signature) {
		BigInteger clearText = decryptAES(signature, secretVerificationKey);
		return message.equals(clearText);
	}
	
	//Only use for challenge-response
	public static boolean verifyHMac(BigInteger message, byte[] hmac, SecretKey key) {
		byte[] hmacGenerated = generateHMac(message, key);
		return Arrays.equals(hmac, hmacGenerated);
	}
	
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
}
