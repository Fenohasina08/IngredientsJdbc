import java.util.Objects;

public class Ingredient {
    private Integer id;
    private String name;
    private CategoryEnum category;
    private Double price;    // Prix à l'unité de base (ex: prix pour 1g)
    private Double quantity; // Quantité dans la recette (ex: 2.0)
    private UnitEnum unit;   // Unité de la recette (ex: KG)
    private Dish dish;

    public Ingredient() {}

    public Ingredient(Integer id, String name, CategoryEnum category, Double price, Double quantity, UnitEnum unit) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.unit = unit;
    }

    /**
     * Calcule la quantité ramenée à l'unité de base.
     * Exemple : 2 (quantity) * 1000 (factor pour KG) = 2000.0
     */
    public Double getConvertedQuantity() {
        if (quantity == null) return 0.0;
        Double factor = (unit == null) ? 1.0 : unit.getFactor();
        return quantity * factor;
    }

    // Getters et Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public CategoryEnum getCategory() { return category; }
    public void setCategory(CategoryEnum category) { this.category = category; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public UnitEnum getUnit() { return unit; }
    public void setUnit(UnitEnum unit) { this.unit = unit; }

    public Dish getDish() { return dish; }
    public void setDish(Dish dish) { this.dish = dish; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && unit == that.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, unit);
    }

    @Override
    public String toString() {
        return "Ingredient{" + "name='" + name + '\'' + ", quantity=" + quantity + ", unit=" + unit + '}';
    }
}