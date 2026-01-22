package classes;
import java.util.ArrayList;
import java.util.List;

public class Dish {
    private Integer id;
    private String name;
    private DishTypeEnum dishType;
    private Double price;
    private List<Ingredient> ingredients;

    public Dish(Integer id, String name, DishTypeEnum dishType, List<Ingredient> ingredients, Double price) {
        this.id = id; this.name = name; this.dishType = dishType;
        this.ingredients = (ingredients != null) ? ingredients : new ArrayList<>();
        this.price = price;
    }
    public Integer getId() { return id; }
    public String getName() { return name; }
    public DishTypeEnum getDishType() { return dishType; }
    public Double getPrice() { return price; }
    public List<Ingredient> getIngredients() { return ingredients; }
    public void setIngredients(List<Ingredient> ingredients) { this.ingredients = ingredients; }
}
