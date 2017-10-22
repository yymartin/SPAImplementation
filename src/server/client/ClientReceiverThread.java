package server.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.Callable;

public class ClientReceiverThread implements Callable<BigInteger> {
	private DataInputStream in;

	public ClientReceiverThread(DataInputStream in) {
		this.in = in;
		System.out.println("ClientReceiverThread created");
	}


	@Override
	public BigInteger call() throws Exception{
		byte[] result = getData();
		System.out.println(result);
		return new BigInteger(result);
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
