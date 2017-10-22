package storage.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.PrivateKey;

import SSLUtility.ProtocolMode;
import storage.ClientToStorageMode;

public class ClientSenderThread extends Thread implements Runnable {
	private DataOutputStream out;
	private ClientToStorageMode mode;
	private ProtocolMode protocol;
	byte[] id, bsk, ctext, sig, password;


	public ClientSenderThread(DataOutputStream out, ProtocolMode protocol, ClientToStorageMode mode, BigInteger id, PrivateKey bsk, byte[] ctext) {
		this.out = out;
		this.protocol = protocol;
		this.mode = mode;
		this.id = id.toByteArray();
		this.ctext = ctext;
		this.bsk = bsk.getEncoded();
	}

	public ClientSenderThread(DataOutputStream out, ProtocolMode protocol, ClientToStorageMode mode, BigInteger id, BigInteger password) {
		this.out = out;
		this.protocol = protocol;
		this.mode = mode;
		this.id = id.toByteArray();
		this.password = password.toByteArray();
	}

	public ClientSenderThread(DataOutputStream out, ProtocolMode protocol, ClientToStorageMode mode, BigInteger id, byte[] ctext) {
		this.out = out;
		this.protocol = protocol;
		this.mode = mode;
		this.id = id.toByteArray();
		this.ctext = ctext;
	}

	public ClientSenderThread(DataOutputStream out, ProtocolMode protocol, ClientToStorageMode mode, BigInteger id) {
		this.out = out;
		this.protocol = protocol;
		this.mode = mode;
		this.id = id.toByteArray();
	}

	@Override
	public void run() {	
		byte[] protocolAsByte = protocol.toString().getBytes();
		byte[] modeAsByte = mode.toString().getBytes();
		switch(protocol) {
		case SERVER_OPTIMAL:
			switch(mode) {
			case STORE:
				try {
					out.writeInt(protocolAsByte.length);
					out.write(protocolAsByte);
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
			break;

		case STORAGE_OPTIMAL:
			switch(mode) {
			case STORE:
				try {
					out.writeInt(protocolAsByte.length);
					out.write(protocolAsByte);
					out.writeInt(modeAsByte.length);
					out.write(modeAsByte);
					out.writeInt(id.length);
					out.write(id);
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
