package storage.server;

import java.io.DataOutputStream;
import java.io.IOException;

import databaseConnection.DatabaseConnector;

public class ServerSenderThread extends Thread implements Runnable {
	private DataOutputStream out;
	private byte[] id;

	public ServerSenderThread(DataOutputStream out, byte[] id) {
		this.out = out;
		this.id = id;
	}


	public void run() {
			DatabaseConnector db = new DatabaseConnector();
			byte[] result = db.searchElement(id);
			db.closeConnection();
			try {
				out.writeInt(result.length);
				out.write(result);
				Thread.currentThread().interrupt();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	}
}
