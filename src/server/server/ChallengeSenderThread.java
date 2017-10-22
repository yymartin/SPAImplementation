package server.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import SSLUtility.ProtocolMode;

public class ChallengeSenderThread implements Runnable{
	
	private DataOutputStream out;
	private ProtocolMode protocol;
	private byte[] id, challenge;
	
	public ChallengeSenderThread(DataOutputStream out, ProtocolMode protocol, BigInteger challenge) {
		this.out = out;
		this.protocol = protocol;
		this.challenge = challenge.toByteArray();
	}
	
	public ChallengeSenderThread(DataOutputStream out, ProtocolMode protocol, BigInteger id, BigInteger challenge) {
		this.out = out;
		this.protocol = protocol;
		this.id = id.toByteArray();
		this.challenge = challenge.toByteArray();
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
			
		case STORAGE_OPTIMAL: 
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
			
		case PRIVACY_OPTIMAL:
			
			break;
		}		
	}
	
}
