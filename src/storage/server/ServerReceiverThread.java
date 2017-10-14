package storage.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerReceiverThread extends Thread implements Runnable{
	private DataInputStream in;
	private DataOutputStream out;

	public ServerReceiverThread(DataInputStream in, DataOutputStream out){
		this.in = in;
		this.out = out;
	}

	public void run() {
			try {
				int length = in.readInt();
				if(length>0) {
					byte[] id = new byte[length];
					in.readFully(id, 0, id.length); 
					Thread t = new Thread(new ServerSenderThread(out, id));
					t.start();
					Thread.currentThread().interrupt();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
