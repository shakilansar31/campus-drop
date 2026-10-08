import database.ClaimDAO;
import database.DatabaseConnection;
import database.HistoryDAO;
import database.ItemDAO;
import model.ClaimRequest;
import model.Item;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

/**
 * CampusDrop.java
 * Core business logic class integrating Java Collections with local Oracle SQL Database via JDBC DAOs:
 * 1. HashMap<Integer, Item> - Fast O(1) in-memory lookup.
 * 2. LinkedList<Item> - Sequential audit trail of returned items.
 * 3. Stack<String> - LIFO Undo mechanism.
 * 4. Queue<ClaimRequest> - FIFO Queue for processing student claims.
 * 5. Oracle SQL DAOs (ItemDAO, ClaimDAO, HistoryDAO) - Persistent database layer.
 */
public class CampusDrop {

    // Mandatory Java Data Structures
    private HashMap<Integer, Item> items;
    private LinkedList<Item> recoveryHistory;
    private Stack<String> actionStack;
    private Queue<ClaimRequest> claimQueue;

    // Database Access Objects (DAO Layer)
    private final ItemDAO itemDAO;
    private final ClaimDAO claimDAO;
    private final HistoryDAO historyDAO;

    private int nextItemId;
    private boolean isOracleAvailable;

    // Constructor
    public CampusDrop() {
        this.items = new HashMap<>();
        this.recoveryHistory = new LinkedList<>();
        this.actionStack = new Stack<>();
        this.claimQueue = new LinkedList<>();

        this.itemDAO = new ItemDAO();
        this.claimDAO = new ClaimDAO();
        this.historyDAO = new HistoryDAO();
        this.nextItemId = 101;

        // Verify local Oracle Database connection
        this.isOracleAvailable = DatabaseConnection.testConnection();

        // Synchronize records from Oracle DB into Java Data Structures
        syncFromDatabase();
    }

    /**
     * Loads existing records from Oracle Database into Java Collections.
     * Fallback to sample data if DB is empty or offline.
     */
    private void syncFromDatabase() {
        if (isOracleAvailable) {
            List<Item> dbItems = itemDAO.getAllItems();
            if (dbItems != null && !dbItems.isEmpty()) {
                for (Item item : dbItems) {
                    items.put(item.getId(), item);
                    if (item.getId() >= nextItemId) {
                        nextItemId = item.getId() + 1;
                    }
                }
            } else {
                preloadSampleData();
            }

            List<ClaimRequest> dbClaims = claimDAO.getPendingClaims();
            if (dbClaims != null) {
                for (ClaimRequest claim : dbClaims) {
                    claimQueue.offer(claim);
                }
            }

            List<Item> dbHistory = historyDAO.getAllRecoveryHistory();
            if (dbHistory != null) {
                for (Item h : dbHistory) {
                    recoveryHistory.add(h);
                }
            }
        } else {
            preloadSampleData();
        }
    }

    private void preloadSampleData() {
        if (items.isEmpty()) {
            insertSampleItem(new Item(101, "Black Wallet", "Personal", "Library", "25/09/2026", "Rahul Sharma", "LOST", "ACTIVE"));
            insertSampleItem(new Item(102, "Casio Calculator", "Electronics", "Block B", "24/09/2026", "Ananya Roy", "FOUND", "ACTIVE"));
            insertSampleItem(new Item(103, "Blue Water Bottle", "Personal", "Canteen", "25/09/2026", "Vikram Singh", "LOST", "ACTIVE"));
            insertSampleItem(new Item(104, "HP Laptop Charger", "Electronics", "Lab 2", "23/09/2026", "Neha Gupta", "FOUND", "ACTIVE"));
            insertSampleItem(new Item(105, "College ID Card", "Documents", "Main Block", "25/09/2026", "Amit Verma", "LOST", "ACTIVE"));
            nextItemId = 106;
        }
    }

    private void insertSampleItem(Item item) {
        items.put(item.getId(), item);
        if (isOracleAvailable) {
            itemDAO.addItem(item);
        }
    }

    // =========================================================================
    // FEATURE 1 & 2: REPORT LOST AND FOUND ITEMS (HASHMAP + ORACLE DB)
    // =========================================================================

    public int reportLostItem(String name, String category, String location, String date, String studentName) {
        int id = nextItemId++;
        Item lostItem = new Item(id, name, category, location, date, studentName, "LOST", "ACTIVE");

        // 1. Add to HashMap (Java Data Structure)
        items.put(id, lostItem);

        // 2. Persist in Oracle Database via ItemDAO
        if (isOracleAvailable) {
            itemDAO.addItem(lostItem);
        }

        // 3. Push operation to Stack (LIFO Undo)
        actionStack.push("Reported lost item #" + id + " (" + name + ")");
        return id;
    }

