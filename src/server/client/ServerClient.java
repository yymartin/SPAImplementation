package server.client;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import SSLUtility.ProtocolMode;
import SSLUtility.SSLClientUtility;
import cryptographyBasics.Hash;
import cryptographyBasics.MyKeyGenerator;
import server.ClientToServerMode;

public class ServerClient {

	public static Socket socket = null;
	public static Thread t1, t2;
	private static DataInputStream in;
	private static DataOutputStream out;
	
	private static ProtocolMode protocol;
	private static String username;
	private BigInteger password;
	private PublicKey svk;
	private PrivateKey bsk;

	public ServerClient(ProtocolMode protocol, String username, String password, PrivateKey bsk, PublicKey svk) {
		ServerClient.protocol = protocol;
		ServerClient.username = username;
		this.password = Hash.generateSHA256Hash(password.getBytes());
		this.svk = svk;
		this.bsk = bsk;
	}

	public void registerToServer() {
		switch(protocol) {
		case SERVER_OPTIMAL:
			try {
				InputStream key = new FileInputStream(new File("./PUBLICKEY.jks"));
				System.out.println("Ask for connection");
				socket = SSLClientUtility.getSocketWithCert(InetAddress.getLocalHost(), 2009, key, "8rXbM7twa)E96xtFZmWq6/J^");
				System.out.println("Connection established"); 
				out = new DataOutputStream(socket.getOutputStream());

				ExecutorService ex = Executors.newFixedThreadPool(2);
				ex.execute(new ClientSenderThread(out, ProtocolMode.SERVER_OPTIMAL, ClientToServerMode.REGISTER, username, svk));
			} catch (UnknownHostException e) {

			} catch (IOException e) {

			}	
			break;
			
		case STORAGE_OPTIMAL:
			try {
				InputStream key = new FileInputStream(new File("./PUBLICKEY.jks"));
				System.out.println("Ask for connection");
				socket = SSLClientUtility.getSocketWithCert(InetAddress.getLocalHost(), 2009, key, "8rXbM7twa)E96xtFZmWq6/J^");
				System.out.println("Connection established"); 
				out = new DataOutputStream(socket.getOutputStream());

				ExecutorService ex = Executors.newFixedThreadPool(2);
				ex.execute(new ClientSenderThread(out, ProtocolMode.STORAGE_OPTIMAL, ClientToServerMode.REGISTER, username, svk, bsk));
			} catch (UnknownHostException e) {

			} catch (IOException e) {

			}
			break;
			
		case PRIVACY_OPTIMAL:
			
			break;
		}
	}
	
	public BigInteger[] askForChallengeToServer() {
		BigInteger[] finalChallenge = null;
		
		switch(protocol) {
		case SERVER_OPTIMAL:
			try {
				InputStream key = new FileInputStream(new File("./PUBLICKEY.jks"));
				System.out.println("Ask for connection");
				socket = SSLClientUtility.getSocketWithCert(InetAddress.getLocalHost(), 2009, key, "8rXbM7twa)E96xtFZmWq6/J^");
				System.out.println("Connection established"); 
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());

				ExecutorService ex = Executors.newFixedThreadPool(3);
				ex.execute(new ClientSenderThread(out, ProtocolMode.SERVER_OPTIMAL, ClientToServerMode.CHALLENGE, username));
				
				Future<BigInteger[]> result = ex.submit(new ClientReceiverThread(in, ProtocolMode.SERVER_OPTIMAL));
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
			break;
			
		case STORAGE_OPTIMAL:
			try {
				InputStream key = new FileInputStream(new File("./PUBLICKEY.jks"));
				System.out.println("Ask for connection");
				socket = SSLClientUtility.getSocketWithCert(InetAddress.getLocalHost(), 2009, key, "8rXbM7twa)E96xtFZmWq6/J^");
				System.out.println("Connection established"); 
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());

				ExecutorService ex = Executors.newFixedThreadPool(3);
				ex.execute(new ClientSenderThread(out, ProtocolMode.STORAGE_OPTIMAL, ClientToServerMode.CHALLENGE, username, password));
				
				Future<BigInteger[]> result = ex.submit(new ClientReceiverThread(in, ProtocolMode.STORAGE_OPTIMAL));
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
			break;
			
		case PRIVACY_OPTIMAL:
			
			break;
		}
		return finalChallenge;
	}

	public void executeChallengeToServer(BigInteger response) {
		try {
			InputStream key = new FileInputStream(new File("./PUBLICKEY.jks"));
			System.out.println("Ask for connection");
			socket = SSLClientUtility.getSocketWithCert(InetAddress.getLocalHost(), 2009, key, "8rXbM7twa)E96xtFZmWq6/J^");
			System.out.println("Connection established"); 
			out = new DataOutputStream(socket.getOutputStream());
						
			ExecutorService ex = Executors.newSingleThreadExecutor();
			ex.execute(new ClientSenderThread(out, ClientToServerMode.AUTH, username, response));
		} catch (UnknownHostException e) {

		} catch (IOException e) {

		} 
	}
}
