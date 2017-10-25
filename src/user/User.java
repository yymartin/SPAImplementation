package user;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.SecretKey;

import cryptographyBasics.AsymmetricEncryption;
import cryptographyBasics.Hash;
import cryptographyBasics.MyKeyGenerator;
import server.client.ServerClient;
import storage.client.StorageClient;

public class User {

	public static synchronized void main(String[] args) {
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
				
		//SERVER OPTIMAL

//		serverConnector = new ServerClient(username, svk);
//		storageConnector = new StorageClient(username, password, website, bsk, bvk, ssk, r);


//		//		registration phase
//		serverConnector.registerToServer();
//		storageConnector.storeValuesToStorage();

		//		authentication phase
//		keyFromStorage = storageConnector.retrieveValuesFromStorage(null);
//		challenge = serverConnector.askForChallengeToServer()[0];
//		System.out.println(challenge);
//		response = AsymmetricEncryption.sign(challenge, (RSAPrivateKey) keyFromStorage);
//		serverConnector.executeChallengeToServer(response);
		//should print "Connected!"


//		//STORAGE OPTIMAL
//
//		serverConnector = new ServerClient(username, password, bsk, bvk, svk, r);
//		storageConnector = new StorageClient(password, bsk, svk, ssk, r);
//
//		//		registration phase
//
//		serverConnector.registerToServer();
//		storageConnector.storeValuesToStorage();
//
//		//		authentication phase
//
//		BigInteger[] result = serverConnector.askForChallengeToServer();
//		BigInteger id = result[0];
//		challenge = result[1];
//		keyFromStorage = storageConnector.retrieveValuesFromStorage(id);
//		response = AsymmetricEncryption.sign(challenge, (RSAPrivateKey) keyFromStorage);
//		serverConnector.executeChallengeToServer(response);
//		//should print "Connected!"

	}

}

