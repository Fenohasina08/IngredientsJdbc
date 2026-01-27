import java.util.Objects;

public class DishIngredient {
    private Integer id;
    private Dish dish;
    private Ingredient ingredient;
    private Double quantity;
    private Unit unit;

    public  DishIngredient() {}
    public DishIngredient(int id, Dish dish, Ingredient ingredient, Double quantity, Unit unit) {
        this.id = id;
        this.dish = dish;
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.unit = unit;
    }



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DishIngredient that = (DishIngredient) o;
        return id == that.id && Objects.equals(dish, that.dish) && Objects.equals(ingredient, that.ingredient) && Objects.equals(quantity, that.quantity) && unit == that.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dish, ingredient, quantity, unit);
    }

    @Override
    public String toString() {
        return "DishIngredient{" +
                "id=" + id +
                ", ingredient=" + ingredient +
                ", quantity=" + quantity +
                ", unit=" + unit +
                '}';
    }
}