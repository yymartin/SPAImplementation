package storage.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.security.PublicKey;
import java.util.concurrent.Callable;

import cryptographyBasics.MyKeyGenerator;

/**
 * @author yoanmartin
 *  Instantiation of a thread which receives a PublicKey from a storage
 */
public class ClientReceivePublicThread implements Callable<PublicKey>{
	private DataInputStream in;
	
	/**
	 * The constructor of the thread
	 * @param in The DataInputStream received by the client
	 */
	public ClientReceivePublicThread(DataInputStream in) {
		this.in = in;
	}

	@Override
	public PublicKey call() throws Exception {
		byte[] keyAsBytes = getData();
		return MyKeyGenerator.convertByteArrayIntoPublicKey(keyAsBytes);
	}
	
	private byte[] getData() {
		byte[] result = null;
		int length;
		try {
			length = in.readInt();
			if(length > 0) {
				result = new byte[length];
				in.readFully(result, 0, result.length); 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}  
		return result;
	}
}
