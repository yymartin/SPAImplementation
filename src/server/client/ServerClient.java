package server.client;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import SSLUtility.ProtocolMode;
import SSLUtility.SSLClientUtility;
import cryptographyBasics.AsymmetricEncryption;
import cryptographyBasics.Hash;
import server.ClientToServerMode;
import user.UserApplication;

/**
 * @author yoanmartin
 * Object which instantiate the client side of the connection with a server
 */
public class ServerClient {
	private final int port = 2010;
	public static Socket socket = null;
	public static Thread t1, t2;
	private static DataInputStream in;
	private static DataOutputStream out;
	
	private static ProtocolMode protocol;
	private static String username;
	private BigInteger password;
	private PublicKey svk, bvk;
	private PrivateKey bsk;
	private BigInteger r;
	
	private byte[] k;
	
	private ExecutorService serverPool = Executors.newFixedThreadPool(20);
	
	
	/**
	 * Constructor used when the Storage Optimal protocol is used
	 * @param username The username of the user
	 * @param password The password of the user
	 * @param bsk The bsk of the user
	 * @param svk The svk of the user
	 * @param r The blind factor used in blind signature
	 */
	public ServerClient(String username, String password, PrivateKey bsk, PublicKey bvk, PublicKey svk, BigInteger r) {
		ServerClient.protocol = SSLUtility.ProtocolMode.STORAGE_OPTIMAL;
		ServerClient.username = username;
		this.password = Hash.generateSHA256Hash(password.getBytes());
		this.svk = svk;
		this.bsk = bsk;
		this.bvk = bvk;
		this.r = r;
	}
	
	public ServerClient(String username, PublicKey svk) {
		ServerClient.protocol = SSLUtility.ProtocolMode.SERVER_OPTIMAL;
		ServerClient.username = username;
		this.svk = svk;
	}
	
	public ServerClient(String username, byte[] k) {
		ServerClient.protocol = SSLUtility.ProtocolMode.MOBILE;
		ServerClient.username = username;
		this.k = k;
	}

	/**
	 * Function which registers to the server
	 */
	public void registerToServer() {
		switch(protocol) {
		case SERVER_OPTIMAL:
			try {
				InputStream key = new FileInputStream(new File("./PUBLICKEY.jks"));
				System.out.println("Ask for connection with the server");
				UserApplication.output = UserApplication.output + "\n Ask for connection with the server";
				socket = SSLClientUtility.getSocketWithCert(InetAddress.getLocalHost(), port, key, "8rXbM7twa)E96xtFZmWq6/J^");
				System.out.println("Server connection established"); 
				UserApplication.output = UserApplication.output + "\n Server connection established";
				out = new DataOutputStream(socket.getOutputStream());
				serverPool.execute(new ClientSenderThread(out, ProtocolMode.SERVER_OPTIMAL, ClientToServerMode.REGISTER, username, svk));
				serverPool.shutdown();
			} catch (UnknownHostException e) {

			} catch (IOException e) {

			}	
			break;
			
		case STORAGE_OPTIMAL: case PRIVACY_OPTIMAL :
			try {
				InputStream key = new FileInputStream(new File("./PUBLICKEY.jks"));
				System.out.println("Ask for connection");
				socket = SSLClientUtility.getSocketWithCert(InetAddress.getLocalHost(), port, key, "8rXbM7twa)E96xtFZmWq6/J^");
				System.out.println("Connection established"); 
				out = new DataOutputStream(socket.getOutputStream());
				serverPool.execute(new ClientSenderThread(out, ProtocolMode.STORAGE_OPTIMAL, ClientToServerMode.REGISTER, username, svk, bsk));
			} catch (UnknownHostException e) {

			} catch (IOException e) {

			}
			break;
			
		case MOBILE:
			try {
				InputStream key = new FileInputStream(new File("./PUBLICKEY.jks"));
				System.out.println("Ask for connection");
				socket = SSLClientUtility.getSocketWithCert(InetAddress.getLocalHost(), port, key, "8rXbM7twa)E96xtFZmWq6/J^");
				System.out.println("Connection established"); 
				out = new DataOutputStream(socket.getOutputStream());
				serverPool.execute(new ClientSenderThread(out, ProtocolMode.MOBILE, ClientToServerMode.REGISTER, username, k));
			} catch (UnknownHostException e) {

			} catch (IOException e) {

			}
			break;
		}
	}
	
