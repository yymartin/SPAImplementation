package storage.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;

public class ClientSenderThread extends Thread implements Runnable {
	private DataOutputStream out;
	byte[] id;

	public ClientSenderThread(DataOutputStream out, BigInteger id) {
		this.out = out;
		this.id = id.toByteArray();
	}

	@Override
	public void run() {		
		try {
			out.writeInt(id.length);
			out.write(id);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
