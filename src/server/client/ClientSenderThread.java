package server.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;

import server.ClientToServerMode;

public class ClientSenderThread extends Thread implements Runnable {
	private DataOutputStream out;
	ClientToServerMode mode;
	byte[] username, svk, challenge;


	public ClientSenderThread(DataOutputStream out, ClientToServerMode mode, String username, PublicKey svk) {
		this.out = out;
		this.mode = mode;
		this.username = username.getBytes();
		this.svk = svk.getEncoded();
	}

	public ClientSenderThread(DataOutputStream out, ClientToServerMode mode, String username) {
		this.out = out;
		this.mode = mode;
		this.username = username.getBytes();
	}

	public ClientSenderThread(DataOutputStream out, ClientToServerMode mode, String username, BigInteger challenge) {
		this.out = out;
		this.mode = mode;
		this.username = username.getBytes();
		this.challenge = challenge.toByteArray();
	}


	@Override
	public void run() {	
		byte[] modeAsByte = mode.toString().getBytes();
		switch(mode) {
		case REGISTER:
			try {
				out.writeInt(modeAsByte.length);
				out.write(modeAsByte);
				out.writeInt(username.length);
				out.write(username);
				out.writeInt(svk.length);
				out.write(svk);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case CHALLENGE:
			try {
				out.writeInt(modeAsByte.length);
				out.write(modeAsByte);
				out.writeInt(username.length);
				out.write(username);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case AUTH:
			try {
				out.writeInt(modeAsByte.length);
				out.write(modeAsByte);
				out.writeInt(username.length);
				out.write(username);
				out.writeInt(challenge.length);
				out.write(challenge);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
	}
}
