import java.util.List;
import java.util.Objects;

public class Dish {
    private Integer id;
    private Double price; // Prix de vente du plat 💰
    private String name;
    private DishTypeEnum dishType;
    private List<Ingredient> ingredients;

    // Constructeurs
    public Dish() {
    }

    public Dish(Integer id, String name, DishTypeEnum dishType, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.ingredients = ingredients;
    }

    /**
     * Calcule le coût total de revient du plat en fonction des ingrédients.
     * Utilise 0.0 si le prix ou la quantité d'un ingrédient est absent. 🛠️
     */
    public Double getDishCost() {
        double totalCost = 0.0;

        if (this.ingredients == null) {
            return totalCost;
        }

        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient current = ingredients.get(i);

            // Gestion des valeurs nulles avec l'opérateur ternaire
            Double unitPrice = (current.getPrice() == null) ? 0.0 : current.getPrice();
            Double quantity = (current.getQuantity() == null) ? 0.0 : current.getQuantity();

            totalCost += (unitPrice * quantity);
        }
        return totalCost;
    }

    /**
     * Calcule la marge brute (Prix de vente - Coût de revient).
     * Lève une exception si le prix de vente est inconnu. ⚠️
     */
    public Double getGrossMargin() {
        if (this.price == null) {
            throw new RuntimeException("Impossible de calculer la marge : le prix de vente est null pour le plat " + name);
        }
        return this.price - getDishCost();
    }

    // Getters et Setters
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DishTypeEnum getDishType() {
        return dishType;
    }

    public void setDishType(DishTypeEnum dishType) {
        this.dishType = dishType;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        if (ingredients == null) {
            this.ingredients = null;
            return;
        }
        for (int i = 0; i < ingredients.size(); i++) {
            ingredients.get(i).setDish(this);
        }
        this.ingredients = ingredients;
    }

    // Méthodes standards (equals, hashCode, toString)
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Dish dish = (Dish) o;
        return Objects.equals(id, dish.id) && Objects.equals(name, dish.name) &&
                dishType == dish.dishType && Objects.equals(ingredients, dish.ingredients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dishType, ingredients);
    }

    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", price=" + price +
                ", name='" + name + '\'' +
                ", dishType=" + dishType +
                ", ingredients=" + ingredients +
                '}';
    }
}