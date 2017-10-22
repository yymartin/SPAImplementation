package storage.test;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import cryptographyBasics.AsymmetricEncryption;
import cryptographyBasics.MyKeyGenerator;
import server.client.Client;

public class ServerTest {
	public static void main(String[] args) {
		String username = "Yoan";
		KeyPair keys = MyKeyGenerator.generateAsymmetricKey();
		
		PublicKey svk = keys.getPublic();
		PrivateKey ssk = keys.getPrivate();
				
		Client.registerToServer(username, svk);
		
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BigInteger challenge = Client.askForChallengeToServer(username);
		System.out.println(challenge);
		BigInteger challengeSigned = AsymmetricEncryption.sign(challenge, (RSAPrivateKey) ssk);
		System.out.println(AsymmetricEncryption.signatureVerification(challenge, challengeSigned, (RSAPublicKey) svk));
		Client.executeChallengeToServer(username, challengeSigned);
	}
}
 