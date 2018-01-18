package mobile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.Socket;

import javax.crypto.SecretKey;
import javax.net.ssl.SSLServerSocket;

import SSLUtility.SSLServerUtility;
import cryptographyBasics.Hash;
import cryptographyBasics.SymmetricEncryption;

public class MobileClientRegistrationThread extends Thread implements Runnable{

	public static SSLServerSocket ss = null;
	public static Socket socket;
	public static DataInputStream in;
	public static DataOutputStream out;
	
	private byte[] K;
	private BigInteger hashPassword;
	
	/**
	 * Constructor of the thread used to create the connection with the mobile application
	 * @param K The SecretKey K
	 * @param password The password used to encrypt K
	 */
	public MobileClientRegistrationThread(SecretKey K, String password) {
			this.K = K.getEncoded();
			this.hashPassword = Hash.generateSHA256Hash(password.getBytes());
	}

	public void run() {				
		try {
			System.out.println("Server created");
			InputStream key = new FileInputStream(new File("./PRIVATEKEYMOBILE.jks"));
			ss = SSLServerUtility.getServerSocketWithCert(1234, key, "8rXbM7twa)E96xtFZmWq6/J^");
			key.close();
			socket = ss.accept();
			ss.close();
			System.out.println("Client accepted");
			byte[] ctext = SymmetricEncryption.encryptOneTimePadding(K, hashPassword.toByteArray());
			out = new DataOutputStream(socket.getOutputStream());		
			out.writeInt(ctext.length);
			out.write(ctext);
			out.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}
}
