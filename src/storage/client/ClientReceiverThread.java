package storage.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.concurrent.Callable;

import javax.crypto.SecretKey;

import SSLUtility.ProtocolMode;
import cryptographyBasics.AsymmetricEncryption;
import cryptographyBasics.MyKeyGenerator;
import cryptographyBasics.SymmetricEncryption;

public class ClientReceiverThread implements Callable<PrivateKey> {
	private DataInputStream in;
	private ProtocolMode protocol;
	private BigInteger r;
	private PublicKey bvk;

	public ClientReceiverThread(DataInputStream in, ProtocolMode protocol, BigInteger r, PublicKey bvk) {
		this.in = in;
		this.protocol = protocol;
		this.r = r;
		this.bvk = bvk;
	}


	@Override
	public PrivateKey call() throws Exception{
	PrivateKey resultKey = null;
	byte[] ctext;
		switch(protocol) {
		case SERVER_OPTIMAL :
			BigInteger sigBlinded = new BigInteger(getData());
			BigInteger sig = AsymmetricEncryption.unblind(sigBlinded, ((RSAPublicKey) bvk).getModulus(), r);
			ctext = getData();
			SecretKey aesKey = MyKeyGenerator.generateAESKeyFromPassword(sig);
			
			byte[] keyAsByte = SymmetricEncryption.decryptAES(ctext, aesKey);
		
			resultKey = MyKeyGenerator.convertByteArrayIntoPrivateKey(keyAsByte);
			break;
			
//		case STORAGE_OPTIMAL :
//			ctext = new BigInteger(getData());
//			result[0] = ctext;
//			break;
			
		case PRIVACY_OPTIMAL :
			
			break;
		}
		return resultKey;
	}
	
	private byte[] getData() {
		byte[] result = null;
		int length;
		try {
			length = in.readInt();
			if(length > 0) {
				result = new byte[length];
				in.readFully(result, 0, result.length); 
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return result;
	}
}
