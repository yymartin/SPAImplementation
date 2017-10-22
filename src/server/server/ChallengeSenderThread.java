package server.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;

public class ChallengeSenderThread implements Runnable{
	
	private DataOutputStream out;
	private byte[] challenge;
	
	public ChallengeSenderThread(DataOutputStream out, BigInteger challenge) {
		this.out = out;
		this.challenge = challenge.toByteArray();
	}

	@Override
	public void run() {
		try {
			out.writeInt(challenge.length);
			out.write(challenge);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
