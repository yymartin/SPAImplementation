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
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

import SSLUtility.ProtocolMode;
import SSLUtility.SSLClientUtility;
import cryptographyBasics.AsymmetricEncryption;
import cryptographyBasics.Hash;
import cryptographyBasics.MyKeyGenerator;
import cryptographyBasics.SymmetricEncryption;

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
	public StorageClient(String password, PrivateKey bsk, PublicKey bvk, PrivateKey ssk, BigInteger r) {
		this.protocol = SSLUtility.ProtocolMode.STORAGE_OPTIMAL;
		this.password = Hash.generateSHA256Hash(password.getBytes());
		this.r = r;
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

				ex.execute(new ClientSenderThread(out, id, bsk, ctext));
				try {
					ex.awaitTermination(1, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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

				ex.execute(new ClientSenderThread(out, id, ctext));
				try {
					ex.awaitTermination(1, TimeUnit.MINUTES);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
	public PrivateKey retrieveValuesFromStorage(BigInteger idFromStorage) {
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
				BigInteger passwordBlinded = AsymmetricEncryption.blind(password, r, (RSAPublicKey) bvk);
				ex.execute(new ClientSenderThread(out, id, passwordBlinded));
				Future<PrivateKey> result = ex.submit(new ClientReceiverThread(in, r, bvk));
				resultKey = result.get();
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
				ex.execute(new ClientSenderThread(out, idFromStorage));
				Future<PrivateKey> result = ex.submit(new ClientReceiverThread(in, r, bvk, password));
				resultKey = result.get();
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