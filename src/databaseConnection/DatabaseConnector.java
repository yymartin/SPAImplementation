package databaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

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
	public void insertElement(String... values){ 
		String query = "INSERT INTO EXAMPLE VALUES (" + values[0] + ", '" + values[1]+"')";
		executeQuery(query);
		System.out.println("Element inserted");
	}

	/**
	 * Function used to search an element into the database
	 * @param elem The id of the searched row
	 * @return The corresponding data received from the database
	 */
	public String searchElement(int elem){
		String query = "SELECT * FROM EXAMPLE WHERE ID = "+ elem +";";
		return executeQueryAndPrintResult(query);
	}
	
	/**
	 * Function used to delete an element from a database
	 * @param elem The id corresponding to the row to be deleted
	 */
	public void deleteElement(int elem) {
		String query = "DELETE FROM EXAMPLE WHERE ID = "+ elem +";";
		executeQuery(query);
		System.out.println("Element deleted");
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
	
	private void executeQuery(String query) {
		try {
			Statement stmt = connection.createStatement();
			stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private String executeQueryAndPrintResult(String query){
		String resultText = ""; 
		try {
			Statement stmt = connection.createStatement();
			ResultSet result = stmt.executeQuery(query);
			ResultSetMetaData rsmd = result.getMetaData();
			int columnsNumber = rsmd.getColumnCount();

			while (result.next()) {
				for (int i = 1; i <= columnsNumber; i++) {
					if (i > 1) {
						resultText = resultText.concat(", ");
					}
					String columnValue = result.getString(i);
					resultText = resultText + " " + rsmd.getColumnName(i) + ": " + columnValue;
				}
				resultText = resultText + System.lineSeparator();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return resultText;
	}
}