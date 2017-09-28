package databaseConnection;

public class DatabaseConnectionTest {
	public static void main(String[] args) {
		DatabaseConnector dbConnect = new DatabaseConnector();
		
		dbConnect.insertElement("10", "Tim");
		System.out.println(dbConnect.searchElement(10));
		dbConnect.deleteElement(10);

		dbConnect.closeConnection();
	}
}
