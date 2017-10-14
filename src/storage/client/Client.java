package storage.client;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Client {

	public static Socket socket = null;
	public static Thread t1, t2;
	private static DataInputStream in;
	private static DataOutputStream out;
	
	public static BigInteger getValuesFromId(BigInteger id) {
		BigInteger finalResult = null;
		try {
			InputStream key = new FileInputStream(new File("./PUBLICKEY.jks"));
			System.out.println("Ask for connection");
			socket = SSLClientUtility.getSocketWithCert(InetAddress.getLocalHost(), 2009, key, "8rXbM7twa)E96xtFZmWq6/J^");
			System.out.println("Connection established"); 
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());	

			
			ExecutorService ex = Executors.newFixedThreadPool(2);
			ex.execute(new ClientSenderThread(out, BigInteger.valueOf(123456)));
			
			Future<BigInteger> result = ex.submit(new ClientReceiverThread(in));
			finalResult = result.get();
			ex.shutdown();
		} catch (UnknownHostException e) {

		} catch (IOException e) {

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return finalResult;
	}

}
