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

import SSLUtility.ProtocolMode;
import SSLUtility.SSLClientUtility;
import cryptographyBasics.AsymmetricEncryption;
import cryptographyBasics.Hash;
import storage.ClientToStorageMode;

public class StorageClient {

	public static Socket socket = null;
	public static Thread t1, t2;
	private static DataInputStream in;
	private static DataOutputStream out;

	private ProtocolMode protocol;
	private BigInteger id;
	private BigInteger password;
	private PublicKey bvk;
	private PrivateKey bsk, ssk;

	public StorageClient(ProtocolMode protocol, String username, String password, String website, PublicKey bvk, PrivateKey bsk, PrivateKey ssk) {
		this.protocol = protocol;
		this.id = Hash.generateSHA256Hash((username+website).getBytes());
		this.password = Hash.generateSHA256Hash(password.getBytes());
		this.bvk = bvk;
		this.bsk = bsk;
		this.ssk = ssk;
	}

	public void storeValuesToStorage() {
		RSAPrivateKey privatekey = (RSAPrivateKey) bsk;
		BigInteger r = AsymmetricEncryption.generateRForBlindSignature(privatekey.getModulus());
		BigInteger passwordBlinded = AsymmetricEncryption.blind(password, r, (RSAPublicKey) bvk);

		byte[] ctext;
		switch(protocol) {
		case SERVER_OPTIMAL:
			BigInteger sig = AsymmetricEncryption.sign(passwordBlinded, (RSAPrivateKey) bsk);

			ctext = generateCText(sig, ssk);
			try {
				InputStream key = new FileInputStream(new File("./PUBLICKEY.jks"));
				System.out.println("Ask for connection");
				socket = SSLClientUtility.getSocketWithCert(InetAddress.getLocalHost(), 2009, key, "8rXbM7twa)E96xtFZmWq6/J^");
				System.out.println("Connection established"); 
				out = new DataOutputStream(socket.getOutputStream());

				ExecutorService ex = Executors.newFixedThreadPool(2);
				ex.execute(new ClientSenderThread(out, ProtocolMode.SERVER_OPTIMAL, ClientToStorageMode.STORE, id, bsk, ctext));
			} catch (UnknownHostException e) {

			} catch (IOException e) {

			}
			break;
		case STORAGE_OPTIMAL:
			id = AsymmetricEncryption.sign(passwordBlinded, (RSAPrivateKey) bsk);
			ctext = generateCText(password, ssk);
			
			try {
				InputStream key = new FileInputStream(new File("./PUBLICKEY.jks"));
				System.out.println("Ask for connection");
				socket = SSLClientUtility.getSocketWithCert(InetAddress.getLocalHost(), 2009, key, "8rXbM7twa)E96xtFZmWq6/J^");
				System.out.println("Connection established"); 
				out = new DataOutputStream(socket.getOutputStream());

				ExecutorService ex = Executors.newFixedThreadPool(2);
				ex.execute(new ClientSenderThread(out, ProtocolMode.STORAGE_OPTIMAL, ClientToStorageMode.STORE, id, ctext));
			} catch (UnknownHostException e) {

			} catch (IOException e) {

			}
			break;
		case PRIVACY_OPTIMAL:

			break;
		}	
	}

	public BigInteger[] retrieveValuesFromStorage() {
		BigInteger[] finalResult = new BigInteger[2];

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
				ex.execute(new ClientSenderThread(out, ProtocolMode.SERVER_OPTIMAL, ClientToStorageMode.RETRIEVE, id, password));

				Future<BigInteger[]> result = ex.submit(new ClientReceiverThread(in, ProtocolMode.SERVER_OPTIMAL));
				finalResult = result.get();

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
				ex.execute(new ClientSenderThread(out, ProtocolMode.STORAGE_OPTIMAL, ClientToStorageMode.RETRIEVE, id));

				Future<BigInteger[]> result = ex.submit(new ClientReceiverThread(in, ProtocolMode.SERVER_OPTIMAL));
				finalResult = result.get();

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

		return finalResult;
	}

	private byte[] generateCText(BigInteger sig, PrivateKey ssk) {
		// TODO Auto-generated method stub
		return null;
	}
}