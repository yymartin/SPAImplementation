package cryptographyBasics;

import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;
import java.util.Random;

//Source: https://en.wikipedia.org/wiki/Oblivious_transfer
public class OTReceiver {
	private final int randomMessageSize = 1024;

	private final int b;
	private final BigInteger N;
	private final BigInteger e;
	private final BigInteger k;
	
	public OTReceiver(int b, RSAPublicKey publicKey) {
		this.b = b;
		this.N = publicKey.getModulus();
		this.e = publicKey.getPublicExponent();
		this.k = generateK();
	}
	
	
	public BigInteger generateV(BigInteger[] randomMessages) {
		return randomMessages[b].add(k.modPow(e, N)).mod(N);
	}
	
	public BigInteger receiveFinalMessage(BigInteger[] encryptedMessages) {
		return encryptedMessages[b].subtract(k);
	}
	
	private BigInteger generateK() {
		Random rand = new Random();
		return new BigInteger(randomMessageSize, rand);
	}
}
