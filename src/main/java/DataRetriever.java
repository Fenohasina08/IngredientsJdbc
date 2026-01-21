import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    public Dish findDishById(Integer id) {
        try (Connection connection = new DBConnection().getConnection()) {
            String sql = "SELECT id, name, dish_type, selling_price FROM dish WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Dish dish = new Dish();
                        dish.setId(rs.getInt("id"));
                        dish.setName(rs.getString("name"));
                        dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                        dish.setPrice(rs.getDouble("selling_price"));
                        dish.setIngredients(findIngredientByDishId(id));
                        return dish;
                    }
                }
            }
            throw new RuntimeException("Dish non trouvé ID: " + id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Ingredient> findIngredientByDishId(Integer dishId) {
        List<Ingredient> ingredients = new ArrayList<>();
        try (Connection conn = new DBConnection().getConnection()) {
            String sql = "SELECT i.id, i.name, i.price, i.category, di.quantity_required, di.unit " +
                    "FROM ingredient i JOIN DishIngredient di ON i.id = di.id_ingredient " +
                    "WHERE di.id_dish = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, dishId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Ingredient ing = new Ingredient();
                        ing.setId(rs.getInt("id"));
                        ing.setName(rs.getString("name"));
                        ing.setPrice(rs.getDouble("price"));
                        ing.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                        ing.setQuantity(rs.getDouble("quantity_required"));
                        String unitStr = rs.getString("unit");
                        if (unitStr != null) ing.setUnit(UnitEnum.valueOf(unitStr));
                        ingredients.add(ing);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ingredients;
    }
    public Ingredient saveIngredient(Ingredient toSave) {
        // 1. Sauvegarde/Mise à jour de l'ingrédient
        String sqlIngredient = "INSERT INTO ingredient (id, name, price, category) VALUES (?, ?, ?, ?::ingredient_category) " +
                "ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, price = EXCLUDED.price, category = EXCLUDED.category";

        // 2. Sauvegarde des mouvements de stock (avec ON CONFLICT DO NOTHING)
        String sqlMovement = "INSERT INTO StockMovement (id, id_ingredient, quantity, type, unit, creation_datetime) " +
                "VALUES (?, ?, ?, ?::mouvement_type, ?::unit_type, ?) " +
                "ON CONFLICT (id) DO NOTHING"; [cite: 51]

        try (Connection conn = new DBConnection().getConnection()) {
            conn.setAutoCommit(false); // On utilise une transaction
            try {
                // Sauvegarde de l'ingrédient
                try (PreparedStatement psIng = conn.prepareStatement(sqlIngredient)) {
                    psIng.setInt(1, toSave.getId());
                    psIng.setString(2, toSave.getName());
                    psIng.setDouble(3, toSave.getPrice());
                    psIng.setString(4, toSave.getCategory().name());
                    psIng.executeUpdate();
                }

                // Sauvegarde de la liste des mouvements
                try (PreparedStatement psMov = conn.prepareStatement(sqlMovement)) {
                    for (StockMovement mov : toSave.getStockMovementList()) { [cite: 50]
                        psMov.setInt(1, mov.getId()); [cite: 51]
                        psMov.setInt(2, toSave.getId());
                        psMov.setDouble(3, mov.getValue().getQuantity());
                        psMov.setString(4, mov.getType().name());
                        psMov.setString(5, mov.getValue().getUnit().name());
                        psMov.setTimestamp(6, Timestamp.from(mov.getCreationDatetime()));
                        psMov.addBatch();
                    }
                    psMov.executeBatch();
                }

                conn.commit();
                return toSave;
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Erreur lors de la sauvegarde : " + e.getMessage());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Dish saveDish(Dish dish) {
        String sql = "INSERT INTO dish (id, name, dish_type, selling_price) VALUES (?, ?, ?::dish_type, ?) " +
                "ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, selling_price = EXCLUDED.selling_price RETURNING id";
        try (Connection conn = new DBConnection().getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setObject(1, dish.getId());
                ps.setString(2, dish.getName());
                ps.setString(3, dish.getDishType().name());
                ps.setObject(4, dish.getPrice());
                ps.executeQuery();

                detachIngredients(conn, dish.getId());
                attachIngredients(conn, dish.getId(), dish.getIngredients());

                conn.commit();
                return findDishById(dish.getId());
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void detachIngredients(Connection conn, Integer dishId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM DishIngredient WHERE id_dish = ?")) {
            ps.setInt(1, dishId);
            ps.executeUpdate();
        }
    }

    private void attachIngredients(Connection conn, Integer dishId, List<Ingredient> ingredients) throws SQLException {
        if (ingredients == null) return;
        String sql = "INSERT INTO DishIngredient (id_dish, id_ingredient, quantity_required, unit) VALUES (?, ?, ?, ?::unit_type)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Ingredient ing : ingredients) {
                ps.setInt(1, dishId);
                ps.setInt(2, ing.getId());
                ps.setDouble(3, ing.getQuantity() != null ? ing.getQuantity() : 0.0);
                ps.setString(4, ing.getUnit() != null ? ing.getUnit().name() : null);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
    public List<Ingredient> createIngredients(List<Ingredient> ingredients) {
        String sql = "INSERT INTO ingredient (name, category, price) VALUES (?, ?::category_type, ?) RETURNING id";
        try (Connection conn = new DBConnection().getConnection()) {
            for (Ingredient ing : ingredients) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, ing.getName());
                    ps.setString(2, ing.getCategory().name());
                    ps.setDouble(3, ing.getPrice());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            ing.setId(rs.getInt(1));
                        }
                    }
                }
            }
            return ingredients;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}