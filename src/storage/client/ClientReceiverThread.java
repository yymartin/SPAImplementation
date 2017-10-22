package storage.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.Callable;

public class ClientReceiverThread implements Callable<BigInteger[]> {
	private DataInputStream in;

	public ClientReceiverThread(DataInputStream in) {
		this.in = in;
	}


	@Override
	public BigInteger[] call() throws Exception{
		BigInteger[] result = new BigInteger[2];
		BigInteger sig = new BigInteger(getData());
		BigInteger ctext = new BigInteger(getData());
		result[0] = sig;
		result[1] = ctext;
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
