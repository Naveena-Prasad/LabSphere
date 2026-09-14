package backend;
public class LabItem extends Equipment {
    private final double voltageRating;

    public LabItem(String equipmentId, String name, double voltageRating) {
        super(equipmentId, name);
        this.voltageRating = voltageRating;
    }

    @Override
    public void checkOut(String studentId) {
        if (isAvailable) {
            isAvailable = false;
            issuedTo = studentId;
            System.out.println("\n[SUCCESS] " + name + " (ID: " + equipmentId + ") successfully issued to Student: " + studentId);
        } else {
            System.out.println("\n[ERROR] " + name + " is already checked out by " + issuedTo + "!");
        }
    }

    @Override
    public void returnItem() {
        if (!isAvailable) {
            System.out.println("\n[SUCCESS] " + name + " returned successfully from " + issuedTo);
            isAvailable = true;
            issuedTo = null;
        } else {
            System.out.println("\n[ERROR] This item was not checked out.");
        }
    }

    @Override
    public double calculatePenalty(int delayedDays) {
        return delayedDays * 50.0;
    }

    @Override
    public void displayDetails() {
        System.out.printf(" | %-8s | %-22s | Type: Electronic  | Voltage: %-5.1fV | Status: %-18s |\n", 
            equipmentId, name, voltageRating, (isAvailable ? "Available" : "In-Use (" + issuedTo + ")"));
    }
}