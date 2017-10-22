package server.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
		ClientToServerMode mode = ClientToServerMode.valueOf(new String(getData()));

		String username;
		PublicKey svk;

		Client client;

		switch(mode) {
		case REGISTER : 
			username = new String(getData());
			svk = MyKeyGenerator.convertByteArrayIntoPublicKey(getData());
			client = new Client(username, svk);
			Server.clients.put(username, client);
			break;
		case CHALLENGE :
			username = new String(getData());
			BigInteger challenge = new BigInteger(100, new Random());
			client = Server.clients.get(username);
			client.setChallenge(challenge);
			ExecutorService ex = Executors.newSingleThreadExecutor();
			ex.execute(new ChallengeSenderThread(out, challenge));
			break;
		case AUTH :
			username = new String(getData());
			BigInteger challengeSigned = new BigInteger(getData());
			client = Server.clients.get(username);
			if(client.isReadyToAuth()) {
				System.out.println(client.getChallenge());
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
