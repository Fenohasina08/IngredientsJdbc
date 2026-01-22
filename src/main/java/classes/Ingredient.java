package classes;
public class Ingredient {
    private Integer id;
    private String name;
    private Double price;
    private CategoryEnum category;
    private Double quantity; // Pour le Many-to-Many
    private UnitEnum unit;

    public Ingredient(Integer id, String name, Double price, CategoryEnum category) {
        this.id = id; this.name = name; this.price = price; this.category = category;
    }
    public Integer getId() { return id; }
    public String getName() { return name; }
    public Double getPrice() { return price; }
    public CategoryEnum getCategory() { return category; }
    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }
    public UnitEnum getUnit() { return unit; }
    public void setUnit(UnitEnum unit) { this.unit = unit; }
}
