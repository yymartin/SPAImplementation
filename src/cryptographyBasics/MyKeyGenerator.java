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

public class MyKeyGenerator {
	
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
	
	public static byte[] getOneTimePaddingKey() {
		SecureRandom random = new SecureRandom();
		byte[] key = new byte[128];
		random.nextBytes(key);;
		
		return key;
	}
	
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
	
	public static KeyPair generateAssymetricKey(){
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
	
	public static void generateAssymmetricKeyToFile(String address){
		KeyPair keyPair = generateAssymetricKey();
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
