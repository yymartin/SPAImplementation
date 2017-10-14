package databaseConnection;

import java.math.BigInteger;
import java.security.SecureRandom;

public class DatabaseConnectionTest {
	public static void main(String[] args) {
		DatabaseConnector dbConnect = new DatabaseConnector();
		SecureRandom rand = new SecureRandom();
		BigInteger id = new BigInteger(10, rand);
		BigInteger value = new BigInteger(10, rand);
		
		dbConnect.insertElement(id, value);
		byte[] dbResult = dbConnect.searchElement(id.toByteArray());
		assert(new BigInteger(dbResult).equals(value));		
		dbConnect.deleteElement(id);
		
		dbConnect.closeConnection();
	}
}
