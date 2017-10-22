package server.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;

import SSLUtility.ProtocolMode;
import server.ClientToServerMode;

public class ClientSenderThread extends Thread implements Runnable {
	private DataOutputStream out;
	ProtocolMode protocol;
	ClientToServerMode mode;
	byte[] username, svk, bsk, password, challenge;


	public ClientSenderThread(DataOutputStream out, ProtocolMode protocol, ClientToServerMode mode, String username, PublicKey svk) {
		this.out = out;
		this.mode = mode;
		this.username = username.getBytes();
		this.svk = svk.getEncoded();
	}

	public ClientSenderThread(DataOutputStream out, ProtocolMode protocol, ClientToServerMode mode, String username) {
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


	public ClientSenderThread(DataOutputStream out, ProtocolMode protocol, ClientToServerMode mode, String username, PublicKey svk, PrivateKey bsk) {
		this.out = out;
		this.protocol = protocol;
		this.mode = mode;
		this.username = username.getBytes();
		this.svk = svk.getEncoded();
		this.bsk = bsk.getEncoded();
	}

	public ClientSenderThread(DataOutputStream out, ProtocolMode protocol, ClientToServerMode mode, String username, BigInteger password) {
		this.out = out;
		this.protocol = protocol;
		this.mode = mode;
		this.username = username.getBytes();
		this.password = password.toByteArray();
	}

	@Override
	public void run() {	
		byte[] protocolAsByte = mode.toString().getBytes();
		byte[] modeAsByte = mode.toString().getBytes();
		
		switch(protocol) {
		case SERVER_OPTIMAL:
			switch(mode) {
			case REGISTER:
				try {
					out.writeInt(protocolAsByte.length);
					out.write(protocolAsByte);
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
					out.writeInt(protocolAsByte.length);
					out.write(protocolAsByte);
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
			break;
			
		case STORAGE_OPTIMAL:
			switch(mode) {
			case REGISTER:
				try {
					out.writeInt(protocolAsByte.length);
					out.write(protocolAsByte);
					out.writeInt(modeAsByte.length);
					out.write(modeAsByte);
					out.writeInt(username.length);
					out.write(username);
					out.writeInt(svk.length);
					out.write(svk);
					out.writeInt(bsk.length);
					out.write(bsk);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case CHALLENGE:
				try {
					out.writeInt(protocolAsByte.length);
					out.write(protocolAsByte);
					out.writeInt(modeAsByte.length);
					out.write(modeAsByte);
					out.writeInt(username.length);
					out.write(username);
					out.writeInt(password.length);
					out.write(password);
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
			break;
			
		case PRIVACY_OPTIMAL:
			
			break;
		}
	}
}
