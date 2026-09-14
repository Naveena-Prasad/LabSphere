package backend;
import java.io.*;
import java.util.*;

public class LabInventorySystem {
    private static final String RECORD_FILE = "data/inventory_audit.txt";

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            ArrayList<Equipment> inventory = new ArrayList<>();
            
            inventory.add(new LabItem("EQ101", "Digital Oscilloscope", 230.0));
            inventory.add(new LabItem("EQ102", "Digital Multimeter", 9.0));
            inventory.add(new ConsumableTool("EQ201", "Jumper Wires Kit", 50));
            inventory.add(new ConsumableTool("EQ202", "Breadboard", 30));
            
            int choice = 0;
            do {
                printHeader();
                System.out.println("  1. View Complete Lab Inventory");
                System.out.println("  2. Issue Equipment to Student");
                System.out.println("  3. Process Equipment Return");
                System.out.println("  4. Calculate Late Penalty / Fine");
                System.out.println("  5. Export Audit Logs to File (Persistence)");
                System.out.println("  6. Exit System");
                System.out.println("=========================================================");
                System.out.print("  Enter your selection (1-6): ");
                
                try {
                    choice = scanner.nextInt();
                    scanner.nextLine();
                    
                    switch (choice) {
                        case 1 -> {
                            printSubHeader("CURRENT LABORATORY INVENTORY STATUS");
                            System.out.println(" ----------------------------------------------------------------------------------");
                            for (Equipment eq : inventory) {
                                eq.displayDetails();
                            }
                            System.out.println(" ----------------------------------------------------------------------------------");
                        }
                            
                        case 2 -> {
                            printSubHeader("EQUIPMENT CHECK-OUT PORTAL");
                            System.out.print("  Enter Equipment ID (e.g., EQ101): ");
                            String issueId = scanner.nextLine();
                            System.out.print("  Enter Student ID: ");
                            String studentId = scanner.nextLine();
                            
                            boolean issued = false;
                            for (Equipment eq : inventory) {
                                if (eq.getEquipmentId().equalsIgnoreCase(issueId)) {
                                    eq.checkOut(studentId);
                                    issued = true;
                                    break;
                                }
                            }
                            if (!issued) System.out.println("\n[ERROR] Equipment ID not found.");
                        }
                            
                        case 3 -> {
                            printSubHeader("EQUIPMENT RETURN PORTAL");
                            System.out.print("  Enter Equipment ID to return: ");
                            String returnId = scanner.nextLine();
                            
                            boolean returned = false;
                            for (Equipment eq : inventory) {
                                if (eq.getEquipmentId().equalsIgnoreCase(returnId)) {
                                    eq.returnItem();
                                    returned = true;
                                    break;
                                }
                            }
                            if (!returned) System.out.println("\n[ERROR] Equipment ID not recognized.");
                        }
                            
                        case 4 -> {
                            printSubHeader("PENALTY & FINE CALCULATOR");
                            System.out.print("  Enter Equipment ID: ");
                            String penId = scanner.nextLine();
                            System.out.print("  Enter number of overdue days: ");
                            int days = scanner.nextInt();
                            
                            boolean calculated = false;
                            for (Equipment eq : inventory) {
                                if (eq.getEquipmentId().equalsIgnoreCase(penId)) {
                                    double fine = eq.calculatePenalty(days);
                                    System.out.println("\n[RESULT] Total penalty: Rs. " + fine);
                                    calculated = true;
                                    break;
                                }
                            }
                            if (!calculated) System.out.println("\n[ERROR] Equipment ID not found.");
                        }
                            
                        case 5 -> exportAuditLog(inventory);
                            
                        case 6 -> System.out.println("\n  Exiting System. Thank you!");
                            
                        default -> System.out.println("\n[WARNING] Invalid choice! Choose between 1 and 6.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("\n[CRITICAL ERROR] Please enter valid numbers!");
                    scanner.nextLine();
                }
                
                if (choice != 6) {
                    System.out.println("\n  Press Enter to continue...");
                    scanner.nextLine();
                }
                
            } while (choice != 6);
        }
    }

    private static void printHeader() {
        System.out.println("\n=========================================================");
        System.out.println("     CAMPUS LAB EQUIPMENT INVENTORY & BOOKING SYSTEM     ");
        System.out.println("=========================================================");
    }

    private static void printSubHeader(String title) {
        System.out.println("\n--- " + title + " ---");
    }

    private static void exportAuditLog(ArrayList<Equipment> inventory) {
        try {
            File dir = new File("data");
            if (!dir.exists()) dir.mkdir();

            try (PrintWriter writer = new PrintWriter(new FileWriter(RECORD_FILE))) {
                writer.println("=== CAMPUS LAB INVENTORY AUDIT RECORD ===");
                for (Equipment eq : inventory) {
                    writer.println("ID: " + eq.getEquipmentId() + " | Name: " + eq.getName() + " | Status: " + (eq.isAvailable() ? "Available" : "In-Use"));
                }
            }
            System.out.println("\n[SUCCESS] Audit logs exported to 'data/inventory_audit.txt'");
        } catch (IOException e) {
            System.out.println("\n[ERROR] Failed to save records: " + e.getMessage());
        }
    }
}