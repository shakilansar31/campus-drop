import model.ClaimRequest;
import model.Item;

import java.util.Scanner;

/**
 * Main.java
 * Console User Interface driver for CampusDrop System.
 * Connects directly to CampusDrop logic, Java Collections, and Oracle Database.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final CampusDrop system = new CampusDrop();

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            displayMenu();
            int choice = readIntInput("Enter your choice (1-13): ");

            switch (choice) {
                case 1:
                    handleReportLostItem();
                    break;
                case 2:
                    handleReportFoundItem();
                    break;
                case 3:
                    handleSearchItem();
                    break;
                case 4:
                    system.viewAllItems();
                    break;
                case 5:
                    system.findPossibleMatches();
                    break;
                case 6:
                    handleSubmitClaim();
                    break;
                case 7:
                    handleProcessClaim();
                    break;
                case 8:
                    handleReturnItem();
                    break;
                case 9:
                    system.viewClaimQueue();
                    break;
                case 10:
                    system.viewRecoveryHistory();
                    break;
                case 11:
                    system.undoLastAction();
                    break;
                case 12:
                    system.viewStatistics();
                    break;
                case 13:
                    System.out.println("\nThank you for using CampusDrop Smart Lost & Found System!");
                    System.out.println("Exiting application...");
                    running = false;
                    break;
                default:
                    System.out.println("\nInvalid choice! Please select an option between 1 and 13.");
            }

            if (running) {
                promptEnterToContinue();
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\n==================================================");
        System.out.println("                  CAMPUS DROP");
        System.out.println("       SMART LOST & FOUND MANAGEMENT SYSTEM");
        System.out.println("==================================================");
        System.out.println(" 1. Report Lost Item");
        System.out.println(" 2. Report Found Item");
        System.out.println(" 3. Search Item");
        System.out.println(" 4. View All Items");
        System.out.println(" 5. Find Possible Matches");
        System.out.println(" 6. Submit Claim");
        System.out.println(" 7. Process Claim");
        System.out.println(" 8. Return Item");
        System.out.println(" 9. View Claim Queue");
        System.out.println("10. View Recovery History");
        System.out.println("11. Undo Last Action");
        System.out.println("12. View Statistics");
        System.out.println("13. Exit");
        System.out.println("==================================================");
    }

    private static void handleReportLostItem() {
        System.out.println("\n--- REPORT LOST ITEM ---");
        String name = readNonEmptyString("Enter Item Name: ");
        String category = readNonEmptyString("Enter Category: ");
        String location = readNonEmptyString("Enter Location: ");
        String date = readNonEmptyString("Enter Date: ");
        String studentName = readNonEmptyString("Enter Student Name: ");

        int itemId = system.reportLostItem(name, category, location, date, studentName);
        System.out.println("\nLost item reported successfully!");
        System.out.println("Generated Item ID: " + itemId);
    }

    private static void handleReportFoundItem() {
        System.out.println("\n--- REPORT FOUND ITEM ---");
        String name = readNonEmptyString("Enter Item Name: ");
        String category = readNonEmptyString("Enter Category: ");
        String location = readNonEmptyString("Enter Location: ");
        String date = readNonEmptyString("Enter Date: ");
        String finderName = readNonEmptyString("Enter Finder Name: ");

        int itemId = system.reportFoundItem(name, category, location, date, finderName);
        System.out.println("\nFound item reported successfully!");
        System.out.println("Generated Item ID: " + itemId);
    }

    private static void handleSearchItem() {
        System.out.println("\n--- SEARCH ITEM ---");
        System.out.println("1. Search by Item ID (HashMap -> Oracle)");
        System.out.println("2. Search by Item Name");
        System.out.println("3. Search by Category");
        System.out.println("4. Search by Location");

        int subChoice = readIntInput("Select Search Criteria (1-4): ");
        switch (subChoice) {
            case 1:
                int id = readIntInput("Enter Item ID: ");
                Item item = system.searchItemById(id);
                if (item != null) {
                    System.out.println("\nItem Found:");
                    item.display();
                } else {
                    System.out.println("\nNo item found with ID: " + id);
                }
                break;
            case 2:
                String name = readNonEmptyString("Enter Item Name to search: ");
                system.searchItemByKeyword(name, "NAME");
                break;
            case 3:
                String cat = readNonEmptyString("Enter Category to search: ");
                system.searchItemByKeyword(cat, "CATEGORY");
                break;
            case 4:
                String loc = readNonEmptyString("Enter Location to search: ");
                system.searchItemByKeyword(loc, "LOCATION");
                break;
            default:
                System.out.println("Invalid search choice.");
        }
    }

    private static void handleSubmitClaim() {
        System.out.println("\n--- SUBMIT CLAIM FOR FOUND ITEM ---");
        int itemId = readIntInput("Enter Found Item ID: ");
        String studentName = readNonEmptyString("Enter Student Name: ");
        String studentId = readNonEmptyString("Enter Student ID: ");
        String claimReason = readNonEmptyString("Enter Claim Reason: ");

        system.submitClaim(itemId, studentName, studentId, claimReason);
    }

    private static void handleProcessClaim() {
        System.out.println("\n--- PROCESS CLAIM REQUEST ---");
        ClaimRequest frontClaim = system.peekFrontClaim();
        if (frontClaim == null) {
            System.out.println("No pending claims in the queue.");
            return;
        }

        System.out.println("Claim Request at Front of Queue:");
        frontClaim.display();

        String decision = readNonEmptyString("Approve claim? (Y/N): ");
        if (decision.equalsIgnoreCase("Y")) {
            system.processClaim(true);
        } else if (decision.equalsIgnoreCase("N")) {
            system.processClaim(false);
        } else {
            System.out.println("Invalid decision input. Claim remains in queue.");
        }
    }

    private static void handleReturnItem() {
        System.out.println("\n--- RETURN ITEM TO OWNER ---");
        int itemId = readIntInput("Enter Item ID to Return: ");
        system.returnItem(itemId);
    }

    private static int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number! Please enter a valid integer.");
            }
        }
    }

    private static String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    private static void promptEnterToContinue() {
        System.out.print("\nPress ENTER to return to main menu...");
        scanner.nextLine();
    }
}
