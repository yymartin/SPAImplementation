package storage.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;

import SSLUtility.ProtocolMode;
import cryptographyBasics.AsymmetricEncryption;
import cryptographyBasics.MyKeyGenerator;
import databaseConnection.DatabaseConnector;
import databaseConnection.DatabaseMode;
import storage.ClientToStorageMode;

public class ClientAdministratorThread extends Thread implements Runnable{
	private DataInputStream in;
	private DataOutputStream out;

	public ClientAdministratorThread(DataInputStream in, DataOutputStream out){
		this.in = in;
		this.out = out;
	}

	public void run() {
		ProtocolMode protocol = ProtocolMode.valueOf(new String(getData()));
		ClientToStorageMode mode = ClientToStorageMode.valueOf(new String(getData()));
		DatabaseConnector db;

		switch(protocol) {
		case SERVER_OPTIMAL:
			byte[] id, bsk, ctext, hashPassword;

			switch(mode) {
			case STORE : 
				id = getData();
				bsk = getData();
				ctext = getData();	
				db = new DatabaseConnector(DatabaseMode.SERVER_OPTIMAL);
				db.insertElementIntoStorage(new byte[][] {id, bsk, ctext});

				break;

			case RETRIEVE :
				id = getData();
				hashPassword = getData();
				db = new DatabaseConnector(DatabaseMode.SERVER_OPTIMAL);
				db.searchElementFromStorage(id);
				ctext = db.getCTextFromStorage();
				bsk = db.getBSKFromStorage();

				byte[] sig = generateBlindSignature(bsk, hashPassword);
				try {
					out.writeInt(sig.length);
					out.write(sig);
					out.writeInt(ctext.length);
					out.write(ctext);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;
			}
			break;

		case STORAGE_OPTIMAL:
			switch(mode) {
			case STORE : 
				id = getData();
				ctext = getData();

				db = new DatabaseConnector(DatabaseMode.STORAGE_OPTIMAL);
				db.insertElementIntoStorage(new byte[][] {id, ctext});

				break;

			case RETRIEVE :
				id = getData();
				hashPassword = getData();
				db = new DatabaseConnector(DatabaseMode.STORAGE_OPTIMAL);
				db.searchElementFromStorage(id);
				ctext = db.getCTextFromStorage();
				try {
					out.writeInt(ctext.length);
					out.write(ctext);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			break;

		case PRIVACY_OPTIMAL:

			break;
		}

		Thread.currentThread().interrupt();
	}

	private byte[] getData() {
		byte[] id = null;
		try {
			int length = in.readInt();
			if(length > 0) {
				id = new byte[length];
				in.readFully(id, 0, id.length); 
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return id;
	}

	private byte[] generateBlindSignature(byte[] bsk, byte[] hashPassword) {
		RSAPrivateKey privateKey = MyKeyGenerator.convertByteArrayIntoPrivateKey(bsk);
		BigInteger signature = AsymmetricEncryption.sign(new BigInteger(hashPassword), privateKey);
		return signature.toByteArray();
	}
}
