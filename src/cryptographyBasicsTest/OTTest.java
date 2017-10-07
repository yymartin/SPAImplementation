package cryptographyBasicsTest;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import cryptographyBasics.AsymmetricEncryption;
import cryptographyBasics.MyKeyGenerator;
import cryptographyBasics.OTReceiver;
import cryptographyBasics.OTSender;

public class OTTest {
	@Test
	public void testObliviousTransfer() {
		Map<BigInteger, BigInteger> data = new HashMap<>();
		data.put(BigInteger.valueOf(437128), BigInteger.valueOf(8432901));
		data.put(BigInteger.valueOf(574890), BigInteger.valueOf(4378979));
		data.put(BigInteger.valueOf(947439), BigInteger.valueOf(5478932));
		
		KeyPair pair = MyKeyGenerator.generateAssymetricKey();
		RSAPublicKey publicKey = (RSAPublicKey) pair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) pair.getPrivate();
		
		OTSender sender = new OTSender(data, privateKey);
		OTReceiver receiver = new OTReceiver(BigInteger.valueOf(437128), publicKey);
		
		ArrayList<byte[]> e = sender.generateE();
		
		BigInteger r = AsymmetricEncryption.generateRForBlindSignature(publicKey.getModulus());
		BigInteger y = receiver.generateY(r);
		
		BigInteger kPrime = sender.generateKprime(y);
		
		BigInteger k = receiver.generateK(kPrime, r);
		
		ArrayList<byte[]> AiBi = receiver.generateAiBi(e, k);
		
		BigInteger result = receiver.findValue(AiBi);
		
		assertEquals(result, BigInteger.valueOf(8432901));

	}
}
