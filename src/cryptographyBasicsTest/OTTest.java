package cryptographyBasicsTest;

import static org.junit.Assert.assertEquals;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Test;

import cryptographyBasics.AsymmetricEncryption;
import cryptographyBasics.MyKeyGenerator;
import cryptographyBasics.OTReceiver;
import cryptographyBasics.OTSender;

public class OTTest {
	@Test
	public void testObliviousTransfer() {		
		Random random = new Random();
		byte[] id1 = new byte[128];
		random.nextBytes(id1);
		byte[] id2 = new byte[128];
		random.nextBytes(id2);
		byte[] id3 = new byte[128];
		random.nextBytes(id3);
		
		byte[] ctext1 = new byte[128];
		random.nextBytes(ctext1);
		byte[] ctext2 = new byte[128];
		random.nextBytes(ctext2);
		byte[] ctext3 = new byte[128];
		random.nextBytes(ctext3);
		
		Map<BigInteger, BigInteger> data = new HashMap<>();
		data.put(new BigInteger(id1), new BigInteger(ctext1));
		data.put(new BigInteger(id2), new BigInteger(ctext2));
		data.put(new BigInteger(id3), new BigInteger(ctext3));

		KeyPair pair = MyKeyGenerator.generateAsymmetricKey();
		RSAPublicKey publicKey = (RSAPublicKey) pair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) pair.getPrivate();
			
		OTSender sender = new OTSender(data, privateKey);
		OTReceiver receiver = new OTReceiver(new BigInteger(id1), publicKey);
		
		ArrayList<byte[]> e = sender.generateE();
		
		BigInteger r = AsymmetricEncryption.generateRForBlindSignature(publicKey.getModulus());
		BigInteger y = receiver.generateY(r);
				
		BigInteger kPrime = sender.generateKprime(y);
				
		BigInteger k = receiver.generateK(kPrime, r);
		
			
		ArrayList<byte[]> AiBi = receiver.generateAiBi(e, k);
		
		BigInteger result = receiver.findValue(AiBi);
		
		assertEquals(result, new BigInteger(ctext1));

	}
}
