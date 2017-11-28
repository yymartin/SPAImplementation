package server.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import SSLUtility.ProtocolMode;

/**
 * @author yoanmartin
 * Instantiation of a thread which sends a challenge to a client
 */
public class ChallengeSenderThread implements Runnable{
	
	private DataOutputStream out;
	private ProtocolMode protocol;
	private byte[] id, challenge;
	
	/**
	 * Constructor used when the Server Optimal protocol is used
	 * @param out The DataOutputStream received by the server
	 * @param challenge The challenge to be sent
	 */
	public ChallengeSenderThread(DataOutputStream out, ProtocolMode protocol, BigInteger challenge) {
		this.out = out;
		this.protocol = SSLUtility.ProtocolMode.SERVER_OPTIMAL;
		this.challenge = challenge.toByteArray();
	}
	
	/**
	 * Constructor used when the Storage Optimal protocol is used
	 * @param out The DataOutputStream received by the server
	 * @param id The id to be sent
	 * @param challenge The challenge to be sent
	 */
	public ChallengeSenderThread(DataOutputStream out, BigInteger id, BigInteger challenge) {
		this.out = out;
		this.protocol = SSLUtility.ProtocolMode.STORAGE_OPTIMAL;
		this.id = id.toByteArray();
		this.challenge = challenge.toByteArray();
	}

	public ChallengeSenderThread(DataOutputStream out, String challenge) {
		this.out = out;
		this.protocol = SSLUtility.ProtocolMode.MOBILE;
		this.challenge = challenge.getBytes();
	}

	@Override
	public void run() {
		switch(protocol) {
		case SERVER_OPTIMAL:
			try {
				out.writeInt(challenge.length);
				out.write(challenge);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
			
		case STORAGE_OPTIMAL: case PRIVACY_OPTIMAL:
			try {
				out.writeInt(id.length);
				out.write(id);
				out.writeInt(challenge.length);
				out.write(challenge);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
			
		case MOBILE:
			try {
				out.writeInt(challenge.length);
				out.write(challenge);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}	
		
		Thread.currentThread().interrupt();
		return;
	}
	
}
