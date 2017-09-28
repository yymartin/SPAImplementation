package databaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnector {

	Connection connection = null;

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

	public void insertElement(String... values){ 
		String query = "INSERT INTO EXAMPLE VALUES (" + values[0] + ", '" + values[1]+"')";
		executeQuery(query);
		System.out.println("Element inserted");
	}

	public String searchElement(int elem){
		String query = "SELECT * FROM EXAMPLE WHERE ID = "+ elem +";";
		return executeQueryAndPrintResult(query);
	}
	
	public void deleteElement(int elem) {
		String query = "DELETE FROM EXAMPLE WHERE ID = "+ elem +";";
		executeQuery(query);
		System.out.println("Element deleted");
	}
	
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