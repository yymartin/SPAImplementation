package storage.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.Callable;

import SSLUtility.ProtocolMode;

public class ClientReceiverThread implements Callable<BigInteger[]> {
	private DataInputStream in;
	private ProtocolMode protocol;

	public ClientReceiverThread(DataInputStream in, ProtocolMode protocol) {
		this.in = in;
		this.protocol = protocol;
	}


	@Override
	public BigInteger[] call() throws Exception{
	BigInteger[] result = new BigInteger[2];
	BigInteger ctext;
		switch(protocol) {
		case SERVER_OPTIMAL :
			BigInteger sig = new BigInteger(getData());
			ctext = new BigInteger(getData());
			result[0] = sig;
			result[1] = ctext;
			break;
			
		case STORAGE_OPTIMAL :
			ctext = new BigInteger(getData());
			result[0] = ctext;
			break;
			
		case PRIVACY_OPTIMAL :
			
			break;
		}
		return result;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return result;
	}
}
