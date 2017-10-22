package storage.client;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.PrivateKey;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import SSLUtility.SSLClientUtility;
import storage.ClientToStorageMode;

public class Client {

	public static Socket socket = null;
	public static Thread t1, t2;
	private static DataInputStream in;
	private static DataOutputStream out;

	public static void storeValuesToStorage(BigInteger id, PrivateKey bsk, BigInteger ctext) {
		try {
			InputStream key = new FileInputStream(new File("./PUBLICKEY.jks"));
			System.out.println("Ask for connection");
			socket = SSLClientUtility.getSocketWithCert(InetAddress.getLocalHost(), 2009, key, "8rXbM7twa)E96xtFZmWq6/J^");
			System.out.println("Connection established"); 
			out = new DataOutputStream(socket.getOutputStream());

			ExecutorService ex = Executors.newFixedThreadPool(2);
			ex.execute(new ClientSenderThread(out, ClientToStorageMode.STORE, id, bsk, ctext));
		} catch (UnknownHostException e) {

		} catch (IOException e) {

		}	
	}

	public static BigInteger[] retrieveValuesFromStorage(BigInteger id, String password) {
		BigInteger[] finalResult = new BigInteger[2];
		try {
			InputStream key = new FileInputStream(new File("./PUBLICKEY.jks"));
			System.out.println("Ask for connection");
			socket = SSLClientUtility.getSocketWithCert(InetAddress.getLocalHost(), 2009, key, "8rXbM7twa)E96xtFZmWq6/J^");
			System.out.println("Connection established"); 
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());

			ExecutorService ex = Executors.newFixedThreadPool(2);
			ex.execute(new ClientSenderThread(out, ClientToStorageMode.RETRIEVE, id, password));
			
			Future<BigInteger[]> result = ex.submit(new ClientReceiverThread(in));
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
