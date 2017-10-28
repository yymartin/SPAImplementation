package cryptographyBasics;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * @author yoanmartin
 * Class representing the client who asks for data from an id. See page 8 from the reference document
 */
public class OTReceiver {
	private final int l = 1024; //Need to to be changed later depending on the size of the key and the id
	private BigInteger wj;
	private BigInteger N;
	private RSAPublicKey publicKey;
	
	/**
	 * Public constructor of the class
	 * @param wj The id corresponding to the data
	 * @param publicKey The RSA public key given by the server
	 */
	public OTReceiver(BigInteger wj, RSAPublicKey publicKey) {
		this.wj = wj;
		this.N = publicKey.getModulus();
		this.publicKey = publicKey;
	}
	
	/**
	 * Generate Y using the id and blind signature (Step 3)
	 * @param r The BigInteger used as blind factor
	 * @return Y as a BigInteger
	 */
	public BigInteger generateY(BigInteger r) {
		BigInteger hashWj = Hash.generateSHA256Hash(wj.toByteArray());
		return AsymmetricEncryption.blind(hashWj, r, publicKey);
	}
	
	/**
	 * Generate K using K' (Step 4)
	 * @param kPrime The value K' received from the server
	 * @param r The BigInteger used as blind factor
	 * @return K as a BigInteger
	 */
	public BigInteger generateK(BigInteger kPrime, BigInteger r) {
		return AsymmetricEncryption.unblind(kPrime, N, r);
	}
	
	/**
	 * Generate the final data from all the values received from the server
	 * @param eArray The byte array containing values encrypted by the server
	 * @param k K signed by the server and unblinded by the client 
	 * @return The final data computed by the client using every data received from the server
	 */
	public ArrayList<byte[]> generateAiBi(ArrayList<byte[]> eArray, BigInteger k){
		ArrayList<byte[]> AiBi = new ArrayList<>();
		for(int i = 0; i < eArray.size(); i++) {
			byte[] eiLeft = eArray.get(i);
			byte[] seed = concatenateThreeArrays(wj.toByteArray(), k.toByteArray(), BigInteger.valueOf(i).toByteArray());
			Random rand = new Random(new BigInteger(seed).longValue());
			byte[] eiRight = new byte[2048];
			rand.nextBytes(eiRight);
			AiBi.add(xor(eiLeft, eiRight));
		}
		
		return AiBi;
	}
	
	/**
	 * Find the data corresponding to the id
	 * @param AiBi The final data computed with the data received from the server
	 * @return The data corresponding to the id
	 */
	public BigInteger findValue(ArrayList<byte[]> AiBi) {
		for(byte[] elem : AiBi) {
			if(detectZeros(elem, l)) {
				return new BigInteger(Arrays.copyOfRange(elem, l, elem.length));
			}
		}
		return BigInteger.valueOf(-1);
	}
	
	private byte[] concatenateThreeArrays(byte[] a, byte[] b, byte[] c) {
		byte[] result = new byte[a.length + b.length + c.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		System.arraycopy(c, 0, result, a.length + b.length, c.length);
		
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
	
	private boolean detectZeros(byte[] a, int l) {
		byte[] zeros = generateZeros(l);
		byte[] subArray = Arrays.copyOfRange(a, 0, l);

		return Arrays.equals(subArray, zeros);
	}	
}
