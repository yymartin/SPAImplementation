package cryptographyBasics;

import java.io.IOException;
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
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
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
	 * Function which generate a public and a private key for asymmetric encryption
	 * @return A KeyPair containing the RSA public and private key
	 */
	public static KeyPair generateAsymmetricKey(){
		KeyPair keys = null;
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(2048);
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
	 */
	public static void generateAsymmetricKeyToFile(String address){
		KeyPair keyPair = generateAsymmetricKey();
		byte[] privateKey = keyPair.getPrivate().getEncoded();
		byte[] publicKey = keyPair.getPublic().getEncoded();
		Path privatePath = Paths.get(address+"/Private-Key");
		Path publicPath = Paths.get(address+"/Public-Key");
		try {
			Files.write(privatePath, privateKey);
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
	 * @return The RSA public key
	 */
	public static PublicKey getPublicKeyFromFile(String address){
		PublicKey key = null;
		try {			
			Path publicKeyPath = Paths.get(address+"/Public-Key");
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
	 * @return The RSA private key
	 */
	public static PrivateKey getPrivateKeyFromFile(String address){
		PrivateKey key = null;
		
		try {
			Path privateKeyPath = Paths.get(address+"/Private-Key");
			byte[] encodedPrivateKey = Files.readAllBytes(privateKeyPath);
			
			KeyFactory kf = KeyFactory.getInstance("RSA"); 
			key = kf.generatePrivate(new PKCS8EncodedKeySpec(encodedPrivateKey));
		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return key;
	}
}
