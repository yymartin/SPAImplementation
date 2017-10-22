package server.server;

import java.math.BigInteger;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;

public class Client {
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
	private BigInteger challenge;
	private ClientMode mode;
	
	public Client(String username, PublicKey svk) {
		this.username = username;
		this.svk = svk;
		this.mode = ClientMode.REGISTERED;
	}
	
	public void setChallenge(BigInteger challenge) {
		this.mode = ClientMode.READYTOAUTH;
		this.challenge = challenge;
	}
	
	public void isAuthenticated() {
		this.mode = ClientMode.AUTH;
	}
	
	public ClientMode getState() {
		return mode;
	}
	
	public boolean isReadyToAuth() {
		return mode == ClientMode.READYTOAUTH;
	}
	
	public BigInteger getChallenge() {
		return challenge;
	}
	
	public PublicKey getSvk() {
		return svk;
	}
	
	@Override
	public String toString() {
		RSAPublicKey publicKey = (RSAPublicKey) svk;
		return "STATE : " + mode.toString() + " SVK : " + publicKey.toString();
	}
}

