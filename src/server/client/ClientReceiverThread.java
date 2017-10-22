package server.client;

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
		BigInteger challenge;
		switch(protocol) {
		case SERVER_OPTIMAL:
			challenge = new BigInteger(getData());
			result[0] = challenge;
			break;
			
		case STORAGE_OPTIMAL:
			BigInteger id = new BigInteger(getData());
			challenge = new BigInteger(getData());
			result[0] = id;
			result[1] = challenge;
			break;
		
		case PRIVACY_OPTIMAL:
			
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
