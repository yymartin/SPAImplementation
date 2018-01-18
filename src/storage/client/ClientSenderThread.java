package storage.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.util.concurrent.Callable;
import SSLUtility.ProtocolMode;
import storage.ClientToStorageMode;

/**
 * @author yoanmartin
 * Instantiation of a thread which sends information to a storage
 */
public class ClientSenderThread implements Callable<Boolean> {
	private DataInputStream in;
	private DataOutputStream out;
	private ClientToStorageMode mode;
	private ProtocolMode protocol;
	byte[] id, bsk, ctext, sig, password;


	/**
	 * Constructor used when the client registers to the storage using the Server Optimal protocol
	 * @param in The DataInputStream rceived by the client
	 * @param out The DataOutputStream received by the client
	 * @param id The id to be sent
	 * @param bsk The bsk to be sent
	 * @param ctext The ctext to be sent
	 */
	public ClientSenderThread(DataInputStream in, DataOutputStream out, BigInteger id, PrivateKey bsk, byte[] ctext) {
		this.in = in;
		this.out = out;
		this.protocol = SSLUtility.ProtocolMode.SERVER_OPTIMAL;
		this.mode = ClientToStorageMode.STORE;
		this.id = id.toByteArray();
		this.ctext = ctext;
		this.bsk = bsk.getEncoded();
	}

	/**
	 * Constructor used when the client retrieves information from the storage in the Server Optimal protocol
	 * @param out The DataOutputStream received by the client
	 * @param id The id to be sent
	 * @param password The hashed password to be sent
	 */
	public ClientSenderThread(DataOutputStream out, BigInteger id, BigInteger password) {
		this.out = out;
		this.protocol = SSLUtility.ProtocolMode.SERVER_OPTIMAL;
		this.mode = ClientToStorageMode.RETRIEVE;
		this.id = id.toByteArray();
		this.password = password.toByteArray();
	}

	/**
	 * Constructor used when the client registers to the storage using the Storage Optimal protocol
	 * @param in The DataInputStream received by the client
	 * @param out The DataOutputStream received by the client
	 * @param protocol The protocol used by the client
	 * @param id The id to be sent
	 * @param ctext The ctext to be sent
	 */
	public ClientSenderThread(DataInputStream in, DataOutputStream out, ProtocolMode protocol, BigInteger id, byte[] ctext) {
		this.in = in;
		this.out = out;
		this.protocol = protocol;
		this.mode = ClientToStorageMode.STORE;
		this.id = id.toByteArray();
		this.ctext = ctext;
	}

	/**
	 * Constructor used when the client retrieves information from the storage in the Storage Optimal protocol and Privacy Optimal protocol
	 * @param out The DataOutputStream received by the client
	 * @param protocol The protocol used by the client
	 * @param id The id to be sent
	 */
	public ClientSenderThread(DataOutputStream out, ProtocolMode protocol, BigInteger id) {
		this.out = out;
		this.protocol = protocol;
		this.mode = ClientToStorageMode.RETRIEVE;
		this.id = id.toByteArray();
	}

	public Boolean call() {	
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
					e.printStackTrace();
				}	
				
				try {
					return in.readBoolean();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return false;
			case RETRIEVE:
				try {
					out.writeInt(protocolAsByte.length);
					out.write(protocolAsByte);
					out.writeInt(modeAsByte.length);
					out.write(modeAsByte);
					out.writeInt(id.length);
					out.write(id);
					out.writeInt(password.length);
					out.write(password);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return false;
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
					e.printStackTrace();
				}
				
				try {
					return in.readBoolean();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return false ;
			case RETRIEVE:
				try {
					out.writeInt(protocolAsByte.length);
					out.write(protocolAsByte);
					out.writeInt(modeAsByte.length);
					out.write(modeAsByte);
					out.writeInt(id.length);
					out.write(id);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return false;
			}
			break;

		case PRIVACY_OPTIMAL:
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
					e.printStackTrace();
				}
				return false;
			case RETRIEVE:
				try {
					out.writeInt(protocolAsByte.length);
					out.write(protocolAsByte);
					out.writeInt(modeAsByte.length);
					out.write(modeAsByte);
					out.writeInt(id.length);
					out.write(id);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return false;
			}
			break;
		case MOBILE:
			break;
		default:
			break;
		}
		return false;
	}
}