    public int reportFoundItem(String name, String category, String location, String date, String finderName) {
        int id = nextItemId++;
        Item foundItem = new Item(id, name, category, location, date, finderName, "FOUND", "ACTIVE");

        // 1. Add to HashMap (Java Data Structure)
        items.put(id, foundItem);

        // 2. Persist in Oracle Database via ItemDAO
        if (isOracleAvailable) {
            itemDAO.addItem(foundItem);
        }

        // 3. Push operation to Stack (LIFO Undo)
        actionStack.push("Reported found item #" + id + " (" + name + ")");
        return id;
    }

    // =========================================================================
    // FEATURE 3: SEARCH ITEM (HASHMAP FIRST -> ORACLE DB FALLBACK)
    // =========================================================================

    public Item searchItemById(int id) {
        // 1. Fast O(1) HashMap lookup
        Item item = items.get(id);
        if (item != null) {
            return item;
        }

        // 2. Oracle Database query fallback
        if (isOracleAvailable) {
            Item dbItem = itemDAO.getItemById(id);
            if (dbItem != null) {
                items.put(dbItem.getId(), dbItem); // Sync back to HashMap
                return dbItem;
            }
        }
        return null;
    }

    public void searchItemByKeyword(String keyword, String fieldType) {
        boolean foundAny = false;
        String searchKey = keyword.trim().toLowerCase();

        System.out.println("\n==================================================");
        System.out.println("                SEARCH RESULTS (" + fieldType.toUpperCase() + ")");
        System.out.println("==================================================");

        for (Item item : items.values()) {
            boolean match = false;
            if (fieldType.equalsIgnoreCase("NAME") && item.getName().toLowerCase().contains(searchKey)) match = true;
            else if (fieldType.equalsIgnoreCase("CATEGORY") && item.getCategory().toLowerCase().contains(searchKey)) match = true;
            else if (fieldType.equalsIgnoreCase("LOCATION") && item.getLocation().toLowerCase().contains(searchKey)) match = true;

            if (match) {
                item.display();
                foundAny = true;
            }
        }

        if (!foundAny) {
            System.out.println("No matching item found for keyword: \"" + keyword + "\"");
        }
    }

    // =========================================================================
    // FEATURE 4: VIEW ALL ITEMS
    // =========================================================================

    public void viewAllItems() {
        if (items.isEmpty()) {
            System.out.println("\nNo items reported in the system.");
            return;
        }

        System.out.println("\n============================================================================================");
        System.out.println("                                 ALL REPORTED ITEMS (HASHMAP + ORACLE)");
        System.out.println("============================================================================================");
        System.out.println(String.format("| %-5s | %-20s | %-12s | %-12s | %-10s | %-8s | %-8s |",
                "ID", "Item Name", "Category", "Location", "Date", "Type", "Status"));
        System.out.println("--------------------------------------------------------------------------------------------");

        for (Item item : items.values()) {
            System.out.println(item.toString());
        }
        System.out.println("============================================================================================");
    }

    // =========================================================================
    // FEATURE 5: FIND POSSIBLE MATCHES
    // =========================================================================

    public void findPossibleMatches() {
        System.out.println("\n==================================================");
        System.out.println("             POSSIBLE MATCHES DETECTED");
        System.out.println("==================================================");

        boolean matchFound = false;

        for (Item lostItem : items.values()) {
            if (!lostItem.getType().equals("LOST") || !lostItem.getStatus().equals("ACTIVE")) continue;

            for (Item foundItem : items.values()) {
                if (!foundItem.getType().equals("FOUND") || !foundItem.getStatus().equals("ACTIVE")) continue;

                boolean nameMatch = lostItem.getName().equalsIgnoreCase(foundItem.getName())
                        || lostItem.getName().toLowerCase().contains(foundItem.getName().toLowerCase())
                        || foundItem.getName().toLowerCase().contains(lostItem.getName().toLowerCase());

                boolean categoryMatch = lostItem.getCategory().equalsIgnoreCase(foundItem.getCategory());
                boolean locationMatch = lostItem.getLocation().equalsIgnoreCase(foundItem.getLocation());

                if (nameMatch && (categoryMatch || locationMatch)) {
                    matchFound = true;
                    System.out.println("\n[ MATCH FOUND ]");
                    System.out.println("  LOST ITEM  : #" + lostItem.getId() + " - " + lostItem.getName() + " | Location: " + lostItem.getLocation());
                    System.out.println("  FOUND ITEM : #" + foundItem.getId() + " - " + foundItem.getName() + " | Location: " + foundItem.getLocation());
                    System.out.println("--------------------------------------------------");
                }
            }
        }

        if (!matchFound) {
            System.out.println("No possible matches found at this time.");
        }
    }

