package cryptographyBasics;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import javax.xml.bind.DatatypeConverter;

public class Hash {
	//Source: https://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
	public static String generatePBKDF2WithHmacSHA1Hash(String password) {
		int iterations = 1000;
		char[] chars = password.toCharArray();
		byte[] salt = generateSalt();

		PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 512);
		SecretKeyFactory skf = null;
		byte[] hash = null;
		try {
			skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			hash = skf.generateSecret(spec).getEncoded();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String saltInHex = DatatypeConverter.printHexBinary(salt);
		String hashInHex = DatatypeConverter.printHexBinary(hash);

		return iterations + ":" + saltInHex + ":" + hashInHex;
	}

	public static boolean validatePassword(String originalPassword, String storedPassword) {
		String[] parts = storedPassword.split(":");
		int iterations = Integer.parseInt(parts[0]);
		byte[] salt = DatatypeConverter.parseHexBinary(parts[1]);
		byte[] hash = DatatypeConverter.parseHexBinary(parts[2]);

		PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
		SecretKeyFactory skf = null;
		byte[] testHash = null;
		try {
			skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			testHash = skf.generateSecret(spec).getEncoded();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Arrays.equals(hash, testHash);
	}
	
	public static BigInteger generateSHA256Hash(BigInteger number) {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] hash = digest.digest(number.toByteArray());
		return new BigInteger(hash);
	}

	private static byte[] generateSalt() {
		SecureRandom sr = null;
		try {
			sr = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		byte[] salt = new byte[16];
		sr.nextBytes(salt);

		return salt;
	}
}
