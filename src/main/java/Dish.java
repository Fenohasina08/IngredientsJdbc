import java.util.List;
import java.util.Objects;

public class Dish {
    private Integer id;
    private String name;
    private Double price; // Prix de vente
    private DishTypeEnum dishType;
    private List<Ingredient> ingredients;

    public Dish() {}

    public Dish(Integer id, String name, Double price, DishTypeEnum dishType, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.dishType = dishType;
        this.ingredients = ingredients;
    }

    /**
     * Calcule le coût de revient total du plat.
     * Prend en compte les facteurs de conversion des ingrédients.
     */
    public Double getDishCost() {
        double totalCost = 0.0;
        if (this.ingredients == null) return totalCost;

        for (Ingredient current : ingredients) {
            Double unitPrice = (current.getPrice() == null) ? 0.0 : current.getPrice();
            // Utilise la méthode de conversion de l'ingrédient
            Double realQuantity = current.getConvertedQuantity();

            totalCost += (unitPrice * realQuantity);
        }
        return totalCost;
    }

    /**
     * Calcule la marge brute.
     * Lève une exception si le prix de vente est absent.
     */
    public Double getGrossMargin() {
        if (this.price == null) {
            throw new RuntimeException("Erreur : Le prix de vente du plat '" + name + "' n'est pas défini (NULL).");
        }
        return this.price - getDishCost();
    }

    // Getters et Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public DishTypeEnum getDishType() { return dishType; }
    public void setDishType(DishTypeEnum dishType) { this.dishType = dishType; }

    public List<Ingredient> getIngredients() { return ingredients; }
    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
        if (ingredients != null) {
            for (Ingredient i : ingredients) i.setDish(this);
        }
    }

    @Override
    public String toString() {
        return "Dish{" + "name='" + name + '\'' + ", sellingPrice=" + price + '}';
    }
}