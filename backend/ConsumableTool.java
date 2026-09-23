package backend;
public class ConsumableTool extends Equipment {
    private int stockQuantity;

    public ConsumableTool(String equipmentId, String name, int stockQuantity) {
        super(equipmentId, name);
        this.stockQuantity = stockQuantity;
    }

    @Override
    public void checkOut(String studentId) {
        if (stockQuantity > 0) {
            stockQuantity--;
            System.out.println("\n[SUCCESS] 1 unit of " + name + " allocated to Student: " + studentId);
        } else {
            System.out.println("\n[ERROR] " + name + " is currently out of stock!");
        }
    }

    @Override
    public void returnItem() {
        stockQuantity++;
        System.out.println("\n[SUCCESS] Consumable stock updated. Unit restocked.");
    }

    @Override
    public double calculatePenalty(int delayedDays) {
        return delayedDays * 10.0;
    }

    @Override
    public void displayDetails() {
        System.out.printf(" | %-8s | %-22s | Type: Consumable  | Stock: %-7d | Status: Available        |\n", 
            equipmentId, name, stockQuantity);
    }
}