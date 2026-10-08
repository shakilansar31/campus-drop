package database;

import model.Item;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * ItemDAO.java
 * Data Access Object for ITEMS table in Oracle SQL Database.
 * Uses PreparedStatement for secure, SQL-injection safe database operations.
 */
public class ItemDAO {

    public boolean addItem(Item item) {
        String sql = "INSERT INTO ITEMS (ITEM_ID, ITEM_NAME, CATEGORY, LOCATION, REPORT_DATE, REPORTED_BY, STUDENT_ID, ITEM_TYPE, STATUS) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, item.getId());
            ps.setString(2, item.getName());
            ps.setString(3, item.getCategory());
            ps.setString(4, item.getLocation());
            ps.setString(5, item.getDate());
            ps.setString(6, item.getReportedBy());
            ps.setString(7, item.getReportedBy());
            ps.setString(8, item.getType());
            ps.setString(9, item.getStatus());

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("ItemDAO Insert Warning: " + e.getMessage());
            return false;
        }
    }

    public Item getItemById(int id) {
        String sql = "SELECT ITEM_ID, ITEM_NAME, CATEGORY, LOCATION, REPORT_DATE, REPORTED_BY, ITEM_TYPE, STATUS FROM ITEMS WHERE ITEM_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToItem(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("ItemDAO GetById Warning: " + e.getMessage());
        }
        return null;
    }

    public List<Item> getAllItems() {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT ITEM_ID, ITEM_NAME, CATEGORY, LOCATION, REPORT_DATE, REPORTED_BY, ITEM_TYPE, STATUS FROM ITEMS ORDER BY ITEM_ID ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToItem(rs));
            }
        } catch (SQLException e) {
            System.err.println("ItemDAO GetAll Warning: " + e.getMessage());
        }
        return list;
    }

    public List<Item> searchByName(String name) {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT ITEM_ID, ITEM_NAME, CATEGORY, LOCATION, REPORT_DATE, REPORTED_BY, ITEM_TYPE, STATUS FROM ITEMS WHERE LOWER(ITEM_NAME) LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + name.toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToItem(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("ItemDAO SearchByName Warning: " + e.getMessage());
        }
        return list;
    }

    public List<Item> searchByCategory(String category) {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT ITEM_ID, ITEM_NAME, CATEGORY, LOCATION, REPORT_DATE, REPORTED_BY, ITEM_TYPE, STATUS FROM ITEMS WHERE LOWER(CATEGORY) LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + category.toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToItem(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("ItemDAO SearchByCategory Warning: " + e.getMessage());
        }
        return list;
    }

    public List<Item> searchByLocation(String location) {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT ITEM_ID, ITEM_NAME, CATEGORY, LOCATION, REPORT_DATE, REPORTED_BY, ITEM_TYPE, STATUS FROM ITEMS WHERE LOWER(LOCATION) LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + location.toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToItem(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("ItemDAO SearchByLocation Warning: " + e.getMessage());
        }
        return list;
    }

    public boolean updateItemStatus(int id, String status) {
        String sql = "UPDATE ITEMS SET STATUS = ? WHERE ITEM_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ItemDAO UpdateStatus Warning: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteItem(int id) {
        String sql = "DELETE FROM ITEMS WHERE ITEM_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ItemDAO Delete Warning: " + e.getMessage());
            return false;
        }
    }

    private Item mapRowToItem(ResultSet rs) throws SQLException {
        return new Item(
            rs.getInt("ITEM_ID"),
            rs.getString("ITEM_NAME"),
            rs.getString("CATEGORY"),
            rs.getString("LOCATION"),
            rs.getString("REPORT_DATE"),
            rs.getString("REPORTED_BY"),
            rs.getString("ITEM_TYPE"),
            rs.getString("STATUS")
        );
    }
}
