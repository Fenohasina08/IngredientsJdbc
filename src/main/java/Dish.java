import java.util.List;

public class Dish {
    private Integer id;
    private String name;
    private Double price;
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

    public Double getDishCost() {
        double totalCost = 0.0;
        if (this.ingredients == null) return totalCost;

        for (Ingredient current : ingredients) {
            Double unitPrice = (current.getPrice() == null) ? 0.0 : current.getPrice();
            Double quantity = (current.getQuantity() == null) ? 0.0 : current.getQuantity();
            // Calcul direct P * Q comme demandé
            totalCost += (unitPrice * quantity);
        }
        return totalCost;
    }

    public Double getGrossMargin() {
        if (this.price == null) {
            throw new RuntimeException("Erreur : Le prix de vente du plat '" + name + "' est absent.");
        }
        return this.price - getDishCost();
    }

    // Getters et Setters standards
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
        return String.format("Plat: %s | Coût: %s€ | Marge: %s€",
                name, getDishCost(), (price != null ? getGrossMargin() : "N/A"));
    }
}