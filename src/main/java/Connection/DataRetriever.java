package Connection;
import classes.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    private Connection connection;
    public DataRetriever(Connection c) { this.connection = c; }

    public Double getDishCost(int dishId) throws SQLException {
        String sql = "SELECT i.price, di.quantity_required FROM ingredient i JOIN DishIngredient di ON i.id = di.id_ingredient WHERE di.id_dish = ?";
        double total = 0;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, dishId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                total += rs.getDouble("price") * rs.getDouble("quantity_required");
            }
        }
        return total;
    }

    public Double getGrossMargin(int dishId) throws SQLException {
        String sql = "SELECT selling_price, name FROM dish WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, dishId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Double price = (Double) rs.getObject("selling_price");
                if (price == null) throw new RuntimeException("Prix de vente NULL pour " + rs.getString("name"));
                return price - getDishCost(dishId);
            }
        }
        return 0.0;
    }
}
