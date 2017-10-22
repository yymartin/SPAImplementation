package server.client;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import SSLUtility.SSLClientUtility;
import cryptographyBasics.AsymmetricEncryption;
import server.ClientToServerMode;

public class Client {

	public static Socket socket = null;
	public static Thread t1, t2;
	private static DataInputStream in;
	private static DataOutputStream out;

	public static void registerToServer(String username, PublicKey svk) {
		try {
			InputStream key = new FileInputStream(new File("./PUBLICKEY.jks"));
			System.out.println("Ask for connection");
			socket = SSLClientUtility.getSocketWithCert(InetAddress.getLocalHost(), 2009, key, "8rXbM7twa)E96xtFZmWq6/J^");
			System.out.println("Connection established"); 
			out = new DataOutputStream(socket.getOutputStream());

			ExecutorService ex = Executors.newFixedThreadPool(2);
			ex.execute(new ClientSenderThread(out, ClientToServerMode.REGISTER, username, svk));
		} catch (UnknownHostException e) {

		} catch (IOException e) {

		}	
	}
	
	public static BigInteger askForChallengeToServer(String username) {
		BigInteger finalChallenge = null;
		try {
			InputStream key = new FileInputStream(new File("./PUBLICKEY.jks"));
			System.out.println("Ask for connection");
			socket = SSLClientUtility.getSocketWithCert(InetAddress.getLocalHost(), 2009, key, "8rXbM7twa)E96xtFZmWq6/J^");
			System.out.println("Connection established"); 
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());

			ExecutorService ex = Executors.newFixedThreadPool(3);
			ex.execute(new ClientSenderThread(out, ClientToServerMode.CHALLENGE, username));
			
			Future<BigInteger> result = ex.submit(new ClientReceiverThread(in));
			finalChallenge = result.get();
		} catch (UnknownHostException e) {

		} catch (IOException e) {

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return finalChallenge;
	}

	public static void executeChallengeToServer(String username, BigInteger challenge) {
		try {
			InputStream key = new FileInputStream(new File("./PUBLICKEY.jks"));
			System.out.println("Ask for connection");
			socket = SSLClientUtility.getSocketWithCert(InetAddress.getLocalHost(), 2009, key, "8rXbM7twa)E96xtFZmWq6/J^");
			System.out.println("Connection established"); 
			out = new DataOutputStream(socket.getOutputStream());
						
			ExecutorService ex = Executors.newSingleThreadExecutor();
			ex.execute(new ClientSenderThread(out, ClientToServerMode.AUTH, username, challenge));
		} catch (UnknownHostException e) {

		} catch (IOException e) {

		} 
	}
}
