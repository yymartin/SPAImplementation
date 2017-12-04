package mobile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import javax.net.ssl.SSLServerSocket;

import SSLUtility.SSLServerUtility;
import cryptographyBasics.Hash;
import cryptographyBasics.MyKeyGenerator;
import cryptographyBasics.SymmetricEncryption;

public class MobileClientRegistrationThread implements Callable<String>{

	public static SSLServerSocket ss = null;
	public static Socket socket;
	public static DataInputStream in;
	public static DataOutputStream out;

	public String call() {
		byte[] K = MyKeyGenerator.getHMacKeyFromFile(System.getProperty("user.dir"), "mobile").getEncoded();
		String password = "Martin";
		BigInteger hashPassword = Hash.generateSHA256Hash(password.getBytes());
		
		byte[] ctext = SymmetricEncryption.encryptOneTimePadding(K, hashPassword.toByteArray());
		
		try {
			System.out.println("Server created");
			InputStream key = new FileInputStream(new File("./PRIVATEKEYMOBILE.jks"));
			ss = SSLServerUtility.getServerSocketWithCert(1234, key, "8rXbM7twa)E96xtFZmWq6/J^");
			key.close();
			ArrayList<SocketAddress> clientsConnected = new ArrayList<>();
			socket = ss.accept();
			ss.close();
			SocketAddress clientAddress = socket.getRemoteSocketAddress();
			System.out.println(clientAddress);
			clientsConnected.add(clientAddress);
			System.out.println("Client accepted");
			out = new DataOutputStream(socket.getOutputStream());		
			out.writeInt(ctext.length);
			out.write(ctext);
			out.close();
			socket.close();
			
			return InetAddress.getLocalHost().getHostAddress();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
