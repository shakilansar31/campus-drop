package database;

import model.ClaimRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * ClaimDAO.java
 * Data Access Object for CLAIMS table in Oracle SQL Database.
 * Secures SQL parameters using PreparedStatement.
 */
public class ClaimDAO {

    public boolean addClaim(ClaimRequest claim) {
        String sql = "INSERT INTO CLAIMS (CLAIM_ID, ITEM_ID, STUDENT_NAME, STUDENT_ID, CLAIM_REASON, CLAIM_STATUS) " +
                     "VALUES (CLAIM_SEQ.NEXTVAL, ?, ?, ?, ?, 'PENDING')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, claim.getItemId());
            ps.setString(2, claim.getStudentName());
            ps.setString(3, claim.getStudentId());
            ps.setString(4, claim.getClaimReason());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ClaimDAO AddClaim Warning: " + e.getMessage());
            return false;
        }
    }

    public List<ClaimRequest> getPendingClaims() {
        List<ClaimRequest> list = new ArrayList<>();
        String sql = "SELECT ITEM_ID, STUDENT_NAME, STUDENT_ID, CLAIM_REASON FROM CLAIMS WHERE CLAIM_STATUS = 'PENDING' ORDER BY CLAIM_ID ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new ClaimRequest(
                    rs.getInt("ITEM_ID"),
                    rs.getString("STUDENT_NAME"),
                    rs.getString("STUDENT_ID"),
                    rs.getString("CLAIM_REASON")
                ));
            }
        } catch (SQLException e) {
            System.err.println("ClaimDAO GetPendingClaims Warning: " + e.getMessage());
        }
        return list;
    }

    public ClaimRequest getNextPendingClaim() {
        List<ClaimRequest> list = getPendingClaims();
        return list.isEmpty() ? null : list.get(0);
    }

    public boolean updateClaimStatus(int itemId, String studentId, String status) {
        String sql = "UPDATE CLAIMS SET CLAIM_STATUS = ? WHERE ITEM_ID = ? AND STUDENT_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, itemId);
            ps.setString(3, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("ClaimDAO UpdateClaimStatus Warning: " + e.getMessage());
            return false;
        }
    }

    public List<ClaimRequest> getClaimsByItem(int itemId) {
        List<ClaimRequest> list = new ArrayList<>();
        String sql = "SELECT ITEM_ID, STUDENT_NAME, STUDENT_ID, CLAIM_REASON FROM CLAIMS WHERE ITEM_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ClaimRequest(
                        rs.getInt("ITEM_ID"),
                        rs.getString("STUDENT_NAME"),
                        rs.getString("STUDENT_ID"),
                        rs.getString("CLAIM_REASON")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("ClaimDAO GetClaimsByItem Warning: " + e.getMessage());
        }
        return list;
    }
}
