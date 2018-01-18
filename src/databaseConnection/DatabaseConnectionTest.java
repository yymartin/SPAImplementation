package databaseConnection;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

public class DatabaseConnectionTest {
	@Test
	public void testDatabase() {		
		//Test storage optimal
		DatabaseConnector db = new DatabaseConnector(DatabaseMode.STORAGE_OPTIMAL);
		BigInteger id = BigInteger.valueOf(123456);
		BigInteger ctextStorageOptimal = BigInteger.valueOf(987654);	
		db.insertElementIntoStorage(new byte[][] {id.toByteArray(), ctextStorageOptimal.toByteArray()});
		db.searchElementFromStorage(id.toByteArray());
		BigInteger ctextFromDBStorageOptimal = new BigInteger(db.getCTextFromStorage());
		assertEquals(ctextStorageOptimal, ctextFromDBStorageOptimal);
		db.closeConnection();

		//Test server optimal
		db = new DatabaseConnector(DatabaseMode.SERVER_OPTIMAL);
		BigInteger bskStorage = BigInteger.valueOf(73194713); 
		BigInteger ctextServerOptimal = BigInteger.valueOf(47893073);
		db.insertElementIntoStorage(new byte[][] {id.toByteArray(), bskStorage.toByteArray(), ctextServerOptimal.toByteArray()});
		db.searchElementFromStorage(id.toByteArray());
		BigInteger bskStorageFromDB = new BigInteger(db.getBSKFromStorage());
		BigInteger ctextServerFromDB = new BigInteger(db.getCTextFromStorage());
		assertEquals(bskStorage, bskStorageFromDB);
		assertEquals(ctextServerOptimal, ctextServerFromDB);
		db.closeConnection();
	}
}
