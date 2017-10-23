package server.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import SSLUtility.ProtocolMode;
import cryptographyBasics.AsymmetricEncryption;
import cryptographyBasics.MyKeyGenerator;
import server.ClientToServerMode;

public class ClientAdministratorThread extends Thread implements Runnable{
	private DataInputStream in;
	private DataOutputStream out;

	public ClientAdministratorThread(DataInputStream in, DataOutputStream out){
		this.in = in;
		this.out = out;
	}

	public void run() {
		ProtocolMode protocol = ProtocolMode.valueOf(new String(getData()));
		ClientToServerMode mode = ClientToServerMode.valueOf(new String(getData()));
		String username;
		BigInteger password;
		PublicKey svk;
		PrivateKey bsk;
		Client client;
		
		switch(protocol) {
		case SERVER_OPTIMAL:
			switch(mode) {
			case REGISTER :
				username = new String(getData());
				svk = MyKeyGenerator.convertByteArrayIntoPublicKey(getData());
				client = new Client(username, svk);
				Server.clients.put(username, client);
				System.out.println(Server.clients);
				break;
			case CHALLENGE :
				username = new String(getData());
				BigInteger challenge = new BigInteger(100, new Random());
				client = Server.clients.get(username);
				client.setChallenge(challenge);
				Server.ex.execute(new ChallengeSenderThread(out, ProtocolMode.SERVER_OPTIMAL, challenge));
				break;
			case AUTH :
				username = new String(getData());
				BigInteger challengeSigned = new BigInteger(getData());
				client = Server.clients.get(username);
				if(client.isReadyToAuth()) {
					boolean result = AsymmetricEncryption.signatureVerification(client.getChallenge() , challengeSigned, (RSAPublicKey) client.getSvk());
					if(result) {
						System.out.println("Connected!");
						client.isAuthenticated();
					} else {
						System.out.println("Not connected");
					}
				}
				break;
			}
			break;
				
		case STORAGE_OPTIMAL:
			switch(mode) {
			case REGISTER : 
				username = new String(getData());
				svk = MyKeyGenerator.convertByteArrayIntoPublicKey(getData());
				bsk = MyKeyGenerator.convertByteArrayIntoPrivateKey(getData());
				client = new Client(username, svk, bsk);
				Server.clients.put(username, client);
				System.out.println(Server.clients.keySet());
				break;
			case CHALLENGE :
				username = new String(getData());
				password = new BigInteger(getData());
				Client actualClient = Server.clients.get(username);
				bsk = actualClient.getBsk();
				BigInteger id = AsymmetricEncryption.sign(password, (RSAPrivateKey) bsk);
				BigInteger challenge = new BigInteger(100, new Random());
				actualClient.setChallenge(challenge);
				Server.ex.execute(new ChallengeSenderThread(out, ProtocolMode.STORAGE_OPTIMAL, id, challenge));
				break;
			case AUTH :
				username = new String(getData());
				BigInteger challengeSigned = new BigInteger(getData());
				client = Server.clients.get(username);
				if(client.isReadyToAuth()) {
					boolean result = AsymmetricEncryption.signatureVerification(client.getChallenge() , challengeSigned, (RSAPublicKey) client.getSvk());
					if(result) {
						System.out.println("Connected!");
						client.isAuthenticated();
					} else {
						System.out.println("Not connected");
					}
				}
				break;
			}
			break;
			
		case PRIVACY_OPTIMAL:
			
			break;
		}

		Thread.currentThread().interrupt();
	}

	private byte[] getData() {
		byte[] id = null;
		try {
			int length = in.readInt();
			if(length > 0) {
				id = new byte[length];
				in.readFully(id, 0, id.length); 
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return id;
	}

}