package Connection;

import classes.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    private Connection connection;

    public DataRetriever(Connection c) {
        this.connection = c;
    }

    // --- LOGIQUE TD3 : COÛT ET MARGE ---

    /**
     * Calcule le coût total des ingrédients pour un plat donné.
     */
    public Double getDishCost(int dishId) throws SQLException {
        // Utilisation de DishIngredient (table de liaison TD3)
        // Note: Si ta table en DB est en minuscules, retire les guillemets
        String sql = "SELECT i.price, di.quantity_required FROM ingredient i " +
                "JOIN \"DishIngredient\" di ON i.id = di.id_ingredient " +
                "WHERE di.id_dish = ?";
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

    /**
     * Calcule la marge brute d'un plat.
     */
    public Double getGrossMargin(int dishId) throws SQLException {
        String sql = "SELECT selling_price, name FROM dish WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, dishId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Double price = (Double) rs.getObject("selling_price");
                if (price == null) {
                    throw new RuntimeException("Prix de vente NULL pour le plat : " + rs.getString("name"));
                }
                return price - getDishCost(dishId);
            }
        }
        return 0.0;
    }

    // --- LOGIQUE TD4 : GESTION DES STOCKS ---

    /**
     * Récupère la liste de tous les mouvements de stock pour un ingrédient précis.
     * Cette liste sera utilisée par la méthode getStockValueAt(Instant t) dans la classe Ingredient.
     */
    public List<StockMovement> getMovementsForIngredient(int ingredientId) throws SQLException {
        List<StockMovement> movements = new ArrayList<>();
        // Nom de table stock_movement (recommandé en minuscules sous Postgres)
        String sql = "SELECT id, quantity, type, unit, creation_datetime FROM stock_movement WHERE id_ingredient = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, ingredientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // 1. Création de l'objet StockValue (Quantité + Unité) comme spécifié dans le TD4
                StockValue value = new StockValue(
                        rs.getDouble("quantity"),
                        Unit.valueOf(rs.getString("unit"))
                );

                // 2. Création du StockMovement
                movements.add(new StockMovement(
                        rs.getInt("id"),
                        value,
                        MovementTypeEnum.valueOf(rs.getString("type")),
                        rs.getTimestamp("creation_datetime").toInstant() // Conversion vers Instant
                ));
            }
        }
        return movements;
    }
}