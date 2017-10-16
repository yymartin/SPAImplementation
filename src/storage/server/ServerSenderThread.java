package storage.server;

import java.io.DataOutputStream;
import java.io.IOException;

import databaseConnection.DatabaseConnector;
import databaseConnection.DatabaseMode;

public class ServerSenderThread extends Thread implements Runnable {
	private DataOutputStream out;
	private byte[] id;

	public ServerSenderThread(DataOutputStream out, byte[] id) {
		this.out = out;
		this.id = id;
	}


	public void run() {
			DatabaseConnector db = new DatabaseConnector(DatabaseMode.STORAGE_STORAGE_OPTIMAL);
			db.searchElementFromStorage(id);
			byte[] result = db.getCTextFromStorage();
			try {
				out.writeInt(result.length);
				out.write(result);
				Thread.currentThread().interrupt();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			db.closeConnection();
	}
}
