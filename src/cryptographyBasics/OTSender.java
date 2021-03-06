package cryptographyBasics;

import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

/**
 * @author yoanmartin
 * Class representing the server who send its data to a client. See page 8 from the reference document
 */
public class OTSender {
	private RSAPrivateKey privateKey;
	private ArrayList<BigInteger> w = new ArrayList<>();
	private ArrayList<BigInteger> c = new ArrayList<>();
	private ArrayList<BigInteger> k = new ArrayList<>();
	private BigInteger N;
	private BigInteger d;
	
	/**
	 * Public constructor of the class
	 * @param data A map containing the data 
	 * @param privateKey The RSA private key
	 */
	public OTSender(Map<BigInteger, BigInteger> data, RSAPrivateKey privateKey) {
		this.privateKey = privateKey;
		this.N = privateKey.getModulus();
		this.d = privateKey.getPrivateExponent();
		
		for(Map.Entry<BigInteger, BigInteger> elem : data.entrySet()) {
			w.add(elem.getKey());
			c.add(elem.getValue());
			k.add(Hash.generateSHA256Hash(elem.getKey().toByteArray()).modPow(d, N));
		}
	}
	
	/**
	 * Generate an array E containing the data encrypted (Commit phase)
	 * @return The array E 
	 */
	public ArrayList<byte[]> generateE() {
		ArrayList<byte[]> e = new ArrayList<>();
		for(int i = 0; i < w.size(); i++) {
			byte[] seed = concatenateThreeArrays(w.get(i).toByteArray(), k.get(i).toByteArray(), BigInteger.valueOf(i).toByteArray());
			Random rand = new Random(new BigInteger(seed).longValue());
			byte[] eiLeft = new byte[2048];
			rand.nextBytes(eiLeft);
			
			int l = 	eiLeft.length - c.get(i).toByteArray().length;
			byte[] eiRight = concatenateTwoArrays(generateZeros(l), c.get(i).toByteArray());
			e.add(xor(eiLeft, eiRight));
		}
		return e;
	}
	
	/**
	 * Generate K' from the value Y received by the client. (Step 3)
	 * @param y The value received from the client
	 * @return The value K'
	 */
	public BigInteger generateKprime(BigInteger y) {
		return AsymmetricEncryption.blindSign(y, privateKey);
	}
	
	private byte[] concatenateThreeArrays(byte[] a, byte[] b, byte[] c) {
		byte[] result = new byte[a.length + b.length + c.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		System.arraycopy(c, 0, result, a.length + b.length, c.length);
		
		return result;
	}
	
	private byte[] concatenateTwoArrays(byte[] a, byte[] b) {
		byte[] result = new byte[a.length + b.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		
		return result;
	}

	private byte[] xor(byte[] a, byte[] b) {
		byte[] c = new byte[a.length];
		for(int i = 0; i < a.length; i++) {
			c[i] = (byte) (a[i] ^ b[i]);
		}
		return c;
	}
	
	private byte[] generateZeros(int l) {
		return new byte[l];
	}
	
}
