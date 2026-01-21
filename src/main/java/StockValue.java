public class StockValue {
    private double quantity;
    private UnitEnum unit; // Utilise ton UnitEnum.java existant

    public StockValue(double quantity, UnitEnum unit) {
        this.quantity = quantity;
        this.unit = unit;
    }

    // Getters
    public double getQuantity() { return quantity; }
    public UnitEnum getUnit() { return unit; }
}
