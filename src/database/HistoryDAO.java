package database;

import model.Item;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * HistoryDAO.java
 * Data Access Object for RECOVERY_HISTORY table in Oracle SQL Database.
 * Stores audit logs for returned property using PreparedStatement.
 */
public class HistoryDAO {

    public boolean addRecoveryHistory(Item item, String studentName) {
        String sql = "INSERT INTO RECOVERY_HISTORY (HISTORY_ID, ITEM_ID, ITEM_NAME, STUDENT_NAME, RETURN_DATE, STATUS) " +
                     "VALUES (HISTORY_SEQ.NEXTVAL, ?, ?, ?, ?, 'RETURNED')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, item.getId());
            ps.setString(2, item.getName());
            ps.setString(3, studentName != null ? studentName : item.getReportedBy());
            ps.setString(4, item.getDate());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("HistoryDAO AddHistory Warning: " + e.getMessage());
            return false;
        }
    }

    public List<Item> getAllRecoveryHistory() {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT ITEM_ID, ITEM_NAME, 'Personal' AS CATEGORY, 'Campus' AS LOCATION, RETURN_DATE, STUDENT_NAME AS REPORTED_BY, 'FOUND' AS ITEM_TYPE, STATUS " +
                     "FROM RECOVERY_HISTORY ORDER BY HISTORY_ID DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Item(
                    rs.getInt("ITEM_ID"),
                    rs.getString("ITEM_NAME"),
                    rs.getString("CATEGORY"),
                    rs.getString("LOCATION"),
                    rs.getString("RETURN_DATE"),
                    rs.getString("REPORTED_BY"),
                    rs.getString("ITEM_TYPE"),
                    rs.getString("STATUS")
                ));
            }
        } catch (SQLException e) {
            System.err.println("HistoryDAO GetAllHistory Warning: " + e.getMessage());
        }
        return list;
    }

    public List<Item> getHistoryByItem(int itemId) {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT ITEM_ID, ITEM_NAME, 'Personal' AS CATEGORY, 'Campus' AS LOCATION, RETURN_DATE, STUDENT_NAME AS REPORTED_BY, 'FOUND' AS ITEM_TYPE, STATUS " +
                     "FROM RECOVERY_HISTORY WHERE ITEM_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Item(
                        rs.getInt("ITEM_ID"),
                        rs.getString("ITEM_NAME"),
                        rs.getString("CATEGORY"),
                        rs.getString("LOCATION"),
                        rs.getString("RETURN_DATE"),
                        rs.getString("REPORTED_BY"),
                        rs.getString("ITEM_TYPE"),
                        rs.getString("STATUS")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("HistoryDAO GetHistoryByItem Warning: " + e.getMessage());
        }
        return list;
    }
}
