import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Ingredient {

    private int id; // Le sujet utilise désormais 'int'
    private String name; [cite: 27]
    private Double price; [cite: 28]
    private CategoryEnum category; [cite: 29]

    // Nouvelle liste pour gérer l'historique des stocks
    private List<StockMovement> stockMovementList = new ArrayList<>();

    public Ingredient() {
    }

    public Ingredient(int id, String name, Double price, CategoryEnum category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    // Méthode demandée par le sujet pour calculer le stock à une date T
    public StockValue getStockValueAt(Instant t) {
        // Nous coderons la logique de calcul (Entrées - Sorties) ensemble plus tard
        return null;
    }

    // Getters et Setters mis à jour
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public CategoryEnum getCategory() { return category; }
    public void setCategory(CategoryEnum category) { this.category = category; }

    public List<StockMovement> getStockMovementList() { return stockMovementList; }
    public void setStockMovementList(List<StockMovement> stockMovementList) {
        this.stockMovementList = stockMovementList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ingredient)) return false;
        Ingredient that = (Ingredient) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category=" + category +
                '}';
    }
}