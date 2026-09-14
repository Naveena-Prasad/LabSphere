package backend;
public abstract class Equipment implements Bookable {
    protected String equipmentId;
    protected String name;
    protected boolean isAvailable;
    protected String issuedTo;

    public Equipment(String equipmentId, String name) {
        this.equipmentId = equipmentId;
        this.name = name;
        this.isAvailable = true;
        this.issuedTo = null;
    }

    public String getEquipmentId() { return equipmentId; }
    public String getName() { return name; }
    public boolean isAvailable() { return isAvailable; }
    public String getIssuedTo() { return issuedTo; }

    public abstract void displayDetails();
}