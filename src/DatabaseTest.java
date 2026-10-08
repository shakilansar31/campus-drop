import database.DatabaseConnection;

/**
 * DatabaseTest.java
 * Simple diagnostic test class to verify Oracle SQL Database connection over JDBC.
 */
public class DatabaseTest {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("      CAMPUS DROP - ORACLE JDBC TEST HARNESS");
        System.out.println("==================================================");
        System.out.println("Testing connection to Local Oracle Database...");
        
        boolean success = DatabaseConnection.testConnection();

        if (success) {
            System.out.println("\nSUCCESS: Oracle Database Connected Successfully!");
            System.out.println("All DAO operations (ItemDAO, ClaimDAO, HistoryDAO) are ready.");
        } else {
            System.out.println("\nWARNING: Oracle Database Connection Failed.");
            System.out.println("Please check:");
            System.out.println(" 1. Is Oracle Database service running? (OracleServiceXE)");
            System.out.println(" 2. Is TNSListener running on port 1521?");
            System.out.println(" 3. Are credentials in DatabaseConnection.java correct?");
        }
        System.out.println("==================================================");
    }
}
