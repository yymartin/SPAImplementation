package cryptographyBasics;

import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;
import java.util.Random;

public class OTSender {
	private final int randomMessageSize = 1024;
	private BigInteger[] messages;
	private final int numMessages;
	private BigInteger N;
	private BigInteger d;
	
	public OTSender(BigInteger[] messages, RSAPrivateKey privateKey) {
		this.messages = messages;
		this.numMessages = messages.length;
		this.N = privateKey.getModulus();
		this.d = privateKey.getPrivateExponent();
	}
	
	public BigInteger[] generateRandomMessages() {
		Random rand = new Random();
		BigInteger[] randomMessages = new BigInteger[numMessages];
		for(int i = 0; i < numMessages; i++) {
			randomMessages[i] = new BigInteger(randomMessageSize, rand);
		}
		
		return randomMessages;
	}
	
	public BigInteger[] encryptMessages(BigInteger v, BigInteger[] randomMessages) {
		BigInteger[] encryptedMessages = new BigInteger[numMessages];
		for(int i = 0; i < numMessages; i++) {
			encryptedMessages[i] = messages[i].add((v.subtract(randomMessages[i]).modPow(d, N)).mod(N));
		}
		return encryptedMessages;
	}
}
