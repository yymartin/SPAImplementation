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
import javax.crypto.spec.SecretKeySpec;

import SSLUtility.ProtocolMode;
import SSLUtility.SSLClientUtility;
import cryptographyBasics.AsymmetricEncryption;
import cryptographyBasics.Hash;
import cryptographyBasics.MyKeyGenerator;
import cryptographyBasics.SymmetricEncryption;
import storage.ClientToStorageMode;

/**
 * @author yoanmartin
 * Object which instantiate the client side of the connection with a storage
 */
public class StorageClient {

	public static Socket socket = null;
	public static Thread t1, t2;
	private static DataInputStream in;
	private static DataOutputStream out;

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
	 * @param bvk The bvk of the user
	 * @param bsk The bsk of the user
	 * @param ssk The ssk of the user
	 * @param r The blind factor of the user
	 */
	public StorageClient(String username, String password, String website, PublicKey bvk, PrivateKey bsk, PrivateKey ssk, BigInteger r) {
		this.protocol = SSLUtility.ProtocolMode.SERVER_OPTIMAL;
		this.id = Hash.generateSHA256Hash((username+website).getBytes());
		this.password = Hash.generateSHA256Hash(password.getBytes());
		this.r = r;
		this.bvk = bvk;
		this.bsk = bsk;
		this.ssk = ssk;
	}

	/**
	 * Constructor used when the Storage Optimal protocol is used
	 * @param id The id of the user
	 * @param password The password of the user
	 * @param website The website which the user wants to connect to
	 * @param bvk The bvk of the user
	 * @param bsk The bsk of the user
	 * @param ssk The ssk of the user
	 * @param r The blind factor of the user
	 */
	public StorageClient(ProtocolMode protocol, BigInteger id, String password, String website, PublicKey bvk, PrivateKey bsk, PrivateKey ssk, BigInteger r) {
		this.protocol = protocol;
		this.id = id;
		this.password = Hash.generateSHA256Hash(password.getBytes());
		this.r = r;
		this.bvk = bvk;
		this.bsk = bsk;
		this.ssk = ssk;
	}

	/**
	 * Function which stores the necessary values to the storage
	 */
	public void storeValuesToStorage() {
		byte[] ctext;
		switch(protocol) {
		case SERVER_OPTIMAL:
			BigInteger sig = AsymmetricEncryption.sign(password, (RSAPrivateKey) bsk);
			SecretKey aesKey = MyKeyGenerator.generateAESKeyFromPassword(sig);
			ctext = SymmetricEncryption.encryptAES(ssk.getEncoded(), aesKey);

			try {
				InputStream key = new FileInputStream(new File("./PUBLICKEY.jks"));
				System.out.println("Ask for connection");
				socket = SSLClientUtility.getSocketWithCert(InetAddress.getLocalHost(), 2009, key, "8rXbM7twa)E96xtFZmWq6/J^");
				System.out.println("Connection established"); 
				out = new DataOutputStream(socket.getOutputStream());

				ExecutorService ex = Executors.newFixedThreadPool(2);
				ex.execute(new ClientSenderThread(out, id, bsk, ctext));
			} catch (UnknownHostException e) {

			} catch (IOException e) {

			}
			break;
		case STORAGE_OPTIMAL:
			id = AsymmetricEncryption.sign(password, (RSAPrivateKey) bsk);
			ctext = generateCText(password, ssk);

			try {
				InputStream key = new FileInputStream(new File("./PUBLICKEY.jks"));
				System.out.println("Ask for connection");
				socket = SSLClientUtility.getSocketWithCert(InetAddress.getLocalHost(), 2009, key, "8rXbM7twa)E96xtFZmWq6/J^");
				System.out.println("Connection established"); 
				out = new DataOutputStream(socket.getOutputStream());

				ExecutorService ex = Executors.newFixedThreadPool(2);
				ex.execute(new ClientSenderThread(out, id, ctext));
			} catch (UnknownHostException e) {

			} catch (IOException e) {

			}
			break;
		case PRIVACY_OPTIMAL:

			break;
		}	
	}

	/**
	 * Function which retrieves the values from the storage and uses them to retrieve the ssk
	 * @return The ssk of the user
	 */
	public PrivateKey retrieveValuesFromStorage() {
		PrivateKey resultKey = null;

		switch(protocol) {
		case SERVER_OPTIMAL:
			try {
				InputStream key = new FileInputStream(new File("./PUBLICKEY.jks"));
				System.out.println("Ask for connection");
				socket = SSLClientUtility.getSocketWithCert(InetAddress.getLocalHost(), 2009, key, "8rXbM7twa)E96xtFZmWq6/J^");
				System.out.println("Connection established"); 
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
				ExecutorService ex = Executors.newFixedThreadPool(2);
				BigInteger passwordBlinded = AsymmetricEncryption.blind(password, r, (RSAPublicKey) bvk);
				ex.execute(new ClientSenderThread(out, id, passwordBlinded));
				Future<PrivateKey> result = ex.submit(new ClientReceiverThread(in, r, bvk));
				resultKey = result.get();

				ex.shutdown();
			} catch (UnknownHostException e) {

			} catch (IOException e) {

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		case STORAGE_OPTIMAL:
			try {
				InputStream key = new FileInputStream(new File("./PUBLICKEY.jks"));
				System.out.println("Ask for connection");
				socket = SSLClientUtility.getSocketWithCert(InetAddress.getLocalHost(), 2009, key, "8rXbM7twa)E96xtFZmWq6/J^");
				System.out.println("Connection established"); 
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());

				ExecutorService ex = Executors.newFixedThreadPool(2);
				ex.execute(new ClientSenderThread(out, id));

				Future<PrivateKey> result = ex.submit(new ClientReceiverThread(in, r, bvk, password));
				resultKey = result.get();

				ex.shutdown();
			} catch (UnknownHostException e) {

			} catch (IOException e) {

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		case PRIVACY_OPTIMAL:

			break;
		}

		return resultKey;
	}

	private byte[] generateCText(BigInteger password, PrivateKey ssk) {
		byte[] sskAsByte = ssk.getEncoded();
		SecretKey aesKey = MyKeyGenerator.generateAESKeyFromPassword(password);
		return SymmetricEncryption.encryptAES(sskAsByte, aesKey);
	}
}