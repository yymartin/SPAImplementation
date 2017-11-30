package server.client;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
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
		Future<String> response;
		switch(protocol) {
		case SERVER_OPTIMAL:			
			response = serverPool.submit(new ClientSenderThread(ProtocolMode.SERVER_OPTIMAL, ClientToServerMode.REGISTER, username, svk));
			try {
				if(response.get().equals("OK")) {
					System.out.println("Correctly registered to the server!");
				}
			} catch (InterruptedException | ExecutionException e) {
				System.out.println("Something went wrong, you are not registered to the server");
				e.printStackTrace();
			} 
			break;

		case STORAGE_OPTIMAL: case PRIVACY_OPTIMAL :
			response = serverPool.submit(new ClientSenderThread(ProtocolMode.STORAGE_OPTIMAL, ClientToServerMode.REGISTER, username, svk, bsk));
			try {
				if(response.get().equals("OK")) {
					System.out.println("Correctly registered to the server!");
				}
			} catch (InterruptedException | ExecutionException e) {
				System.out.println("Something went wrong, you are not registered to the server");
				e.printStackTrace();
			}
			break;

		case MOBILE:
			response = serverPool.submit(new ClientSenderThread(ProtocolMode.MOBILE, ClientToServerMode.REGISTER, username, k));
			try {
				if(response.get().equals("OK")) {
					System.out.println("Correctly registered to the server!");
				}
			} catch (InterruptedException | ExecutionException e) {
				System.out.println("Something went wrong, you are not registered to the server");
				e.printStackTrace();
			}
			break;
		}
	}

	/**
	 * Function which asks the server a challenge
	 * @return The challenge in the case of Server Optimal protocol and the id and the challenge in the case of Storage Optimal protocol
	 */
	public BigInteger[] askForChallengeToServer() {
		BigInteger[] finalChallenge = new BigInteger[2];
		Future<String> response;
		switch(protocol) {
		case SERVER_OPTIMAL:
			response = serverPool.submit(new ClientSenderThread(ProtocolMode.SERVER_OPTIMAL, ClientToServerMode.CHALLENGE, username));
			try {
				finalChallenge[0] = new BigInteger(response.get());
			} catch (InterruptedException | ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;

		case STORAGE_OPTIMAL: case PRIVACY_OPTIMAL :
			BigInteger passwordBlinded = AsymmetricEncryption.blind(password, r, (RSAPublicKey) bvk);
			response = serverPool.submit(new ClientSenderThread(ProtocolMode.STORAGE_OPTIMAL, ClientToServerMode.CHALLENGE, username, passwordBlinded));
			try {
				String[] idAndChallenge = response.get().split(",");
				finalChallenge[0] = AsymmetricEncryption.unblind(new BigInteger(idAndChallenge[0]), ((RSAPublicKey) bvk).getModulus(), r);
				finalChallenge[1] = new BigInteger(idAndChallenge[1]);
			} catch (InterruptedException | ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
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
				//				serverPool.execute(new ClientSenderThread(out, ProtocolMode.MOBILE, ClientToServerMode.CHALLENGE, username));
				//				Future<BigInteger[]> result = serverPool.submit(new ClientReceiverThread(in, ProtocolMode.MOBILE));
				//				finalChallenge = result.get();
			} catch (UnknownHostException e) {

			} catch (IOException e) {

				//			} catch (InterruptedException e) {
				//				// TODO Auto-generated catch block
				//				e.printStackTrace();
				//			} catch (ExecutionException e) {
				//				// TODO Auto-generated catch block
				//				e.printStackTrace();
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
		Future<String> serverResponse = serverPool.submit(new ClientSenderThread(ClientToServerMode.AUTH, username, response));
		try {
			if(serverResponse.get().equals("Authenticated")){
				System.out.println("Connected!");
			} else {
				System.out.println("Not connected");
			}
		} catch (InterruptedException | ExecutionException e) {
			System.out.println("Not connected");
			e.printStackTrace();
		}
	}
}
