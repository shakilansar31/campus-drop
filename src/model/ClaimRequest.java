package model;

/**
 * ClaimRequest.java
 * Model class representing a claim request submitted for a found item.
 */
public class ClaimRequest {
    private int itemId;
    private String studentName;
    private String studentId;
    private String claimReason;

    // Constructor
    public ClaimRequest(int itemId, String studentName, String studentId, String claimReason) {
        this.itemId = itemId;
        this.studentName = studentName;
        this.studentId = studentId;
        this.claimReason = claimReason;
    }

    // Getters
    public int getItemId() {
        return itemId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getClaimReason() {
        return claimReason;
    }

    public void display() {
        System.out.println("  Student Name : " + studentName);
        System.out.println("  Student ID   : " + studentId);
        System.out.println("  Item ID      : " + itemId);
        System.out.println("  Claim Reason : " + claimReason);
    }

    @Override
    public String toString() {
        return "ClaimRequest [itemId=" + itemId + ", studentName=" + studentName + 
               ", studentId=" + studentId + ", claimReason=" + claimReason + "]";
    }
}
