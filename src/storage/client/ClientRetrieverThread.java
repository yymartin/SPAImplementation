package storage.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import javax.crypto.SecretKey;

import SSLUtility.ProtocolMode;
import cryptographyBasics.AsymmetricEncryption;
import cryptographyBasics.MyKeyGenerator;
import cryptographyBasics.OTReceiver;
import cryptographyBasics.SymmetricEncryption;

/**
 * @author yoanmartin
 * Instantiation of a thread which receives information from a storage
 */
public class ClientRetrieverThread implements Callable<PrivateKey> {
	private DataInputStream in;
	private ProtocolMode protocol;
	private BigInteger r;
	private PublicKey bvk;
	private BigInteger password;
	private OTReceiver receiver;

	
	/**
	 * Constructor used when the Server Optimal protocol is used
	 * @param in The DataInputStream received by the client
	 * @param r The blind factor received by the client
	 * @param bvk The bvk received by the client
	 */
	public ClientRetrieverThread(DataInputStream in, BigInteger r, PublicKey bvk) {
		this.in = in;
		this.protocol = SSLUtility.ProtocolMode.SERVER_OPTIMAL;
		this.r = r;
		this.bvk = bvk;
	}
	
	/**
	 * Constructor used when the Storage Optimal protocol is used
	 * @param in The DataInputStream received by the client
	 * @param r The blind factor received by the client
	 * @param bvk The bvk received by the client
	 * @param password The password received by the client
	 */
	public ClientRetrieverThread(DataInputStream in, BigInteger r, PublicKey bvk, BigInteger password) {
		this.in = in;
		this.protocol = SSLUtility.ProtocolMode.STORAGE_OPTIMAL;
		this.r = r;
		this.bvk = bvk;
		this.password = password;
	}
	
	public ClientRetrieverThread(DataInputStream in, OTReceiver receiver, BigInteger r, BigInteger password) {
		this.in = in;
		this.protocol = SSLUtility.ProtocolMode.PRIVACY_OPTIMAL;
		this.receiver = receiver;
		this.r = r;
		this.password = password;
	}
	

	@Override
	public PrivateKey call() throws Exception{
	PrivateKey resultKey = null;
	SecretKey aesKey;
	byte[] oneTimePadKey;
	byte[] keyAsByte;
	byte[] ctext;

	switch(protocol) {
		case SERVER_OPTIMAL :
			BigInteger sigBlinded = new BigInteger(getData());
			BigInteger sig = AsymmetricEncryption.unblind(sigBlinded, ((RSAPublicKey) bvk).getModulus(), r);
			ctext = getData();
//			aesKey = MyKeyGenerator.generateAESKeyFromPassword(sig);
			oneTimePadKey = MyKeyGenerator.getOneTimePaddingKeyFromPassword(sig);
			
//			keyAsByte = SymmetricEncryption.decryptAES(ctext, aesKey);
			keyAsByte = SymmetricEncryption.decryptOneTimePadding(ctext, oneTimePadKey);
		
			resultKey = MyKeyGenerator.convertByteArrayIntoPrivateKey(keyAsByte);
			break;
			
		case STORAGE_OPTIMAL :
			ctext = getData();
//			aesKey = MyKeyGenerator.generateAESKeyFromPassword(password);
			oneTimePadKey = MyKeyGenerator.getOneTimePaddingKeyFromPassword(password);

//			keyAsByte = SymmetricEncryption.decryptAES(ctext, aesKey);
			keyAsByte = SymmetricEncryption.decryptOneTimePadding(ctext, oneTimePadKey);
			
			resultKey = MyKeyGenerator.convertByteArrayIntoPrivateKey(keyAsByte);
			break;
			
		case PRIVACY_OPTIMAL :
			byte[] kPrime = getData();
			ArrayList<byte[]> e = new ArrayList<>();
			while(in.available() > 0) {
				e.add(getData());
			}
			BigInteger k = receiver.generateK(new BigInteger(kPrime), r);
			ArrayList<byte[]> AiBi = receiver.generateAiBi(e, k);
			ctext = receiver.findValue(AiBi).toByteArray();
			System.out.println(new BigInteger(ctext));
//			aesKey = MyKeyGenerator.generateAESKeyFromPassword(password);
			oneTimePadKey = MyKeyGenerator.getOneTimePaddingKeyFromPassword(password);
			
//			keyAsByte = SymmetricEncryption.decryptAES(ctext, aesKey);
			keyAsByte = SymmetricEncryption.decryptOneTimePadding(ctext, oneTimePadKey);

			resultKey = MyKeyGenerator.convertByteArrayIntoPrivateKey(keyAsByte);
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
