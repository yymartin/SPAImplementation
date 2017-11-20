package cryptographyBasics;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author yoanmartin
 * Library containing cryptographic functions concerning hash
 */
public class Hash {	
	/**
	 * Function which generate a SHA256 hash
	 * @param number The BigInteger to has
	 * @return A hash of the number as a BigInteger
	 */
	public static BigInteger generateSHA256Hash(byte[] number) {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] hash = digest.digest(number);
		return new BigInteger(hash);
	}
}
