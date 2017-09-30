package cryptographyBasicsTest;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Random;

import org.junit.Test;

import cryptographyBasics.MyKeyGenerator;
import cryptographyBasics.OTReceiver;
import cryptographyBasics.OTSender;

public class OTTest {
	private final int messageSize = 1024;
	private final int numMessages = 50;
	
	@Test
	public void testObliviousTransfer() {
		BigInteger[] messages = new BigInteger[numMessages];
		Random rand = new Random();
		for(int i = 0; i < numMessages; i++) {
			messages[i] = new BigInteger(messageSize, rand);
		}
		
		KeyPair keys = MyKeyGenerator.generateAssymetricKey();
		
		OTSender sender = new OTSender(messages, (RSAPrivateKey) keys.getPrivate());
		OTReceiver receiver = new OTReceiver(5, (RSAPublicKey) keys.getPublic());
		
		BigInteger[] randomMessages = sender.generateRandomMessages();
		BigInteger v = receiver.generateV(randomMessages);
		BigInteger[] encryptedMessages = sender.encryptMessages(v, randomMessages);
		
		BigInteger finalMessage = receiver.receiveFinalMessage(encryptedMessages);
		
		assertEquals(messages[5], finalMessage);
	}
}
