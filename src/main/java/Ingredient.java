import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Ingredient {

    private Integer id;
    private String name;
    private Double price;
    private CategoryEnum category;

    // Initialisation de la liste pour éviter les erreurs de type NullPointerException
    private List<StockMovement> stockMovementList = new ArrayList<>();
    public Ingredient(int i) {
        this.id = i;
    }

    // On met à jour celui-là pour que la catégorie soit en 3ème position
    public Ingredient(Integer id, String name, CategoryEnum category, Double price) {
        this.id = id;
        this.name = name;
        this.category = category; // On l'assigne ici
        this.price = price;       // Et le prix à la fin
    }

    /**
     * Calcule la valeur du stock à un instant t donné.
     * @param t L'instant cible pour le calcul
     * @return Un objet StockValue contenant la quantité totale et l'unité
     */
    public StockValue getStockValueAt(Instant t) {
        double totalQuantity = 0.0;
        // On initialise avec une unité par défaut (ex: G) au cas où la liste est vide
        UnitEnum currentUnit = UnitEnum.G;

        for (StockMovement mov : stockMovementList) {
            // Vérifie si le mouvement a eu lieu AVANT ou EXACTEMENT à l'instant t
            if (!mov.getCreationDatetime().isAfter(t)) {
                if (mov.getType() == MovementTypeEnum.IN) {
                    totalQuantity += mov.getValue().getQuantity();
                } else if (mov.getType() == MovementTypeEnum.OUT) {
                    totalQuantity -= mov.getValue().getQuantity();
                }
                // On met à jour l'unité pour correspondre à celle du dernier mouvement traité
                currentUnit = mov.getValue().getUnit();
            }
        }

        return new StockValue(totalQuantity, currentUnit);
    }

    // --- Getters et Setters ---

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
                ", movementsCount=" + stockMovementList.size() +
                '}';
    }
}