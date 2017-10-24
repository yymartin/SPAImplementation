package cryptographyBasics;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author yoanmartin
 * Library containing functions to generate keys
 */
public class MyKeyGenerator {

	/**
	 * Function which generate a key for HMacSHA256 signature
	 * @return The SecretKey for the signature
	 */
	public static SecretKey generateHMacKey() {
		KeyGenerator keyGen = null;
		try {
			keyGen = KeyGenerator.getInstance("HmacSHA256");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		keyGen.init(128);
		return keyGen.generateKey();
	}

	/**
	 * Function which generate a key for one time padding encryption
	 * @param length The length of the key 
	 * @return The key as a byte array
	 */
	public static byte[] getOneTimePaddingKey(int length) {
		SecureRandom random = new SecureRandom();
		byte[] key = new byte[length];
		random.nextBytes(key);;

		return key;
	}

	/**
	 * Function which generate an AES key for symmetric encryption
	 * @return A SecretKey for AES encryption
	 */
	public static SecretKey generateSymmetricKey(){
		SecretKey key = null;
		try {
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(128);
			key = keyGen.generateKey();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return key;
	}

	/**
	 * Function which generates an AES key from a given password. The password should be hashed, so it should be a BigInteger
	 * @param password The hashed password
	 * @return A SecretKey for AES encryption
	 */
	public static SecretKey generateAESKeyFromPassword(BigInteger password) {
		String passwordAsString = new String(password.toString());
		SecretKeyFactory f = null;
		try {
			f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		KeySpec spec = new PBEKeySpec(passwordAsString.toCharArray(), Hash.generateSalt(), 10, 128);
		SecretKey s = null;
		try {
			s = f.generateSecret(spec);
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new SecretKeySpec(s.getEncoded(), "AES");
	}

	/**
	 * Function which generate a public and a private key for asymmetric encryption
	 * @return A KeyPair containing the RSA public and private key
	 */
	public static KeyPair generateAsymmetricKey(){
		KeyPair keys = null;
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(1024);
			keys = keyGen.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return keys;
	}

	/**
	 * Function which generate an AES key for symmetric encryption and store it in a file
	 * @param address The location where the key must be stored
	 */
	public static void generateSymmetricKeyToFile(String address){
		byte[] key = generateSymmetricKey().getEncoded();
		Path path = Paths.get(address+"/AES-Key");
		try {
			Files.write(path, key);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Function which generate a public and a private key for asymmetric encryption and store them in two files
	 * @param address The location where the keys must be stored
	 * @param title The title of the file
	 */
	public static void generateAsymmetricKeyToFile(String address, String title){
		KeyPair keyPair = generateAsymmetricKey();
		byte[] privateKey = keyPair.getPrivate().getEncoded();
		byte[] publicKey = keyPair.getPublic().getEncoded();
		byte[] r = AsymmetricEncryption.generateRForBlindSignature(((RSAPrivateKey)keyPair.getPrivate()).getModulus()).toByteArray();
		List<byte[]> privateList = new ArrayList<>();
		privateList.add(privateKey);
		privateList.add(r);

		ObjectOutputStream out;
		try {
			out = new ObjectOutputStream(new FileOutputStream(address+"/Private-Key-"+title));
			out.writeObject(privateList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Path publicPath = Paths.get(address+"/Public-Key-"+title);
		try {
			Files.write(publicPath, publicKey);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Function which recover the AES key from a file for symmetric encryption
	 * @param address The location where the key is stored
	 * @return The AES key
	 */
	public static SecretKey getSymmetricKeyFromFile(String address){
		SecretKey key = null;
		try {
			Path keyPath = Paths.get(address+"/AES-Key");
			byte[] encodedKey = Files.readAllBytes(keyPath);
			key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return key;
	}

	/**
	 * Function which recover the RSA public key from a file for asymmetric encryption
	 * @param address The location where the RSA public key is stored
	 * @param title The title of the file
	 * @return The RSA public key
	 */
	public static PublicKey getPublicKeyFromFile(String address, String title){
		PublicKey key = null;
		try {			
			Path publicKeyPath = Paths.get(address+"/Public-Key-"+title);
			byte[] encodedPublicKey = Files.readAllBytes(publicKeyPath);

			KeyFactory kf = KeyFactory.getInstance("RSA"); 
			key = kf.generatePublic(new X509EncodedKeySpec(encodedPublicKey));
		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return key;
	}


	/**
	 * Function which recover the RSA private key from a file for asymmetric encryption
	 * @param address The location where the RSA private key is stored
	 * @param title the title of the file
	 * @return The RSA private key
	 */
	@SuppressWarnings("unchecked")
	public static PrivateKey getPrivateKeyFromFile(String address, String title){
		PrivateKey key = null;

		ObjectInputStream in;
		List<byte[]> privateList = null;
		try {
			in = new ObjectInputStream(new FileInputStream(address+"/Private-Key-"+title));
			privateList = (List<byte[]>) in.readObject();

		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		key = MyKeyGenerator.convertByteArrayIntoPrivateKey(privateList.get(0));

		return key;
	}

	
	/**
	 * Function which return the blind factor contained in the private key
	 * @param address The address of the file
	 * @param title The name of the file
	 * @return The blind factor as a BigInteger
	 */
	@SuppressWarnings("unchecked")
	public static BigInteger getRFromFile(String address, String title){
		BigInteger r = null;

		ObjectInputStream in;
		List<byte[]> privateList = null;
		try {
			in = new ObjectInputStream(new FileInputStream(address+"/Private-Key-"+title));
			privateList = (List<byte[]>) in.readObject();

		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		r = new BigInteger(privateList.get(1));

		return r;
	}


	/**
	 * Function which generate an RSAPrivateKey from a byte array. This byte array has been generated using key.getEncoded()
	 * @param key The key as a byte array
	 * @return The RSAPrivateKey 
	 */
	public static RSAPrivateKey convertByteArrayIntoPrivateKey(byte[] key) {
		RSAPrivateKey privateKey = null;
		try {
			privateKey =  (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(key));
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return privateKey;
	}

	/**
	 * Function which generate an RSAPublicKey from a byte array. This byte array has been generated using key.getEncoded()
	 * @param key The key as a byte array
	 * @return The RSAPublicKey 
	 */
	public static RSAPublicKey convertByteArrayIntoPublicKey(byte[] key) {
		RSAPublicKey publicKey = null;
		try {
			publicKey =  (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(key));
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return publicKey;
	}
}
