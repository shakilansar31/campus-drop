package model;

/**
 * Item.java
 * Model class representing a Lost or Found item in CampusDrop.
 */
public class Item {
    private int id;
    private String name;
    private String category;
    private String location;
    private String date;
    private String reportedBy;
    private String type;   // "LOST" or "FOUND"
    private String status; // "ACTIVE", "CLAIMED", "RETURNED"

    // Constructor
    public Item(int id, String name, String category, String location, String date, String reportedBy, String type, String status) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.location = location;
        this.date = date;
        this.reportedBy = reportedBy;
        this.type = type;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void display() {
        System.out.println("--------------------------------------------------");
        System.out.println(" Item ID      : " + id);
        System.out.println(" Name         : " + name);
        System.out.println(" Category     : " + category);
        System.out.println(" Location     : " + location);
        System.out.println(" Date         : " + date);
        System.out.println(" Reported By  : " + reportedBy);
        System.out.println(" Item Type    : " + type);
        System.out.println(" Status       : " + status);
        System.out.println("--------------------------------------------------");
    }

    @Override
    public String toString() {
        return String.format("| %-5d | %-20s | %-12s | %-12s | %-10s | %-8s | %-8s |",
                id, name, category, location, date, type, status);
    }
}
