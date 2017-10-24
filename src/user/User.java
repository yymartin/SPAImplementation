package user;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import SSLUtility.ProtocolMode;
import cryptographyBasics.AsymmetricEncryption;
import cryptographyBasics.Hash;
import cryptographyBasics.MyKeyGenerator;
import cryptographyBasics.SymmetricEncryption;
import server.client.ServerClient;
import storage.client.StorageClient;

public class User {

	public static synchronized void main(String[] args) {
		String address = System.getProperty("user.dir");
		String username = "Yoan";
		String password = "Martin";
		String website = "Bob";

		
//		PublicKey bvk = MyKeyGenerator.getPublicKeyFromFile(address,"blind");
//		PrivateKey bsk = MyKeyGenerator.getPrivateKeyFromFile(address,"blind");
//		BigInteger r = AsymmetricEncryption.generateRForBlindSignature(((RSAPrivateKey) bsk).getModulus());
//		PublicKey svk = MyKeyGenerator.getPublicKeyFromFile(address,"digital");
//		PrivateKey ssk = MyKeyGenerator.getPrivateKeyFromFile(address,"digital");
		
//		ServerClient serverConnector = new ServerClient(ProtocolMode.STORAGE_OPTIMAL, username, password, bsk, svk);
		
		//		registration phase
//		serverConnector.registerToServer();
//		storageConnector.storeValuesToStorage();

		//		authentication phase
//		PrivateKey keyFromStorage = storageConnector.retrieveValuesFromStorage();
//		BigInteger[] result = serverConnector.askForChallengeToServer();
//		BigInteger id = result[0];
//		StorageClient storageConnector = new StorageClient(ProtocolMode.STORAGE_OPTIMAL, id, password, website, bvk, bsk, ssk, r);

//		BigInteger challenge = result[1];
//		System.out.println(challenge);
//		BigInteger response = AsymmetricEncryption.sign(challenge, (RSAPrivateKey) keyFromStorage);
//		serverConnector.executeChallengeToServer(response);
	}

}

