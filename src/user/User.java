package user;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import SSLUtility.ProtocolMode;
import cryptographyBasics.MyKeyGenerator;
import server.client.ServerClient;
import storage.client.StorageClient;

public class User {

	public static void main(String[] args) {
		String username = "Yoan";
		String password = "Martin";
		String website = "Bob";
		KeyPair blindKeys = MyKeyGenerator.generateAsymmetricKey();
		PublicKey bvk = blindKeys.getPublic();
		PrivateKey bsk = blindKeys.getPrivate();

		KeyPair digitalKeys = MyKeyGenerator.generateAsymmetricKey();
		PublicKey svk = digitalKeys.getPublic();
		PrivateKey ssk = digitalKeys.getPrivate();
		StorageClient storageConnector = new StorageClient(ProtocolMode.SERVER_OPTIMAL, username, password, website, bvk, bsk, ssk);
		ServerClient serverConnector = new ServerClient(ProtocolMode.SERVER_OPTIMAL, username, password, bsk, svk);
		
		//registration phase
		serverConnector.registerToServer();
		storageConnector.storeValuesToStorage();
		
		//authentication phase
		BigInteger[] storageValues = storageConnector.retrieveValuesFromStorage();
		BigInteger sig = storageValues[0];
		BigInteger ctext = storageValues[1];
		
		BigInteger challenge = serverConnector.askForChallengeToServer()[0];
		BigInteger response = null;
		serverConnector.executeChallengeToServer(response);
	}

}
