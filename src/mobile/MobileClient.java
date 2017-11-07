package mobile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLServerSocket;

import SSLUtility.SSLServerUtility;

public class MobileClient {
	
	public static SSLServerSocket ss = null;
	public static Socket socket;
	public static DataInputStream in;
	public static DataOutputStream out;
	
	private static Executor ex = Executors.newSingleThreadExecutor();
	private static ServerSocket server;
	
	public static void main(String[] args) {
	try {
		System.out.println("Server created");
		server = new ServerSocket(1234);
//		InputStream key = new FileInputStream(new File("./PRIVATEKEY.jks"));
//		ss = SSLServerUtility.getServerSocketWithCert(2009, key, "8rXbM7twa)E96xtFZmWq6/J^");
		ArrayList<SocketAddress> clientsConnected = new ArrayList<>();
		while(true) {
//			socket = ss.accept();
			System.out.println(InetAddress.getLocalHost());
			socket = server.accept();
			SocketAddress clientAddress = socket.getRemoteSocketAddress();
			System.out.println(clientAddress);
			clientsConnected.add(clientAddress);
			System.out.println("Client accepted");
			out = new DataOutputStream(socket.getOutputStream());		
			byte[] message = "Message".getBytes();
			out.writeInt(message.length);
			out.write(message);
			out.close();
			System.out.println("Message sent");
		}

	} catch (IOException e) {
		e.printStackTrace();
	}
	}
	
}
