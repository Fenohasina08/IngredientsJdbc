package classes;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
public class Ingredient {
    private int id;
    private String name;
    private Double price;
    private CategoryEnum category;
    private List<StockMovement> stockMovementList = new ArrayList<>();
    public Ingredient(int id, String name, Double price, CategoryEnum cat) {
        this.id = id; this.name = name; this.price = price; this.category = cat;
    }
    public double getStockValueAt(Instant t) {
        double total = 0;
        for (StockMovement m : stockMovementList) {
            if (!m.getCreationDatetime().isAfter(t)) {
                if (m.getType() == MovementTypeEnum.IN) total += m.getQuantity();
                else total -= m.getQuantity();
            }
        }
        return total;
    }
    public void addMovement(StockMovement m) { stockMovementList.add(m); }
    public String getName() { return name; }
    public Double getPrice() { return price; }
}
