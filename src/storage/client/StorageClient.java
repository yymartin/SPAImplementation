package storage.client;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import SSLUtility.ProtocolMode;
import SSLUtility.SSLClientUtility;
import cryptographyBasics.AsymmetricEncryption;
import cryptographyBasics.Hash;
import cryptographyBasics.MyKeyGenerator;
import cryptographyBasics.OTReceiver;
import cryptographyBasics.SymmetricEncryption;

/**
 * @author yoanmartin
 * Object which instantiate the client side of the connection with a storage
 */
public class StorageClient {

	private ExecutorService ex = Executors.newSingleThreadScheduledExecutor();
	
	private Socket socket = null;
	private DataInputStream in = null;
	private DataOutputStream out = null;
	
	private ProtocolMode protocol;
	private BigInteger id;
	private BigInteger password;
	private BigInteger r;
	private PublicKey bvk;
	private PrivateKey bsk, ssk;

	/**
	 * Constructor used when the Server Optimal protocol is used
	 * @param username The username of the user
	 * @param password The password of the user
	 * @param website The website which the user wants to connect to
	 * @param bsk The bsk of the user
	 * @param ssk The ssk of the user
	 * @param r The blind factor of the user
	 */
	public StorageClient(String username, String password, String website, PrivateKey bsk, PublicKey bvk, PrivateKey ssk, BigInteger r) {
		this.protocol = SSLUtility.ProtocolMode.SERVER_OPTIMAL;
		this.id = Hash.generateSHA256Hash((username+website).getBytes());
		this.password = Hash.generateSHA256Hash(password.getBytes());
		this.r = r;
		this.bsk = bsk;
		this.bvk = bvk;
		this.ssk = ssk;
	}

	/**
	 * Constructor used when the Storage Optimal protocol is used
	 * @param password The password of the user
	 * @param bsk The bsk of the user
	 * @param ssk The ssk of the user
	 * @param r The blind factor of the user
	 */
	public StorageClient(ProtocolMode protocol, String password, PrivateKey bsk, PublicKey bvk, PrivateKey ssk, BigInteger r) {
		this.protocol = protocol;
		this.password = Hash.generateSHA256Hash(password.getBytes());
		this.r = r;
		this.bsk = bsk;
		this.ssk = ssk;
	}

	/**
	 * Function which stores the necessary values to the storage
	 */
	public boolean storeValuesToStorage() {		
		try {
			InputStream key = new FileInputStream(new File("./PUBLICKEY.jks"));
			socket = SSLClientUtility.getSocketWithCert(InetAddress.getLocalHost(), 2009, key, "8rXbM7twa)E96xtFZmWq6/J^");
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		byte[] ctext;
		PublicKey storageKey = null;
		switch(protocol) {
		case SERVER_OPTIMAL:
			BigInteger sig = AsymmetricEncryption.blindSign(password, (RSAPrivateKey) bsk);
			byte[] oneTimePadkey = MyKeyGenerator.generateOneTimePaddingKeyFromPassword(sig);
			ctext = SymmetricEncryption.encryptOneTimePadding(ssk.getEncoded(), oneTimePadkey);
			try {
				Future<Boolean> result = ex.submit(new ClientSenderThread(in, out, id, bsk, ctext));
				return result.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				return false;
			}
		case STORAGE_OPTIMAL:
			id = AsymmetricEncryption.blindSign(password, (RSAPrivateKey) bsk);
			ctext = generateCTextWithOneTimePadding(password, ssk);

			try {
				Future<Boolean> result = ex.submit(new ClientSenderThread(in, out, protocol, id, ctext));
				return result.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				return false;
			} 
		case PRIVACY_OPTIMAL:
			id = AsymmetricEncryption.blindSign(password, (RSAPrivateKey) bsk);
			ctext = generateCTextWithOneTimePadding(password, ssk);

			try {
				ex.submit(new ClientSenderThread(in, out, protocol, id, ctext));
				Future<PublicKey> result = ex.submit(new ClientReceivePublicThread(in));
				storageKey = result.get();
				if(storageKey != null) {
					String address = System.getProperty("user.dir");
					storePublicKeyToFile(storageKey, address, "OT");
					return true;
				} else {
					return false;
				}

			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				return false;
			} 
		case MOBILE:
			break;
		default:
			break;
		}	
		return false;
	}

	/**
	 * Function which retrieves the values from the storage and uses them to retrieve the ssk
	 * @return The ssk of the user
	 */
	public PrivateKey retrieveValuesFromStorage(BigInteger idFromStorage, PublicKey obliviousTransferKey) {		
		try {
			InputStream key = new FileInputStream(new File("./PUBLICKEY.jks"));
			socket = SSLClientUtility.getSocketWithCert(InetAddress.getLocalHost(), 2009, key, "8rXbM7twa)E96xtFZmWq6/J^");
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		PrivateKey resultKey = null;

		switch(protocol) {
		case SERVER_OPTIMAL:
			try {
				BigInteger passwordBlinded = AsymmetricEncryption.blind(password, r, (RSAPublicKey) bvk);
				ex.submit(new ClientSenderThread(out, id, passwordBlinded));
				Future<PrivateKey> result = ex.submit(new ClientReceivePrivateThread(in, r, bvk));
				resultKey = result.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				return null;
			} 
			return resultKey;

		case STORAGE_OPTIMAL:
			try {
				ex.submit(new ClientSenderThread(out, protocol, idFromStorage));
				Future<PrivateKey> result = ex.submit(new ClientReceivePrivateThread(in, r, bvk, password));
				resultKey = result.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				return null;
			} 
			return resultKey;

		case PRIVACY_OPTIMAL:
			try {
				OTReceiver obliviousReceiver = new OTReceiver(idFromStorage, (RSAPublicKey) obliviousTransferKey);
				BigInteger obliviousR = AsymmetricEncryption.generateRForBlindSignature(((RSAPublicKey) obliviousTransferKey).getModulus());
				BigInteger y = obliviousReceiver.generateY(obliviousR);
				ex.submit(new ClientSenderThread(out, protocol, y));
				Future<PrivateKey> result = ex.submit(new ClientReceivePrivateThread(in, obliviousReceiver, obliviousR, password));
				resultKey = result.get();		
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				return null;
			}  
			return resultKey;
		case MOBILE:
			break;
		default:
			break;
		}
		return null;
	}

	private byte[] generateCTextWithOneTimePadding(BigInteger password, PrivateKey ssk) {
		byte[] sskAsByte = ssk.getEncoded();
		byte[] oneTimePadKey = MyKeyGenerator.generateOneTimePaddingKeyFromPassword(password);
		return SymmetricEncryption.encryptOneTimePadding(sskAsByte, oneTimePadKey);
	}
	
	public void storePublicKeyToFile(PublicKey key, String address, String title) {
		Path path = Paths.get(address+"/Public-Key-"+title);
		try {
			Files.write(path, key.getEncoded());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}