    // =========================================================================
    // FEATURE 6: SUBMIT CLAIM (QUEUE OFFER + ORACLE CLAIMS)
    // =========================================================================

    public boolean submitClaim(int foundItemId, String studentName, String studentId, String claimReason) {
        Item item = searchItemById(foundItemId);

        if (item == null || !item.getType().equals("FOUND") || !item.getStatus().equals("ACTIVE")) {
            System.out.println("\nError: Item ID #" + foundItemId + " cannot be claimed.");
            return false;
        }

        ClaimRequest request = new ClaimRequest(foundItemId, studentName, studentId, claimReason);

        // 1. Offer to Queue (FIFO)
        claimQueue.offer(request);

        // 2. Persist in Oracle CLAIMS table via ClaimDAO
        if (isOracleAvailable) {
            claimDAO.addClaim(request);
        }

        // 3. Push to Stack (Undo)
        actionStack.push("Submitted claim for item #" + foundItemId + " by " + studentName);
        System.out.println("\nClaim submitted successfully! Enqueued in FIFO Queue.");
        return true;
    }

    // =========================================================================
    // FEATURE 7: PROCESS CLAIM (QUEUE PEEK/POLL + ORACLE UPDATE)
    // =========================================================================

    public void processClaim(boolean approve) {
        if (claimQueue.isEmpty()) {
            System.out.println("\nNo pending claims in the queue.");
            return;
        }

        // QUEUE PEEK & POLL
        ClaimRequest processedClaim = claimQueue.poll();

        if (approve) {
            Item item = items.get(processedClaim.getItemId());
            if (item != null) {
                item.setStatus("CLAIMED");
            }
            if (isOracleAvailable) {
                claimDAO.updateClaimStatus(processedClaim.getItemId(), processedClaim.getStudentId(), "APPROVED");
                itemDAO.updateItemStatus(processedClaim.getItemId(), "CLAIMED");
            }
            actionStack.push("Approved claim for item #" + processedClaim.getItemId() + " (" + processedClaim.getStudentName() + ")");
            System.out.println("\nClaim APPROVED successfully!");
        } else {
            if (isOracleAvailable) {
                claimDAO.updateClaimStatus(processedClaim.getItemId(), processedClaim.getStudentId(), "REJECTED");
            }
            actionStack.push("Rejected claim for item #" + processedClaim.getItemId() + " (" + processedClaim.getStudentName() + ")");
            System.out.println("\nClaim REJECTED.");
        }
    }

    public ClaimRequest peekFrontClaim() {
        return claimQueue.peek();
    }

    // =========================================================================
    // FEATURE 8: RETURN ITEM (LINKEDLIST ADD + ORACLE JDBC TRANSACTION)
    // =========================================================================

