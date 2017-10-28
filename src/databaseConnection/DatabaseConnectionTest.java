package databaseConnection;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class DatabaseConnectionTest {
	@Test
	public void test() {
		DatabaseConnector db = new DatabaseConnector(DatabaseMode.STORAGE_OPTIMAL);
		BigInteger id = BigInteger.valueOf(123456);
		BigInteger ctextStorageOptimal = BigInteger.valueOf(987654);		
		db.searchElementFromStorage(id.toByteArray());
		BigInteger ctextFromDBStorageOptimal = new BigInteger(db.getCTextFromStorage());
		assertEquals(ctextStorageOptimal, ctextFromDBStorageOptimal);
		db.closeConnection();


		db = new DatabaseConnector(DatabaseMode.SERVER_OPTIMAL);
		BigInteger bskStorage = BigInteger.valueOf(73194713); 
		BigInteger ctextServerOptimal = BigInteger.valueOf(47893073);
		db.searchElementFromStorage(id.toByteArray());
		BigInteger bskStorageFromDB = new BigInteger(db.getBSKFromStorage());
		BigInteger ctextServerFromDB = new BigInteger(db.getCTextFromStorage());
		assertEquals(bskStorage, bskStorageFromDB);
		assertEquals(ctextServerOptimal, ctextServerFromDB);
		db.closeConnection();

		db = new DatabaseConnector(DatabaseMode.STORAGE_OPTIMAL);
		Map<byte[], byte[]> fromDB = new HashMap<>();
		try {
			fromDB = db.getRandomElementFromStorage();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(fromDB.size(), 11);
		db.closeConnection();
	}
}
