package storage.client;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.crypto.SecretKey;

import SSLUtility.ProtocolMode;
import SSLUtility.SSLClientUtility;
import cryptographyBasics.AsymmetricEncryption;
import cryptographyBasics.Hash;
import cryptographyBasics.MyKeyGenerator;
import cryptographyBasics.OTReceiver;
import cryptographyBasics.SymmetricEncryption;
import user.UserApplication;

/**
 * @author yoanmartin
 * Object which instantiate the client side of the connection with a storage
 */
public class StorageClient {

	public static Socket socket = null;
	public static Thread t1, t2;
	private static DataInputStream in;
	private static DataOutputStream out;

	private static ExecutorService ex = Executors.newSingleThreadExecutor();

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
	public PublicKey storeValuesToStorage() {
		UserApplication.output = UserApplication.output + "\n Try to store values into storage";
		byte[] ctext;
		PublicKey storageKey = null;
		switch(protocol) {
		case SERVER_OPTIMAL:
			BigInteger sig = AsymmetricEncryption.sign(password, (RSAPrivateKey) bsk);
			//			SecretKey aesKey = MyKeyGenerator.generateAESKeyFromPassword(sig);
			byte[] oneTimePadkey = MyKeyGenerator.generateOneTimePaddingKeyFromPassword(sig);
			//			ctext = SymmetricEncryption.encryptAES(ssk.getEncoded(), aesKey);
			ctext = SymmetricEncryption.encryptOneTimePadding(ssk.getEncoded(), oneTimePadkey);

			try {
				InputStream key = new FileInputStream(new File("./PUBLICKEY.jks"));
				socket = SSLClientUtility.getSocketWithCert(InetAddress.getLocalHost(), 2009, key, "8rXbM7twa)E96xtFZmWq6/J^");
				out = new DataOutputStream(socket.getOutputStream());
				ex.execute(new ClientSenderThread(out, id, bsk, ctext));
				UserApplication.output = UserApplication.output + "\n Correctly store values into the storage";
			} catch (IOException e) {
				UserApplication.output = UserApplication.output + "\n Something went wrong, you are not registered to the storage";
				e.printStackTrace();
			}
			break;
		case STORAGE_OPTIMAL:
			id = AsymmetricEncryption.sign(password, (RSAPrivateKey) bsk);
			ctext = generateCTextWithOneTimePadding(password, ssk);

			try {
				InputStream key = new FileInputStream(new File("./PUBLICKEY.jks"));
				socket = SSLClientUtility.getSocketWithCert(InetAddress.getLocalHost(), 2009, key, "8rXbM7twa)E96xtFZmWq6/J^");
				out = new DataOutputStream(socket.getOutputStream());
				ex.execute(new ClientSenderThread(out, protocol, id, ctext));
				UserApplication.output = UserApplication.output + "\n Correctly store values into the storage";
			} catch (IOException e) {
				UserApplication.output = UserApplication.output + "\n Something went wrong, you are not registered to the storage";
				e.printStackTrace();
			} 
			break;
		case PRIVACY_OPTIMAL:
			id = AsymmetricEncryption.sign(password, (RSAPrivateKey) bsk);
			ctext = generateCTextWithOneTimePadding(password, ssk);

			try {
				InputStream key = new FileInputStream(new File("./PUBLICKEY.jks"));
				socket = SSLClientUtility.getSocketWithCert(InetAddress.getLocalHost(), 2009, key, "8rXbM7twa)E96xtFZmWq6/J^");
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
				ex.submit(new ClientSenderThread(out, protocol, id, ctext));
				Future<PublicKey> result = ex.submit(new ClientReceiverThread(in));
				storageKey = result.get();
				if(storageKey != null) {
					UserApplication.output = UserApplication.output + "\n Correctly store value into the storage";
				} else {
					UserApplication.output = UserApplication.output + "\n Something went wrong, you are not registered to the storage";
				}

			} catch (IOException | InterruptedException | ExecutionException e) {
				UserApplication.output = UserApplication.output + "\n Something went wrong, you are not registered to the storage";
				e.printStackTrace();
			} 
			break;
		case MOBILE:
			break;
		default:
			break;
		}	
		return storageKey;
	}

