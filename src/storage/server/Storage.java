package storage.server;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLServerSocket;

import SSLUtility.SSLServerUtility;

public class Storage {
	public static SSLServerSocket ss = null;
	public static Socket socket;
	public static DataInputStream in;
	public static DataOutputStream out;


	public static void main(String[] args) {
		try {
			System.out.println("Server created");
			InputStream key = new FileInputStream(new File("./PRIVATEKEY.jks"));
			ss = SSLServerUtility.getServerSocketWithCert(2009, key, "8rXbM7twa)E96xtFZmWq6/J^");
			ArrayList<SocketAddress> clientsConnected = new ArrayList<>();
			while(true) {
				socket = ss.accept();
				SocketAddress clientAddress = socket.getRemoteSocketAddress();
				clientsConnected.add(clientAddress);
				System.out.println("Client accepted");
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
				
				Executor ex = Executors.newScheduledThreadPool(20);
				ex.execute(new ClientAdministratorThread(in, out));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}