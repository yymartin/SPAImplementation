package server.server;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.SecretKey;

/**
 * @author yoanmartin
 * Object which represents a client for the server
 */
public class Client {
	/**
	 * Enumeration representing the actual state of the client
	 */
	public enum ClientMode {
		REGISTERED {
			@Override
			public String toString() {
				return "REGISTERED";
			}
		}, READYTOAUTH {
			@Override
			public String toString() {
				return "READYTOAUTH";
			}
		}, AUTH {
			@Override
			public String toString() {
				return "AUTH";
			}
		}
	}
	
	private String username;
	private PublicKey svk;
	private PrivateKey bsk;
	private SecretKey k;
	private BigInteger challenge;
	private ClientMode mode;
	
	/**
	 * Constructor used when the Server Optimal protocol is used
	 * @param username The username of the client
	 * @param svk The svk of the client
	 */
	public Client(String username, PublicKey svk) {
		this.username = username;
		this.svk = svk;
		this.mode = ClientMode.REGISTERED;
	}
	
	/**
	 * Constructor used when the Storage Optimal protocol is used
	 * @param username The username of the client
	 * @param svk The svk of the client
	 * @param bsk The bsk of the client
	 */
	public Client(String username, PublicKey svk, PrivateKey bsk) {
		this.username = username;
		this.svk = svk;
		this.bsk = bsk;
	}

	public Client(String username, SecretKey k) {
		this.username = username;
		this.k = k;
	}

	/**
	 * Function which store the last challenge sent to the client
	 * @param challenge The challenge sent
	 */
	public void setChallenge(BigInteger challenge) {
		this.mode = ClientMode.READYTOAUTH;
		this.challenge = challenge;
	}
	
	/**
	 * Function which change the state of the client when authenticated
	 */
	public void isAuthenticated() {
		this.mode = ClientMode.AUTH;
	}
	
	/**
	 * Function which returns the state of the client
	 * @return The state of the client
	 */
	public ClientMode getState() {
		return mode;
	}
	
	/**
	 * Function which say if a challenge was sent to the client
	 * @return The boolean saying if the challenge was sent
	 */
	public boolean isReadyToAuth() {
		return mode == ClientMode.READYTOAUTH;
	}
	
	/**
	 * Function which return the challenge set to a client
	 * @return
	 */
	public BigInteger getChallenge() {
		return challenge;
	}
	
	/**
	 * Function which return the svk of the client
	 * @return The svk of the client
	 */
	public PublicKey getSvk() {
		return svk;
	}
	
	/**
	 * Function which return the bsk of the client
	 * @return The bsk of the client
	 */
	public PrivateKey getBsk() {
		return bsk;
	}
	
	public SecretKey getK() {
		return k;
	}
	
	@Override
	public String toString() {
		RSAPublicKey publicKey = (RSAPublicKey) svk;
		return "STATE : " + mode.toString() + " SVK : " + publicKey.toString();
	}
}

