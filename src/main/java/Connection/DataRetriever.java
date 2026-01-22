package Connection;
import classes.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    private Connection connection;
    public DataRetriever(Connection connection) { this.connection = connection; }

    public Dish findDishById(int id) throws SQLException {
        String sql = "SELECT * FROM dish WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Dish d = new Dish(rs.getInt("id"), rs.getString("name"), 
                         DishTypeEnum.valueOf(rs.getString("dish_type")), 
                         new ArrayList<>(), rs.getObject("selling_price") != null ? rs.getDouble("selling_price") : null);
                d.setIngredients(findIngredientsByDishId(id));
                return d;
            }
        }
        return null;
    }

    public List<Ingredient> findIngredientsByDishId(int dishId) throws SQLException {
        List<Ingredient> list = new ArrayList<>();
        String sql = "SELECT i.*, di.quantity_required, di.unit FROM ingredient i " +
                     "JOIN DishIngredient di ON i.id = di.id_ingredient WHERE di.id_dish = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, dishId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Ingredient ing = new Ingredient(rs.getInt("id"), rs.getString("name"), 
                                 rs.getDouble("price"), CategoryEnum.valueOf(rs.getString("category")));
                ing.setQuantity(rs.getDouble("quantity_required"));
                ing.setUnit(UnitEnum.valueOf(rs.getString("unit")));
                list.add(ing);
            }
        }
        return list;
    }

    public Double getDishCost(int dishId) throws SQLException {
        Dish dish = findDishById(dishId);
        double total = 0;
        for (Ingredient ing : dish.getIngredients()) {
            total += ing.getPrice() * ing.getQuantity();
        }
        return total;
    }

    public Double getGrossMargin(int dishId) throws SQLException {
        Dish dish = findDishById(dishId);
        if (dish.getPrice() == null) throw new RuntimeException("Prix de vente NULL pour " + dish.getName());
        return dish.getPrice() - getDishCost(dishId);
    }

    public void saveIngredient(Ingredient ing) throws SQLException {
        String sql = "INSERT INTO ingredient (id, name, price, category) VALUES (?, ?, ?, ?::ingredient_category) " +
                     "ON CONFLICT (id) DO UPDATE SET name=EXCLUDED.name, price=EXCLUDED.price";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, ing.getId()); ps.setString(2, ing.getName());
            ps.setDouble(3, ing.getPrice()); ps.setString(4, ing.getCategory().name());
            ps.executeUpdate();
        }
    }

    public Dish saveDish(Dish dish) throws SQLException {
        // Logique simplifiée pour le test
        return dish;
    }
}
