import java.util.List;
import java.util.Objects;

public class Dish {

    private Integer id;
    private String name;
    private DishTypeEnum dishType;
    private Double price;
    private List<DishIngredient> ingredients;

    public Dish() {}

    public Dish(Integer id, String name, DishTypeEnum dishType,
                Double price, List<DishIngredient> ingredients) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.price = price;
        this.ingredients = ingredients;
    }


    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public DishTypeEnum getDishType() { return dishType; }
    public void setDishType(DishTypeEnum dishType) { this.dishType = dishType; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public List<DishIngredient> getIngredients() { return ingredients; }
    public void setIngredients(List<DishIngredient> ingredients) {
        this.ingredients = ingredients;
    }


    public Double getDishCost() {
        double cost = 0.0;
        if (ingredients == null) return 0.0;

        for (DishIngredient di : ingredients) {
            if (di.getIngredient() != null &&
                    di.getIngredient().getPrice() != null &&
                    di.getQuantity() != null) {
                cost += di.getIngredient().getPrice() * di.getQuantity();
            }
        }
        return cost;
    }

    public Double getGrossMargin() {
        if (price == null) {
            throw new RuntimeException("Dish price is null");
        }
        return price - getDishCost();
    }


    public void prettyPrint() {
        System.out.println("Plat");
        System.out.println("---------------------------------");
        System.out.println("ID        : " + id);
        System.out.println("Nom       : " + name);
        System.out.println("Type      : " + dishType);
        System.out.println("Prix      : " + price + " Ar\n");

        System.out.println("Ingrédients :");

        for (DishIngredient di : ingredients) {
            Ingredient ing = di.getIngredient();
            System.out.println(" ID " + ing.getId() +": " + ing.getName());
            System.out.println("     - Catégorie : " + ing.getCategory());
            System.out.println("     - Prix      : " + ing.getPrice() + " Ar");
            System.out.println("     - Quantité  : " + di.getQuantity() + " " + di.getUnit());
            System.out.println();
        }
        System.out.println("---------------------------------");
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dish)) return false;
        Dish dish = (Dish) o;
        return Objects.equals(id, dish.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
