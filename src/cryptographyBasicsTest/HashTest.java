package cryptographyBasicsTest;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Test;

import cryptographyBasics.Hash;

public class HashTest {
	
	@Test
	public void test() {
			BigInteger message = BigInteger.valueOf(123);
			BigInteger hash = Hash.generateSHA256Hash(message.toByteArray());
			
			BigInteger realHash = new BigInteger("960651239372262155457909151379123018551187498045146330307848435673183387030");
			
			assertEquals(hash, realHash);
		}
}
