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
	private String username, password, challenge, k, svk, bsk;
	private Map<String, String> dataToSend;

	private String address = "https://localhost:8443/pro/register";


	/**
	 * Constructor used when the client registers to the server using the Server Optimal protocol
	 * @param out The DataOutputStream given by the client 
	 * @param protocol The protocol used 
	 * @param mode The actual client state from the server point of view
	 * @param username
	 * @param svk
	 */
	public ClientSenderThread(ProtocolMode protocol, ClientToServerMode mode, String username, PublicKey svk) {
		this.protocol = protocol;
		this.mode = mode;
		this.username = username;
		this.svk = Base64.getEncoder().encodeToString(svk.getEncoded());
	}

	/**
	 * Constuctor used when the client asks for a challenge using the Server Optimal protocol
	 * @param out The DataOutputStream given by the server 
	 * @param protocol The protocol used 
	 * @param mode The actual client state from the server point of view
	 * @param username The username of the client
	 */
	public ClientSenderThread(ProtocolMode protocol, ClientToServerMode mode, String username) {
		this.protocol = protocol;
		this.mode = mode;
		this.username = username;
	}

	/**
	 * Constructor used when the client sends the response to a challenge using the Server Optimal or Storage Optimal protocol
	 * @param out The DataOutputStream given by the server 
	 * @param mode The actual client state from the server point of view
	 * @param username The username of the client
	 * @param challenge The challenge received by the client
	 */
	public ClientSenderThread(ClientToServerMode mode, String username, BigInteger challenge) {
		this.protocol = SSLUtility.ProtocolMode.SERVER_OPTIMAL;
		this.mode = mode;
		this.username = username;
		this.challenge = challenge.toString();
	}


	/**
	 * Constructor used when the client registers to the server using the Storage Optimal protocol
	 * @param out The DataOutputStream given by the server 
	 * @param protocol The protocol used 
	 * @param mode The actual client state from the server point of view
	 * @param username The username of the client
	 * @param svk The svk of the client
	 * @param bsk The bsk of the client
	 */
	public ClientSenderThread(ProtocolMode protocol, ClientToServerMode mode, String username, PublicKey svk, PrivateKey bsk) {
		this.protocol = protocol;
		this.mode = mode;
		this.username = username;
		this.svk = Base64.getEncoder().encodeToString(svk.getEncoded());
		this.bsk = Base64.getEncoder().encodeToString(bsk.getEncoded());
	}

	/**
	 * Constructor used when the client asks for a challenge using the Storage Optimal protocol
	 * @param out The DataOutputStream given by the server 
	 * @param protocol The protocol used 
	 * @param mode The actual client state from the server point of view
	 * @param username The username of the client
	 * @param password The hashed password of the client
	 */
	public ClientSenderThread(ProtocolMode protocol, ClientToServerMode mode, String username, BigInteger password) {
		this.protocol = protocol;
		this.mode = mode;
		this.username = username;
		this.password = password.toString();
	}

	public ClientSenderThread(ProtocolMode protocol, ClientToServerMode register, String username, SecretKey k) {
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

				response = HTTPUtility.executePost(address, dataToSend);
				return response;
			case READYTOAUTH:
				dataToSend = new HashMap<>();
				dataToSend.put("protocol", protocol.toString());
				dataToSend.put("mode", mode.toString());
				dataToSend.put("username", username);

				response = HTTPUtility.executePost(address, dataToSend);
				return response;
			case AUTH:
				dataToSend = new HashMap<>();
				dataToSend.put("protocol", protocol.toString());
				dataToSend.put("mode", mode.toString());
				dataToSend.put("username", username);
				dataToSend.put("challenge", challenge);

				response = HTTPUtility.executePost(address, dataToSend);
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

				response = HTTPUtility.executePost(address, dataToSend);
				return response;
			case READYTOAUTH:
				dataToSend = new HashMap<>();

				dataToSend.put("protocol", ProtocolMode.STORAGE_OPTIMAL.toString());
				dataToSend.put("mode", mode.toString());
				dataToSend.put("username", username);
				dataToSend.put("password", password);

				response = HTTPUtility.executePost(address, dataToSend);
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

				response = HTTPUtility.executePost(address, dataToSend);
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