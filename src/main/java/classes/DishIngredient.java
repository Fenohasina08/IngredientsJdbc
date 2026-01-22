package classes;

public class DishIngredient {
    private int id;
    private double quantityRequired;
    private UnitEnum unit;
    private Dish dish;
    private Ingredient ingredient;

    public DishIngredient(int id, double quantityRequired, UnitEnum unit, Dish dish, Ingredient ingredient) {
        this.id = id;
        this.quantityRequired = quantityRequired;
        this.unit = unit;
        this.dish = dish;
        this.ingredient = ingredient;
    }
}
