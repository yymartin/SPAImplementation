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

import javax.net.ssl.SSLServerSocket;

import SSLUtility.SSLServerUtility;
import cryptographyBasics.Hash;
import cryptographyBasics.MyKeyGenerator;
import cryptographyBasics.SymmetricEncryption;

public class MobileClient {

	public static SSLServerSocket ss = null;
	public static Socket socket;
	public static DataInputStream in;
	public static DataOutputStream out;

	public static void main(String[] args) {
		byte[] K = MyKeyGenerator.getOneTimePaddingKeyFromFile(System.getProperty("user.dir"));
		String password = "Martin";
		BigInteger hashPassword = Hash.generateSHA256Hash(password.getBytes());
		
		byte[] ctext = SymmetricEncryption.encryptOneTimePadding(hashPassword.toByteArray(), K);
		
		try {
			System.out.println("Server created");
			InputStream key = new FileInputStream(new File("./PRIVATEKEYMOBILE.jks"));
			ss = SSLServerUtility.getServerSocketWithCert(1234, key, "8rXbM7twa)E96xtFZmWq6/J^");
			ArrayList<SocketAddress> clientsConnected = new ArrayList<>();
			System.out.println(InetAddress.getLocalHost());

			while(true) {
				socket = ss.accept();
				SocketAddress clientAddress = socket.getRemoteSocketAddress();
				System.out.println(clientAddress);
				clientsConnected.add(clientAddress);
				System.out.println("Client accepted");
				out = new DataOutputStream(socket.getOutputStream());		
				out.writeInt(ctext.length);
				out.write(ctext);
				out.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
