package server.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;

import SSLUtility.ProtocolMode;
import server.ClientToServerMode;

/**
 * @author yoanmartin
 * Instantiation of a thread which sends information to a server
 */
public class ClientSenderThread extends Thread implements Runnable {
	private DataOutputStream out;
	ProtocolMode protocol;
	ClientToServerMode mode;
	byte[] username, svk, bsk, password, challenge, k;


	/**
	 * Constructor used when the client registers to the server using the Server Optimal protocol
	 * @param out The DataOutputStream given by the client 
	 * @param protocol The protocol used 
	 * @param mode The actual client state from the server point of view
	 * @param username
	 * @param svk
	 */
	public ClientSenderThread(DataOutputStream out, ProtocolMode protocol, ClientToServerMode mode, String username, PublicKey svk) {
		this.out = out;
		this.protocol = protocol;
		this.mode = mode;
		this.username = username.getBytes();
		this.svk = svk.getEncoded();
	}

	/**
	 * Constuctor used when the client asks for a challenge using the Server Optimal protocol
	 * @param out The DataOutputStream given by the server 
	 * @param protocol The protocol used 
	 * @param mode The actual client state from the server point of view
	 * @param username The username of the client
	 */
	public ClientSenderThread(DataOutputStream out, ProtocolMode protocol, ClientToServerMode mode, String username) {
		this.out = out;
		this.protocol = protocol;
		this.mode = mode;
		this.username = username.getBytes();
	}

	/**
	 * Constructor used when the client sends the response to a challenge using the Server Optimal or Storage Optimal protocol
	 * @param out The DataOutputStream given by the server 
	 * @param mode The actual client state from the server point of view
	 * @param username The username of the client
	 * @param challenge The challenge received by the client
	 */
	public ClientSenderThread(DataOutputStream out, ClientToServerMode mode, String username, BigInteger challenge) {
		this.out = out;
		this.protocol = SSLUtility.ProtocolMode.SERVER_OPTIMAL;
		this.mode = mode;
		this.username = username.getBytes();
		this.challenge = challenge.toByteArray();
	}


	/**
	 * Constructor used when the client registers to the server using the Storage Optimal protocol
	 * @param out The DataOutputStream given by the server 
	 * @param protocol The protocol used 
	 * @param mode The actual client state from the server point of view
	 * @param username The username of the client
	 * @param svk The svk of the client
	 * @param bsk The bsk of the client
	 */
	public ClientSenderThread(DataOutputStream out, ProtocolMode protocol, ClientToServerMode mode, String username, PublicKey svk, PrivateKey bsk) {
		this.out = out;
		this.protocol = protocol;
		this.mode = mode;
		this.username = username.getBytes();
		this.svk = svk.getEncoded();
		this.bsk = bsk.getEncoded();
	}

	/**
	 * Constructor used when the client asks for a challenge using the Storage Optimal protocol
	 * @param out The DataOutputStream given by the server 
	 * @param protocol The protocol used 
	 * @param mode The actual client state from the server point of view
	 * @param username The username of the client
	 * @param password The hashed password of the client
	 */
	public ClientSenderThread(DataOutputStream out, ProtocolMode protocol, ClientToServerMode mode, String username, BigInteger password) {
		this.out = out;
		this.protocol = protocol;
		this.mode = mode;
		this.username = username.getBytes();
		this.password = password.toByteArray();
	}

	public ClientSenderThread(DataOutputStream out, ProtocolMode protocol, ClientToServerMode register, String username, byte[] k) {
		this.out = out;
		this.protocol = protocol;
		this.mode = ClientToServerMode.REGISTER;
		this.username = username.getBytes();
		this.k = k;
	}

	@Override
	public void run() {	
		byte[] protocolAsByte = protocol.toString().getBytes();
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
					out.writeInt(protocolAsByte.length);
					out.write(protocolAsByte);
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
			
		case STORAGE_OPTIMAL: case PRIVACY_OPTIMAL:
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
					out.writeInt(protocolAsByte.length);
					out.write(protocolAsByte);
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
			
		case MOBILE:
			switch(mode) {
			case REGISTER:
				try {
					out.writeInt(protocolAsByte.length);
					out.write(protocolAsByte);
					out.writeInt(modeAsByte.length);
					out.write(modeAsByte);
					out.writeInt(username.length);
					out.write(username);
					out.writeInt(k.length);
					out.write(k);
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
				break;
			default:
				break;
			}
			break;
		}
		
		Thread.currentThread().interrupt();
		return;
	}
}