	/**
	 * Function which asks the server a challenge
	 * @return The challenge in the case of Server Optimal protocol and the id and the challenge in the case of Storage Optimal protocol
	 */
	public BigInteger[] askForChallengeToServer() {
		BigInteger[] finalChallenge = null;
		
		switch(protocol) {
		case SERVER_OPTIMAL:
			try {
				InputStream key = new FileInputStream(new File("./PUBLICKEY.jks"));
				System.out.println("Ask for connection");
				socket = SSLClientUtility.getSocketWithCert(InetAddress.getLocalHost(), port, key, "8rXbM7twa)E96xtFZmWq6/J^");
				System.out.println("Connection established"); 
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
				serverPool.execute(new ClientSenderThread(out, ProtocolMode.SERVER_OPTIMAL, ClientToServerMode.CHALLENGE, username));
				Future<BigInteger[]> result = serverPool.submit(new ClientReceiverThread(in, ProtocolMode.SERVER_OPTIMAL));
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
			
		case STORAGE_OPTIMAL: case PRIVACY_OPTIMAL :
			try {
				InputStream key = new FileInputStream(new File("./PUBLICKEY.jks"));
				System.out.println("Ask for connection");
				socket = SSLClientUtility.getSocketWithCert(InetAddress.getLocalHost(), port, key, "8rXbM7twa)E96xtFZmWq6/J^");
				System.out.println("Connection established"); 
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
				
				BigInteger passwordBlinded = AsymmetricEncryption.blind(password, r, (RSAPublicKey) bvk);
				serverPool.execute(new ClientSenderThread(out, ProtocolMode.STORAGE_OPTIMAL, ClientToServerMode.CHALLENGE, username, passwordBlinded));
				Future<BigInteger[]> result = serverPool.submit(new ClientReceiverThread(in, ProtocolMode.STORAGE_OPTIMAL));
				finalChallenge = result.get();
				finalChallenge[0] = AsymmetricEncryption.unblind(finalChallenge[0], ((RSAPublicKey) bvk).getModulus(), r);
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
		case MOBILE:
			try {
				InputStream key = new FileInputStream(new File("./PUBLICKEY.jks"));
				System.out.println("Ask for connection");
				socket = SSLClientUtility.getSocketWithCert(InetAddress.getLocalHost(), port, key, "8rXbM7twa)E96xtFZmWq6/J^");
				System.out.println("Connection established"); 
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
				serverPool.execute(new ClientSenderThread(out, ProtocolMode.MOBILE, ClientToServerMode.CHALLENGE, username));
			} catch (UnknownHostException e) {

			} catch (IOException e) {

			} 
			break;
		}
		return finalChallenge;
	}

	/**
	 * Function which sends the calculated response to the challenge received
	 * @param response The calculated response
	 */
	public void executeChallengeToServer(BigInteger response) {
		try {
			InputStream key = new FileInputStream(new File("./PUBLICKEY.jks"));
			System.out.println("Ask for connection");
			socket = SSLClientUtility.getSocketWithCert(InetAddress.getLocalHost(), port, key, "8rXbM7twa)E96xtFZmWq6/J^");
			System.out.println("Connection established"); 
			out = new DataOutputStream(socket.getOutputStream());
			serverPool.execute(new ClientSenderThread(out, ClientToServerMode.AUTH, username, response));
		} catch (UnknownHostException e) {

		} catch (IOException e) {

		} 
	}
}
