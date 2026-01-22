package classes;
public class Dish {
    private int id;
    private String name;
    private DishType dishType;
    private Double price;
    public Dish(int id, String name, DishType type, Double price) {
        this.id = id; this.name = name; this.dishType = type; this.price = price;
    }
    public String getName() { return name; }
    public Double getPrice() { return price; }
}
