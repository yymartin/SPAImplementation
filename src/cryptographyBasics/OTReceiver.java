package cryptographyBasics;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class OTReceiver {
	private final int l = 996; //Need to to be changed later depending on the size of the key and the id
	private BigInteger wj;
	private BigInteger N;
	private RSAPublicKey publicKey;
	
	public OTReceiver(BigInteger wj, RSAPublicKey publicKey) {
		this.wj = wj;
		this.N = publicKey.getModulus();
		this.publicKey = publicKey;
	}
	
	public BigInteger generateY(BigInteger r) {
		BigInteger hashWj = Hash.generateSHA256Hash(wj);
		return AsymmetricEncryption.blind(hashWj, publicKey, r);
	}
	
	public BigInteger generateK(BigInteger kPrime, BigInteger r) {
		return AsymmetricEncryption.unblind(kPrime, N, r);
	}
	
	public ArrayList<byte[]> generateAiBi(ArrayList<byte[]> eArray, BigInteger k){
		ArrayList<byte[]> AiBi = new ArrayList<>();
		for(int i = 0; i < eArray.size(); i++) {
			byte[] eiLeft = eArray.get(i);
			byte[] seed = concatenateThreeArrays(wj.toByteArray(), k.toByteArray(), BigInteger.valueOf(i).toByteArray());
			Random rand = new Random(ByteBuffer.wrap(seed).getLong());
			byte[] eiRight = new byte[1000];
			rand.nextBytes(eiRight);
			AiBi.add(xor(eiLeft, eiRight));
		}
		
		return AiBi;
	}
	
	public BigInteger findValue(ArrayList<byte[]> AiBi) {
		for(byte[] elem : AiBi) {
			if(detectZeros(elem, l)) {
				return new BigInteger(elem);
			}
		}
		return BigInteger.valueOf(-1);
	}
	
	private byte[] concatenateThreeArrays(byte[] a, byte[] b, byte[] c) {
		byte[] result = new byte[a.length + b.length + c.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		System.arraycopy(c, 0, result, b.length, c.length);
		
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
