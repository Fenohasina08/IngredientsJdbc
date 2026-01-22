package classes;

public class DishIngredient {
    private Dish dish;
    private Ingredient ingredient;
    private double quantityRequired;
    private Unit unit;

    public DishIngredient(Dish dish, Ingredient ingredient, double quantityRequired, Unit unit) {
        this.dish = dish;
        this.ingredient = ingredient;
        this.quantityRequired = quantityRequired;
        this.unit = unit;
    }

    // Getters
    public Dish getDish() { return dish; }
    public Ingredient getIngredient() { return ingredient; }
    public double getQuantityRequired() { return quantityRequired; }
    public Unit getUnit() { return unit; }
}
