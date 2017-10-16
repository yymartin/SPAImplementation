package databaseConnection;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
		case STORAGE_STORAGE_OPTIMAL : 
			table = "STORAGE_STORAGE_OPTIMAL";
			break;
		case STORAGE_SERVER_OPTIMAL : 
			table = "STORAGE_SERVER_OPTIMAL";
			break;
		case SERVER_STORAGE_OPTIMAL :
			table = "SERVER_STORAGE_OPTIMAL";
			break;
		case SERVER_SERVER_OPTIMAL :
			table = "SERVER_SERVER_OPTIMAL"; 
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
			case STORAGE_STORAGE_OPTIMAL :
				stmt = connection.prepareStatement("INSERT INTO " + table + "  VALUES (?, ?);");
				stmt.setBytes(1, values[0]);
				stmt.setBytes(2, values[1]);
				stmt.executeUpdate();
				break;
			case STORAGE_SERVER_OPTIMAL :
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
	 * Function used to insert one element in the database
	 * @param username The username to be inserted
	 * @param values The corresponding values to be inserted
	 */
	public void insertElementIntoServer(String username, byte[]... values){ 
		PreparedStatement stmt;
		try {
			switch(mode) {
			case SERVER_STORAGE_OPTIMAL :
				stmt = connection.prepareStatement("INSERT INTO " + table + "  VALUES (?, ?, ?);");
				stmt.setString(1, username);
				stmt.setBytes(2, values[0]);
				stmt.setBytes(3, values[1]);
				stmt.executeUpdate();
				break;
			case SERVER_SERVER_OPTIMAL :
				stmt = connection.prepareStatement("INSERT INTO " + table + "  VALUES (?, ?);");
				stmt.setString(1, username);
				stmt.setBytes(2, values[0]);
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
			case STORAGE_STORAGE_OPTIMAL : case STORAGE_SERVER_OPTIMAL: 
				stmt = connection.prepareStatement("SELECT * FROM " + table + " WHERE ID = ?;");
				stmt.setBytes(1, id);
				break;
			case SERVER_SERVER_OPTIMAL : case SERVER_STORAGE_OPTIMAL:
				throw new IllegalStateException("Function called in the wrong database mode!");
			}
			ResultSet result = stmt.executeQuery();

			switch(mode) {
			case STORAGE_STORAGE_OPTIMAL :
				while (result.next()) {
					value[0] = result.getBytes(2);
				}
				break;
			case STORAGE_SERVER_OPTIMAL:
				while (result.next()) {
					value[0] = result.getBytes(2);
					value[1] = result.getBytes(3);
				}
				break;
			case SERVER_STORAGE_OPTIMAL : case SERVER_SERVER_OPTIMAL :
				throw new IllegalStateException("Function called in the wrong database mode!");
			}		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = value;
	}

	/**
	 * Function which  return the ctext value in the case of a storage connection. This function must be called
	 * only after calling searchElementFromStorage(byte[] id)
	 * @return the ctext value
	 */
	public byte[] getCTextFromStorage() {
		switch(mode) {
		case STORAGE_STORAGE_OPTIMAL :
			return result[0];
		case STORAGE_SERVER_OPTIMAL :
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
		case STORAGE_SERVER_OPTIMAL :
			return result[0];
		default:
			throw new IllegalStateException("Function called in the wrong database mode!");
		}
	}


	/**
	 * Function used to search an element into the server database
	 * @param username The username of the searched row
	 */
	public void searchElementFromServer(String username){
		byte[][] value = new byte[2][];
		PreparedStatement stmt = null;
		try {
			switch(mode) {
			case SERVER_STORAGE_OPTIMAL : case SERVER_SERVER_OPTIMAL: 
				stmt = connection.prepareStatement("SELECT * FROM " + table + " WHERE USERNAME = ?;");
				stmt.setString(1, username);
				break;
			case STORAGE_SERVER_OPTIMAL : case STORAGE_STORAGE_OPTIMAL:
				throw new IllegalStateException("Function called in the wrong database mode!");
			}
			ResultSet result = stmt.executeQuery();

			switch(mode) {
			case SERVER_SERVER_OPTIMAL :
				while (result.next()) {
					value[0] = result.getBytes(2);
				}
				break;
			case SERVER_STORAGE_OPTIMAL:
				while (result.next()) {
					value[0] = result.getBytes(2);
					value[1] = result.getBytes(3);
				}
				break;
			case STORAGE_STORAGE_OPTIMAL : case STORAGE_SERVER_OPTIMAL :
				throw new IllegalStateException("Function called in the wrong database mode!");
			}	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = value;
	}

	/**
	 * Function which  return the SVK value in the case of a server connection. This function must be called
	 * only after calling searchElementFromServer(String username)
	 * @return the SVK value
	 */
	public byte[] getSVKFromServer() {
		switch(mode) {
		case SERVER_SERVER_OPTIMAL :
			return result[0];
		case SERVER_STORAGE_OPTIMAL :
			return result[0];
		default:
			throw new IllegalStateException("Function called in the wrong database mode!");
		}
	}

	/**
	 * Function which  return the BSK value in the case of a server connection. This function must be called
	 * only after calling searchElementFromServer(String username)
	 * @return the BSK value
	 */
	public byte[] getBSKFromServer() {
		switch(mode) {
		case SERVER_STORAGE_OPTIMAL :
			return result[1];
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
		case STORAGE_STORAGE_OPTIMAL : case STORAGE_SERVER_OPTIMAL :
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
	 * Function used to delete an element from the storage database
	 * @param username The username corresponding to the row to be deleted
	 */
	public void deleteElementFromServer(String username) {
		PreparedStatement stmt;
		switch(mode) {
		case SERVER_STORAGE_OPTIMAL : case SERVER_SERVER_OPTIMAL :
			try {
				stmt = connection.prepareStatement("DELETE FROM " + table + " WHERE ID = ?;");
				stmt.setString(1, username);
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