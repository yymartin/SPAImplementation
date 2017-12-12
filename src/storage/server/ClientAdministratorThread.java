package storage.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.Map;

import SSLUtility.ProtocolMode;
import cryptographyBasics.AsymmetricEncryption;
import cryptographyBasics.MyKeyGenerator;
import cryptographyBasics.OTSender;
import databaseConnection.DatabaseConnector;
import databaseConnection.DatabaseMode;
import storage.ClientToStorageMode;

/**
 * @author yoanmartin
 * Instantiation of a thread which administrates client connection to a storage and run the thread corresponding to the client state
 */
public class ClientAdministratorThread extends Thread implements Runnable{
	private DataInputStream in;
	private DataOutputStream out;
	private PublicKey storagePublicKey;
	private PrivateKey storagePrivateKey;

	public ClientAdministratorThread(DataInputStream in, DataOutputStream out, PublicKey storagePublicKey, PrivateKey storagePrivateKey){
		this.in = in;
		this.out = out;
		this.storagePublicKey = storagePublicKey;
		this.storagePrivateKey = storagePrivateKey;
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
			switch(mode) {
			case STORE : 
				id = getData();
				ctext = getData();
				byte[] publicKeyAsBytes = storagePublicKey.getEncoded();
				db = new DatabaseConnector(DatabaseMode.STORAGE_OPTIMAL);
				db.insertElementIntoStorage(new byte[][] {id, ctext});
				db.closeConnection();
				try {
					out.writeInt(publicKeyAsBytes.length);
					out.write(publicKeyAsBytes);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;

			case RETRIEVE :
				id = getData();
				db = new DatabaseConnector(DatabaseMode.STORAGE_OPTIMAL);
				
				Map<BigInteger, BigInteger> dbElements = null;
				try {
					dbElements = db.getRandomElementFromStorage();
				} catch (IllegalAccessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int numElem = dbElements.size();
				OTSender obliviousSender = new OTSender(dbElements, (RSAPrivateKey) storagePrivateKey);
				
				ArrayList<byte[]> e = obliviousSender.generateE();
				byte[] kPrime = obliviousSender.generateKprime(new BigInteger(id)).toByteArray();			
				
				try {
					out.writeInt(kPrime.length);
					out.write(kPrime);
					out.writeInt(numElem);
					for(byte[] elem : e) {
						out.writeInt(elem.length);
						out.write(elem);
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				break;
			}
			break;
		case MOBILE:
			break;
		default:
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
