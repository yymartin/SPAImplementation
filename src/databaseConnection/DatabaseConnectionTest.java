package databaseConnection;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class DatabaseConnectionTest {
	@Test
	public void test() {
		DatabaseConnector db = new DatabaseConnector(DatabaseMode.STORAGE_OPTIMAL);
		BigInteger id;
		BigInteger ctext;
		SecureRandom rand = new SecureRandom();
		for(int i = 0; i < 500; i++) {
			id = new BigInteger(100, rand);
			ctext = new BigInteger(100, rand);
			db.insertElementIntoStorage(new byte[][] {id.toByteArray(), ctext.toByteArray()});
		}
		db.closeConnection();
//		BigInteger id = BigInteger.valueOf(123456);
//		BigInteger ctextStorageOptimal = BigInteger.valueOf(987654);		
//		db.searchElementFromStorage(id.toByteArray());
//		BigInteger ctextFromDBStorageOptimal = new BigInteger(db.getCTextFromStorage());
//		assertEquals(ctextStorageOptimal, ctextFromDBStorageOptimal);
//		db.closeConnection();
//
//
//		db = new DatabaseConnector(DatabaseMode.SERVER_OPTIMAL);
//		BigInteger bskStorage = BigInteger.valueOf(73194713); 
//		BigInteger ctextServerOptimal = BigInteger.valueOf(47893073);
//		db.searchElementFromStorage(id.toByteArray());
//		BigInteger bskStorageFromDB = new BigInteger(db.getBSKFromStorage());
//		BigInteger ctextServerFromDB = new BigInteger(db.getCTextFromStorage());
//		assertEquals(bskStorage, bskStorageFromDB);
//		assertEquals(ctextServerOptimal, ctextServerFromDB);
//		db.closeConnection();
//
//		db = new DatabaseConnector(DatabaseMode.STORAGE_OPTIMAL);
//		Map<BigInteger, BigInteger> fromDB = new HashMap<>();
//		try {
//			fromDB = db.getRandomElementFromStorage();
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		assertEquals(fromDB.size(), 11);
//		db.closeConnection();
	}
}