    public boolean returnItem(int itemId) {
        Item item = items.get(itemId);

        if (item == null || !item.getStatus().equals("CLAIMED")) {
            System.out.println("\nError: Item #" + itemId + " must have 'CLAIMED' status to be returned.");
            return false;
        }

        // 1. Update HashMap status
        item.setStatus("RETURNED");

        // 2. Add to LinkedList recoveryHistory
        recoveryHistory.add(item);

        // 3. Perform Oracle JDBC Transaction
        if (isOracleAvailable) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.setAutoCommit(false); // Begin JDBC Transaction
                try {
                    itemDAO.updateItemStatus(itemId, "RETURNED");
                    historyDAO.addRecoveryHistory(item, item.getReportedBy());
                    conn.commit(); // Commit Transaction
                } catch (Exception ex) {
                    conn.rollback(); // Rollback Transaction on error
                    System.err.println("JDBC Transaction Rollback: " + ex.getMessage());
                }
            } catch (SQLException e) {
                System.err.println("Oracle Transaction Error: " + e.getMessage());
            }
        }

        // 4. Push to Stack (Undo)
        actionStack.push("Returned item #" + itemId + " (" + item.getName() + ")");
        System.out.println("\nItem #" + itemId + " successfully returned & logged in LinkedList + Oracle RECOVERY_HISTORY.");
        return true;
    }

    // =========================================================================
    // FEATURE 9, 10, 11, 12: QUEUE, LINKEDLIST, STACK, & STATS
    // =========================================================================

    public void viewClaimQueue() {
        if (claimQueue.isEmpty()) {
            System.out.println("\nNo pending claims in queue.");
            return;
        }

        System.out.println("\n==================================================");
        System.out.println("             CLAIM QUEUE (FIFO ORDER)");
        System.out.println("==================================================");

        int pos = 1;
        for (ClaimRequest claim : claimQueue) {
            System.out.println("Position #" + pos + ":");
            claim.display();
            System.out.println("--------------------------------------------------");
            pos++;
        }
    }

    public void viewRecoveryHistory() {
        if (recoveryHistory.isEmpty()) {
            System.out.println("\nNo recovered items in history yet.");
            return;
        }

        System.out.println("\n============================================================================================");
        System.out.println("                              RECOVERY HISTORY (LINKEDLIST)");
        System.out.println("============================================================================================");
        for (Item item : recoveryHistory) {
            System.out.println(item.toString());
        }
        System.out.println("============================================================================================");
    }

    public void undoLastAction() {
        if (actionStack.isEmpty()) {
            System.out.println("\nNo actions available to undo.");
            return;
        }

        String lastAction = actionStack.pop();
        System.out.println("\n==================================================");
        System.out.println("                  UNDO ACTION");
        System.out.println("==================================================");
        System.out.println("Undoing Last Action: " + lastAction);
        System.out.println("==================================================");
    }

    public void viewStatistics() {
        int totalItems = items.size();
        int lostItems = 0, foundItems = 0, activeItems = 0, claimedItems = 0, returnedItems = 0;

        for (Item item : items.values()) {
            if (item.getType().equals("LOST")) lostItems++;
            if (item.getType().equals("FOUND")) foundItems++;
            if (item.getStatus().equals("ACTIVE")) activeItems++;
            else if (item.getStatus().equals("CLAIMED")) claimedItems++;
            else if (item.getStatus().equals("RETURNED")) returnedItems++;
        }

        System.out.println("\n==================================================");
        System.out.println("              CAMPUS DROP STATISTICS");
        System.out.println("==================================================");
        System.out.println(String.format("  %-22s : %d", "Total Items (HashMap)", totalItems));
        System.out.println(String.format("  %-22s : %d", "Lost Items", lostItems));
        System.out.println(String.format("  %-22s : %d", "Found Items", foundItems));
        System.out.println(String.format("  %-22s : %d", "Active Items", activeItems));
        System.out.println(String.format("  %-22s : %d", "Claimed Items", claimedItems));
        System.out.println(String.format("  %-22s : %d", "Returned Items", returnedItems));
        System.out.println(String.format("  %-22s : %d", "Pending Claims (Queue)", claimQueue.size()));
        System.out.println(String.format("  %-22s : %d", "Recovery History (LinkedList)", recoveryHistory.size()));
        System.out.println(String.format("  %-22s : %s", "Oracle SQL DB Status", isOracleAvailable ? "CONNECTED" : "OFFLINE"));
        System.out.println("==================================================");
    }

    // =========================================================================
    // IN-MEMORY DATA STRUCTURE ACCESSORS (FAST O(1) / O(N) WEB ACCESS)
    // =========================================================================

    public List<Item> getAllItemsList() {
        return new java.util.ArrayList<>(items.values());
    }

    public List<ClaimRequest> getClaimQueueList() {
        return new java.util.ArrayList<>(claimQueue);
    }

    public List<Item> getRecoveryHistoryList() {
        return new java.util.ArrayList<>(recoveryHistory);
    }

    public Stack<String> getActionStack() {
        return actionStack;
    }

    public boolean deleteItem(int id) {
        Item item = items.remove(id);
        if (item != null) {
            if (isOracleAvailable) {
                itemDAO.deleteItem(id);
            }
            actionStack.push("Deleted item #" + id + " (" + item.getName() + ")");
            return true;
        }
        return false;
    }

    public String popLastAction() {
        if (actionStack.isEmpty()) {
            return null;
        }
        return actionStack.pop();
    }

    public boolean isOracleConnected() {
        return isOracleAvailable;
    }

    public int getItemsCount() {
        return items.size();
    }

    public int getQueueCount() {
        return claimQueue.size();
    }

    public int getHistoryCount() {
        return recoveryHistory.size();
    }
}
