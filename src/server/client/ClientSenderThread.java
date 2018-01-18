package server.client;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.crypto.SecretKey;

import SSLUtility.HTTPUtility;
import SSLUtility.ProtocolMode;
import server.ClientToServerMode;

/**
 * @author yoanmartin
 * Instantiation of a thread which sends information to a server
 */
public class ClientSenderThread implements Callable<String> {
	private ProtocolMode protocol;
	private ClientToServerMode mode;
	private String website, username, password, challenge, k, svk, bsk;
	private Map<String, String> dataToSend;

	/**
	 * Constructor used when the client registers to the server using the Server Optimal protocol
	 * @param website The address of the server
	 * @param protocol The protocol used 
	 * @param mode The actual client state from the server point of view
	 * @param username The username of the client
	 * @param svk The svk received by the client
	 */
	public ClientSenderThread(String website, ProtocolMode protocol, ClientToServerMode mode, String username, PublicKey svk) {
		this.website = website;
		this.protocol = protocol;
		this.mode = mode;
		this.username = username;
		this.svk = Base64.getEncoder().encodeToString(svk.getEncoded());
	}

	/**
	 * Constuctor used when the client asks for a challenge using the Server Optimal protocol
	 * @param website The address of the server
	 * @param protocol The protocol used 
	 * @param mode The actual client state from the server point of view
	 * @param username The username of the client
	 */
	public ClientSenderThread(String website, ProtocolMode protocol, ClientToServerMode mode, String username) {
		this.website = website;
		this.protocol = protocol;
		this.mode = mode;
		this.username = username;
	}

	/**
	 * Constructor used when the client sends the response to a challenge using the Server Optimal or Storage Optimal protocol
	 * @param website The address of the server
	 * @param mode The actual client state from the server point of view
	 * @param username The username of the client
	 * @param challenge The challenge received by the client
	 */
	public ClientSenderThread(String website, ClientToServerMode mode, String username, BigInteger challenge) {
		this.website = website;
		this.protocol = SSLUtility.ProtocolMode.SERVER_OPTIMAL;
		this.mode = mode;
		this.username = username;
		this.challenge = challenge.toString();
	}


	/**
	 * Constructor used when the client registers to the server using the Storage Optimal protocol
	 * @param website The address of the server
	 * @param protocol The protocol used 
	 * @param mode The actual client state from the server point of view
	 * @param username The username of the client
	 * @param svk The svk of the client
	 * @param bsk The bsk of the client
	 */
	public ClientSenderThread(String website, ProtocolMode protocol, ClientToServerMode mode, String username, PublicKey svk, PrivateKey bsk) {
		this.website = website;
		this.protocol = protocol;
		this.mode = mode;
		this.username = username;
		this.svk = Base64.getEncoder().encodeToString(svk.getEncoded());
		this.bsk = Base64.getEncoder().encodeToString(bsk.getEncoded());
	}

	/**
	 * Constructor used when the client asks for a challenge using the Storage Optimal protocol
	 * @param website The address of the server
	 * @param protocol The protocol used 
	 * @param mode The actual client state from the server point of view
	 * @param username The username of the client
	 * @param password The hashed password of the client
	 */
	public ClientSenderThread(String website, ProtocolMode protocol, ClientToServerMode mode, String username, BigInteger password) {
		this.website = website;
		this.protocol = protocol;
		this.mode = mode;
		this.username = username;
		this.password = password.toString();
	}

	/**
	 * Constructor used when the client registers to the server using Mobile Protocol
	 * @param website The address of the server
	 * @param protocol The protocol used
	 * @param register The client state for the server
	 * @param username The username of the client
	 * @param k The SecretKey of the client
	 */
	public ClientSenderThread(String website, ProtocolMode protocol, ClientToServerMode register, String username, SecretKey k) {
		this.website = website;
		this.protocol = protocol;
		this.mode = ClientToServerMode.REGISTERED;
		this.username = username;
		this.k = Base64.getEncoder().encodeToString(k.getEncoded());
	}

	@Override
	public String call() {
		String response = null;
		switch(protocol) {
		case SERVER_OPTIMAL:
			switch(mode) {
			case REGISTERED:
				dataToSend = new HashMap<>();
				dataToSend.put("protocol", protocol.toString());
				dataToSend.put("mode", mode.toString());
				dataToSend.put("username", username);
				dataToSend.put("svk", svk);

				response = HTTPUtility.executePost(website, dataToSend);
				return response;
			case READYTOAUTH:
				dataToSend = new HashMap<>();
				dataToSend.put("protocol", protocol.toString());
				dataToSend.put("mode", mode.toString());
				dataToSend.put("username", username);

				response = HTTPUtility.executePost(website, dataToSend);
				return response;
			case AUTH:
				dataToSend = new HashMap<>();
				dataToSend.put("protocol", protocol.toString());
				dataToSend.put("mode", mode.toString());
				dataToSend.put("username", username);
				dataToSend.put("challenge", challenge);

				response = HTTPUtility.executePost(website, dataToSend);
				return response;
			}
			break;

		case STORAGE_OPTIMAL: case PRIVACY_OPTIMAL:
			switch(mode) { 
			case REGISTERED:
				dataToSend = new HashMap<>();

				dataToSend.put("protocol", ProtocolMode.STORAGE_OPTIMAL.toString());
				dataToSend.put("mode", mode.toString());
				dataToSend.put("username", username);
				dataToSend.put("svk", svk);
				dataToSend.put("bsk", bsk);

				response = HTTPUtility.executePost(website, dataToSend);
				return response;
			case READYTOAUTH:
				dataToSend = new HashMap<>();

				dataToSend.put("protocol", ProtocolMode.STORAGE_OPTIMAL.toString());
				dataToSend.put("mode", mode.toString());
				dataToSend.put("username", username);
				dataToSend.put("password", password);
				response = HTTPUtility.executePost(website, dataToSend);
				return response;
			case AUTH:
				break;
			}
			break;

		case MOBILE:
			switch(mode) {
			case REGISTERED:
				dataToSend = new HashMap<>();

				dataToSend.put("protocol", protocol.toString());
				dataToSend.put("mode", mode.toString());
				dataToSend.put("username", username);
				dataToSend.put("k", k);

				response = HTTPUtility.executePost(website, dataToSend);
				return response;
			case READYTOAUTH:
			case AUTH:
				break;
			default:
				break;
			}
			break;
		}

		Thread.currentThread().interrupt();
		return "";
	}
}