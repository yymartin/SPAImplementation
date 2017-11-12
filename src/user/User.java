package user;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.SecretKey;

import cryptographyBasics.AsymmetricEncryption;
import cryptographyBasics.Hash;
import cryptographyBasics.MyKeyGenerator;
import cryptographyBasics.SymmetricEncryption;
import mobile.MobileClient;
import qrcode.QRCode;
import server.client.ServerClient;
import storage.client.StorageClient;

public class User {

	public static void main(String[] args) {
		String address = System.getProperty("user.dir");
		String username = "Yoan";
		String password = "Martin";
		String website = "Bob";
				
		PublicKey bvk = MyKeyGenerator.getPublicKeyFromFile(address, "blind");
		PrivateKey bsk = MyKeyGenerator.getPrivateKeyFromFile(address,"blind");
		BigInteger r = MyKeyGenerator.getRFromFile(address, "blind");
		PublicKey svk = MyKeyGenerator.getPublicKeyFromFile(address,"digital");
		PrivateKey ssk = MyKeyGenerator.getPrivateKeyFromFile(address,"digital");
		
		ServerClient serverConnector;
		StorageClient storageConnector;
		BigInteger challenge;
		BigInteger response;
		PrivateKey keyFromStorage;
								
		byte[] K = MyKeyGenerator.getOneTimePaddingKeyFromFile(System.getProperty("user.dir"));
		
		MobileClient.executeRegistration();
		
//		//SERVER OPTIMAL
//
//		serverConnector = new ServerClient(username, svk);
//		storageConnector = new StorageClient(username, password, website, bsk, bvk, ssk, r);
//
//
//		//		registration phase
//		serverConnector.registerToServer();
//		storageConnector.storeValuesToStorage();
//
//		//		authentication phase
//		keyFromStorage = storageConnector.retrieveValuesFromStorage(null, null);
//		challenge = serverConnector.askForChallengeToServer()[0];
//		response = AsymmetricEncryption.sign(challenge, (RSAPrivateKey) keyFromStorage);
//		serverConnector.executeChallengeToServer(response);
		//should print "Connected!"


//		//STORAGE OPTIMAL
//
//		serverConnector = new ServerClient(username, password, bsk, bvk, svk, r);
//		storageConnector = new StorageClient(SSLUtility.ProtocolMode.STORAGE_OPTIMAL, password, bsk, svk, ssk, r);

		//		registration phase

//		serverConnector.registerToServer();
//		storageConnector.storeValuesToStorage();
//
//		//		authentication phase
//
//		BigInteger[] result = serverConnector.askForChallengeToServer();
//		BigInteger id = result[0];
//		challenge = result[1];
//		keyFromStorage = storageConnector.retrieveValuesFromStorage(id, null);
//		response = AsymmetricEncryption.sign(challenge, (RSAPrivateKey) keyFromStorage);
//		serverConnector.executeChallengeToServer(response);
//		//should print "Connected!"
		
		
		//PRIVACY OPTIMAL
		
//		serverConnector = new ServerClient(username, password, bsk, bvk, svk, r);
//		storageConnector = new StorageClient(SSLUtility.ProtocolMode.PRIVACY_OPTIMAL, password, bsk, svk, ssk, r);
//		
//		//		registration phase
////		
//		serverConnector.registerToServer();
//		PublicKey obliviousTransferKey = storageConnector.storeValuesToStorage();
//		storePublicKeyToFile(obliviousTransferKey);
		
		//		authentication phase

//		BigInteger[] result = serverConnector.askForChallengeToServer();
//		BigInteger id = result[0];
//		challenge = result[1];
//		PublicKey obliviousTransferKey = getPublicKeyFromFile();
//		keyFromStorage = storageConnector.retrieveValuesFromStorage(id, obliviousTransferKey);
//		response = AsymmetricEncryption.sign(challenge, (RSAPrivateKey) keyFromStorage);
//		serverConnector.executeChallengeToServer(response);
		//should print "Connected!"
		
		
		//MOBILE PROCOTOL
		
//		serverConnector = new ServerClient(username, K);
		
		//		registration phase
		
//		serverConnector.registerToServer();
		
		//		authentication phase
		
//		serverConnector.askForChallengeToServer();

	}
	
	public static void storePublicKeyToFile(PublicKey key) {
		String address = "/Users/yoanmartin/Desktop";
		Path path = Paths.get(address+"/Server-Key");
		try {
			Files.write(path, key.getEncoded());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static PublicKey getPublicKeyFromFile() {
		String address = "/Users/yoanmartin/Desktop";
		Path path = Paths.get(address+"/Server-Key");
		byte[] key = null;
		try {
			key = Files.readAllBytes(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return MyKeyGenerator.convertByteArrayIntoPublicKey(key);
	}

}

