package server.client;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.crypto.SecretKey;

import SSLUtility.ProtocolMode;
import cryptographyBasics.AsymmetricEncryption;
import cryptographyBasics.Hash;
import server.ClientToServerMode;

/**
 * @author yoanmartin
 * Object which instantiate the client side of the connection with a server
 */
public class ServerClient {
	private ProtocolMode protocol;
	private String username;
	private BigInteger password;
	private PublicKey svk, bvk;
	private PrivateKey bsk;
	private BigInteger r;

	private SecretKey k;

	private ExecutorService serverPool = Executors.newSingleThreadExecutor();


	/**
	 * Constructor used when the Storage Optimal protocol is used
	 * @param username The username of the user
	 * @param password The password of the user
	 * @param bsk The bsk of the user
	 * @param bvk The bvk of the user
	 * @param svk The svk of the user
	 * @param r The blind factor used in blind signature
	 */
	public ServerClient(String username, String password, PrivateKey bsk, PublicKey bvk, PublicKey svk, BigInteger r) {
		this.protocol = SSLUtility.ProtocolMode.STORAGE_OPTIMAL;
		this.username = username;
		this.password = Hash.generateSHA256Hash(password.getBytes());
		this.svk = svk;
		this.bsk = bsk;
		this.bvk = bvk;
		this.r = r;
	}

	public ServerClient(String username, PublicKey svk) {
		this.protocol = SSLUtility.ProtocolMode.SERVER_OPTIMAL;
		this.username = username;
		this.svk = svk;
	}

	public ServerClient(String username, SecretKey k) {
		this.protocol = SSLUtility.ProtocolMode.MOBILE;
		this.username = username;
		this.k = k;
	}

	/**
	 * Function which registers to the server
	 * @param website The address of the server
	 * @return Return true if the operation succeeded
	 */
	public boolean registerToServer(String website) {
		Future<String> response;
		switch(protocol) {
		case SERVER_OPTIMAL:		
			response = serverPool.submit(new ClientSenderThread(website, ProtocolMode.SERVER_OPTIMAL, ClientToServerMode.REGISTERED, username, svk));
			try {
				if(response.get().equals("OK")) {
					return true;
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				return false;
			} 

		case STORAGE_OPTIMAL: case PRIVACY_OPTIMAL :
			response = serverPool.submit(new ClientSenderThread(website, ProtocolMode.STORAGE_OPTIMAL, ClientToServerMode.REGISTERED, username, svk, bsk));
			try {
				if(response.get().equals("OK")) {
					return true;
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				return false;
			}
			break;

		case MOBILE:
			response = serverPool.submit(new ClientSenderThread(website, ProtocolMode.MOBILE, ClientToServerMode.REGISTERED, username, k));
			try {
				if(response.get().equals("OK")) {
					return true;
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				return false;
			}
			break;
		}
		
		return false;
	}

	/**
	 * Function which asks the server a challenge
	 * @param website The address of the server
	 * @return The challenge in the case of Server Optimal protocol and the id and the challenge in the case of Storage Optimal protocol
	 */
	public BigInteger[] askForChallengeToServer(String website) {
		BigInteger[] finalChallenge = new BigInteger[2];
		Future<String> response;
		switch(protocol) {
		case SERVER_OPTIMAL:
			response = serverPool.submit(new ClientSenderThread(website, ProtocolMode.SERVER_OPTIMAL, ClientToServerMode.READYTOAUTH, username));
			try {
				finalChallenge[0] = new BigInteger(response.get());
			} catch (InterruptedException | ExecutionException e1) {
				e1.printStackTrace();
				return null;
			}
			break;

		case STORAGE_OPTIMAL: case PRIVACY_OPTIMAL :
			BigInteger passwordBlinded = AsymmetricEncryption.blind(password, r, (RSAPublicKey) bvk);
			response = serverPool.submit(new ClientSenderThread(website, ProtocolMode.STORAGE_OPTIMAL, ClientToServerMode.READYTOAUTH, username, passwordBlinded));
			try {
				String[] idAndChallenge = response.get().split(",");
				finalChallenge[0] = AsymmetricEncryption.unblind(new BigInteger(idAndChallenge[0]), ((RSAPublicKey) bvk).getModulus(), r);
				finalChallenge[1] = new BigInteger(idAndChallenge[1]);
			} catch (InterruptedException | ExecutionException e1) {
				e1.printStackTrace();
				return null;
			}
			break;
		case MOBILE:
			response = serverPool.submit(new ClientSenderThread(website, ProtocolMode.MOBILE, ClientToServerMode.READYTOAUTH, username));
			try {
				finalChallenge[0] = new BigInteger(response.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				return null;
			}
			break;
		}
		return finalChallenge;
	}
}
