package databaseConnection;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

public class DatabaseConnectionTest {
	@Test
	public void test() {
		DatabaseConnector db = new DatabaseConnector(DatabaseMode.STORAGE_STORAGE_OPTIMAL);
		BigInteger id = BigInteger.valueOf(123456);
		BigInteger ctextStorageOptimal = BigInteger.valueOf(987654);		
		db.searchElementFromStorage(id.toByteArray());
		BigInteger ctextFromDBStorageOptimal = new BigInteger(db.getCTextFromStorage());
		assertEquals(ctextStorageOptimal, ctextFromDBStorageOptimal);
		db.closeConnection();
		
		
		db = new DatabaseConnector(DatabaseMode.STORAGE_SERVER_OPTIMAL);
		BigInteger bskStorage = BigInteger.valueOf(73194713); 
		BigInteger ctextServerOptimal = BigInteger.valueOf(47893073);
		db.searchElementFromStorage(id.toByteArray());
		BigInteger bskStorageFromDB = new BigInteger(db.getBSKFromStorage());
		BigInteger ctextServerFromDB = new BigInteger(db.getCTextFromStorage());
		assertEquals(bskStorage, bskStorageFromDB);
		assertEquals(ctextServerOptimal, ctextServerFromDB);
		db.closeConnection();
		
		String username = "Alice";
		BigInteger svkStorageOptimal = BigInteger.valueOf(949380934);
		BigInteger bskServerOptimal = BigInteger.valueOf(547893025);
		
		db = new DatabaseConnector(DatabaseMode.SERVER_STORAGE_OPTIMAL);
		db.searchElementFromServer(username);
		BigInteger svkStorageOptimalFromDB = new BigInteger(db.getSVKFromServer());
		BigInteger bskStorageOptimalFromDB = new BigInteger(db.getBSKFromServer());
		assertEquals(svkStorageOptimal, svkStorageOptimalFromDB);
		assertEquals(bskServerOptimal, bskStorageOptimalFromDB);
		db.closeConnection();
		
		BigInteger svkServerOptimal = BigInteger.valueOf(491830921);
		db = new DatabaseConnector(DatabaseMode.SERVER_SERVER_OPTIMAL);
		db.searchElementFromServer(username);
		BigInteger svkServerOptimalFromDB = new BigInteger(db.getSVKFromServer());
		assertEquals(svkServerOptimal, svkServerOptimalFromDB);
		db.closeConnection();
	}
}