	/**
	 * Function which retrieves the values from the storage and uses them to retrieve the ssk
	 * @return The ssk of the user
	 */
	public PrivateKey retrieveValuesFromStorage(BigInteger idFromStorage, PublicKey obliviousTransferKey) {
		UserApplication.output = UserApplication.output + "\n Try to retrieve values from storage";

		PrivateKey resultKey = null;

		switch(protocol) {
		case SERVER_OPTIMAL:
			try {
				InputStream key = new FileInputStream(new File("./PUBLICKEY.jks"));
				socket = SSLClientUtility.getSocketWithCert(InetAddress.getLocalHost(), 2009, key, "8rXbM7twa)E96xtFZmWq6/J^");
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
				BigInteger passwordBlinded = AsymmetricEncryption.blind(password, r, (RSAPublicKey) bvk);
				ex.submit(new ClientSenderThread(out, id, passwordBlinded));
				Future<PrivateKey> result = ex.submit(new ClientRetrieverThread(in, r, bvk));
				resultKey = result.get();
				UserApplication.output = UserApplication.output + "\n Correctly retrieve values from the storage";
			} catch (IOException | InterruptedException | ExecutionException e) {
				UserApplication.output = UserApplication.output + "\n Something went wrong, unable to retrieve values from the storage";
				e.printStackTrace();
			} 
			break;

		case STORAGE_OPTIMAL:
			try {
				InputStream key = new FileInputStream(new File("./PUBLICKEY.jks"));
				socket = SSLClientUtility.getSocketWithCert(InetAddress.getLocalHost(), 2009, key, "8rXbM7twa)E96xtFZmWq6/J^");
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
				ex.submit(new ClientSenderThread(out, protocol, idFromStorage));
				Future<PrivateKey> result = ex.submit(new ClientRetrieverThread(in, r, bvk, password));
				resultKey = result.get();
				UserApplication.output = UserApplication.output + "\n Correctly retrieve values from the storage";
			} catch (IOException | InterruptedException | ExecutionException e) {
				UserApplication.output = UserApplication.output + "\n Something went wrong, unable to retrieve values from the storage";
				e.printStackTrace();
			} 
			break;

		case PRIVACY_OPTIMAL:
			try {
				InputStream key = new FileInputStream(new File("./PUBLICKEY.jks"));
				socket = SSLClientUtility.getSocketWithCert(InetAddress.getLocalHost(), 2009, key, "8rXbM7twa)E96xtFZmWq6/J^");
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
				OTReceiver obliviousReceiver = new OTReceiver(idFromStorage, (RSAPublicKey) obliviousTransferKey);
				BigInteger obliviousR = AsymmetricEncryption.generateRForBlindSignature(((RSAPublicKey) obliviousTransferKey).getModulus());
				BigInteger y = obliviousReceiver.generateY(obliviousR);
				ex.submit(new ClientSenderThread(out, protocol, y));
				Future<PrivateKey> result = ex.submit(new ClientRetrieverThread(in, obliviousReceiver, obliviousR, password));
				resultKey = result.get();		
				UserApplication.output = UserApplication.output + "\n Correctly retrieve values from the storage";
			} catch (IOException | InterruptedException | ExecutionException e) {
				UserApplication.output = UserApplication.output + "\n Something went wrong, unable to retrieve values from the storage";
				e.printStackTrace();
			}  
			break;
		case MOBILE:
			break;
		default:
			break;
		}

		return resultKey;
	}

	private byte[] generateCTextWithOneTimePadding(BigInteger password, PrivateKey ssk) {
		byte[] sskAsByte = ssk.getEncoded();
		byte[] oneTimePadKey = MyKeyGenerator.generateOneTimePaddingKeyFromPassword(password);
		return SymmetricEncryption.encryptOneTimePadding(sskAsByte, oneTimePadKey);
	}
	private byte[] generateCTextWithAES(BigInteger password, PrivateKey ssk) {
		byte[] sskAsByte = ssk.getEncoded();
		SecretKey aesKey = MyKeyGenerator.generateAESKeyFromPassword(password);
		return SymmetricEncryption.encryptAES(sskAsByte, aesKey);
	}
}