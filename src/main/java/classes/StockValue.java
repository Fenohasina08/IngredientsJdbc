package classes;
public class StockValue {
    private double quantity;
    private Unit unit;
    public StockValue(double quantity, Unit unit) { this.quantity = quantity; this.unit = unit; }
    public double getQuantity() { return quantity; }
    public Unit getUnit() { return unit; }
}
