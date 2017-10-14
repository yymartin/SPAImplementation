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

	Connection connection = null;

	/**
	 * Public constructor. It immediately connects to the database
	 */
	public DatabaseConnector() {

		// Settings
		String connectionUrl = "jdbc:sqlserver://spaimplementation.database.windows.net:1433;database=spaimplementation;encrypt=true;trustServerCertificate=true;";
		String user = "yymartin@spaimplementation";
		String pass = "VzfPU$87FWZh2gMiN2.s7T;W"; //Oh oh the password is readable here /!\

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
	public void insertElement(BigInteger id, BigInteger value){ 
		try {
			PreparedStatement stmt = connection.prepareStatement("INSERT INTO STORAGE VALUES (?, ?);");
			stmt.setBytes(1, id.toByteArray());
			stmt.setBytes(2, value.toByteArray());
			stmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Element inserted");
	}

	/**
	 * Function used to search an element into the database
	 * @param elem The id of the searched row
	 * @return The corresponding data received from the database
	 */
	public byte[] searchElement(byte[] elem){
		byte[] value = null;
		try {
			PreparedStatement stmt = connection.prepareStatement("SELECT * FROM STORAGE WHERE ID = ?;");
			stmt.setBytes(1, elem);
			ResultSet result = stmt.executeQuery();
			
			while (result.next()) {
				value = result.getBytes(2);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}
	
	/**
	 * Function used to delete an element from a database
	 * @param elem The id corresponding to the row to be deleted
	 */
	public void deleteElement(BigInteger elem) {
		PreparedStatement stmt;
		try {
			stmt = connection.prepareStatement("DELETE FROM STORAGE WHERE ID = ?;");
			stmt.setBytes(1, elem.toByteArray());
			stmt.execute();
			System.out.println("Element deleted");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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