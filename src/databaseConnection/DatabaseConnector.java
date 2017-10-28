package databaseConnection;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yoanmartin
 * Class representing an interface between a user and a database. It permits to connect to the database 
 * and to execute SQL queries
 */
public class DatabaseConnector {
	private Connection connection = null;
	private DatabaseMode mode;
	private String table = null;
	private byte[][] result = new byte[2][];

	/**
	 * Public constructor. It immediately connects to the database
	 * @param mode The corresponding protocol option choosed to access the database
	 */
	public DatabaseConnector(DatabaseMode mode) {
		this.mode = mode;

		// Settings
		String connectionUrl = "jdbc:sqlserver://spaimplementation.database.windows.net:1433;database=storage;encrypt=true;trustServerCertificate=true;";
		String user = "yymartin@spaimplementation";
		String pass = "VzfPU$87FWZh2gMiN2.s7T;W";
		switch(mode) {
		case STORAGE_OPTIMAL : 
			table = "STORAGE_STORAGE_OPTIMAL";
			break;
		case SERVER_OPTIMAL : 
			table = "STORAGE_SERVER_OPTIMAL";
			break;
		}

		// Declare the JDBC object.
		connection = null;

		try {
			// Establish the connection.
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			connection = DriverManager.getConnection(connectionUrl, user, pass);  
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Function used to insert one element in the database
	 * @param values The corresponding values to be inserted into a single row
	 */
	public void insertElementIntoStorage(byte[]... values){ 
		PreparedStatement stmt;
		try {
			switch(mode) {
			case STORAGE_OPTIMAL :
				stmt = connection.prepareStatement("INSERT INTO " + table + "  VALUES (?, ?);");
				stmt.setBytes(1, values[0]);
				stmt.setBytes(2, values[1]);
				stmt.executeUpdate();
				break;
			case SERVER_OPTIMAL :
				stmt = connection.prepareStatement("INSERT INTO " + table + "  VALUES (?, ?, ?);");
				stmt.setBytes(1, values[0]);
				stmt.setBytes(2, values[1]);
				stmt.setBytes(3, values[2]);
				stmt.executeUpdate();
				break;
			default:
				throw new IllegalStateException("Function called in the wrong database mode!");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Element inserted");
	}


	/**
	 * Function used to search an element into the storage database
	 * @param id The id of the searched row
	 */
	public void searchElementFromStorage(byte[] id){
		byte[][] value = new byte[2][];
		PreparedStatement stmt = null;
		try {
			switch(mode) {
			case STORAGE_OPTIMAL : case SERVER_OPTIMAL: 
				stmt = connection.prepareStatement("SELECT * FROM " + table + " WHERE ID = ?;");
				stmt.setBytes(1, id);
				break;
			}
			ResultSet result = stmt.executeQuery();

			switch(mode) {
			case STORAGE_OPTIMAL :
				while (result.next()) {
					value[0] = result.getBytes(2);
				}
				break;
			case SERVER_OPTIMAL:
				while (result.next()) {
					value[0] = result.getBytes(2);
					value[1] = result.getBytes(3);
				}
				break;
			}		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = value;
	}

	/**
	 * Function which return a Map containing all the database rows in a random order
	 * @return A map of the elements
	 * @throws IllegalAccessException Throw Exception if this function is used in SERVER_OPTIMAL mode
	 */
	public Map<byte[], byte[]> getRandomElementFromStorage() throws IllegalAccessException {
		Map<byte[], byte[]> randomValues = new HashMap<>();
		PreparedStatement stmt = null;
		try {
			switch(mode) {
			case STORAGE_OPTIMAL :
				stmt = connection.prepareStatement("SELECT * FROM STORAGE_STORAGE_OPTIMAL ORDER BY NEWID();");
				ResultSet result = stmt.executeQuery();
				if (!result.next()) {                       
					System.out.println("No records found");
				} else {
					do {
						byte[] key = result.getBytes(1);
						byte[] value = result.getBytes(2);
						randomValues.put(key, value);
					} while (result.next());
				}
				break;
			case SERVER_OPTIMAL :
				throw new IllegalAccessException();
			}		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return randomValues;
	}

	/**
	 * Function which return the ctext value in the case of a storage connection. This function must be called
	 * only after calling searchElementFromStorage(byte[] id)
	 * @return the ctext value
	 */
	public byte[] getCTextFromStorage() {
		switch(mode) {
		case STORAGE_OPTIMAL :
			return result[0];
		case SERVER_OPTIMAL :
			return result[1];
		default:
			throw new IllegalStateException("Function called in the wrong database mode!");
		}
	}

	/**
	 * Function which  return the BSK value in the case of a storage connection. This function must be called
	 * only after calling searchElementFromStorage(byte[] id)
	 * @return the BSK value
	 */
	public byte[] getBSKFromStorage() {
		switch(mode) {
		case SERVER_OPTIMAL :
			return result[0];
		default:
			throw new IllegalStateException("Function called in the wrong database mode!");
		}
	}


	/**
	 * Function used to delete an element from the storage database
	 * @param elem The id corresponding to the row to be deleted
	 */
	public void deleteElementFromStorage(BigInteger elem) {
		PreparedStatement stmt;
		switch(mode) {
		case STORAGE_OPTIMAL : case SERVER_OPTIMAL :
			try {
				stmt = connection.prepareStatement("DELETE FROM " + table + " WHERE ID = ?;");
				stmt.setBytes(1, elem.toByteArray());
				stmt.execute();
				System.out.println("Element deleted");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
			throw new IllegalStateException("Function called in the wrong database mode!");
		}
	}


	/**
	 * Close the connection with the database
	 */
	public void closeConnection(){
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}