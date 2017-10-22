package storage.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.PrivateKey;

import storage.ClientToStorageMode;

public class ClientSenderThread extends Thread implements Runnable {
	private DataOutputStream out;
	ClientToStorageMode mode;
	byte[] id, bsk, ctext, sig, password;


	public ClientSenderThread(DataOutputStream out, ClientToStorageMode mode, BigInteger id, PrivateKey bsk, BigInteger ctext) {
		this.out = out;
		this.mode = mode;
		this.id = id.toByteArray();
		this.ctext = ctext.toByteArray();
		this.bsk = bsk.getEncoded();
	}

	public ClientSenderThread(DataOutputStream out, ClientToStorageMode mode, BigInteger id, String password) {
		this.out = out;
		this.mode = mode;
		this.id = id.toByteArray();
		this.password = password.getBytes();
	}

	@Override
	public void run() {	
		byte[] modeAsByte = mode.toString().getBytes();
		switch(mode) {
		case STORE:
			try {
				out.writeInt(modeAsByte.length);
				out.write(modeAsByte);
				out.writeInt(id.length);
				out.write(id);
				out.writeInt(bsk.length);
				out.write(bsk);
				out.writeInt(ctext.length);
				out.write(ctext);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case RETRIEVE:
			try {
				out.writeInt(modeAsByte.length);
				out.write(modeAsByte);
				out.writeInt(id.length);
				out.write(id);
				out.writeInt(password.length);
				out.write(password);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
	}
}
