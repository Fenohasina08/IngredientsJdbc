import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    Dish findDishById(Integer id) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    """
                            SELECT id, name, dish_type, selling_price
                            FROM dish
                            WHERE id = ?;
                            """);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Dish dish = new Dish();
                // Utilisation de getObject pour l'ID et le prix (nullable)
                dish.setId((Integer) resultSet.getObject("id"));
                dish.setName(resultSet.getString("name"));
                dish.setDishType(DishTypeEnum.valueOf(resultSet.getString("dish_type")));
                dish.setPrice((Double) resultSet.getObject("selling_price"));

                dish.setIngredients(findIngredientByDishId(id));
                return dish;
            }
            throw new RuntimeException("Dish not found " + id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(connection);
        }
    }

    Dish saveDish(Dish toSave) {
        String upsertDishSql = """
                    INSERT INTO dish (id, name, dish_type, selling_price)
                    VALUES (?, ?, ?::dish_type, ?)
                    ON CONFLICT (id) DO UPDATE
                    SET name = EXCLUDED.name,
                        dish_type = EXCLUDED.dish_type,
                        selling_price = EXCLUDED.selling_price
                    RETURNING id
                """;

        try (Connection conn = new DBConnection().getConnection()) {
            conn.setAutoCommit(false);
            Integer dishId;
            try (PreparedStatement ps = conn.prepareStatement(upsertDishSql)) {
                ps.setObject(1, toSave.getId() != null ? toSave.getId() : getNextSerialValue(conn, "dish", "id"));
                ps.setString(2, toSave.getName());
                ps.setString(3, toSave.getDishType().name());
                ps.setObject(4, toSave.getPrice()); // setObject gère automatiquement le null

                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    dishId = rs.getInt(1);
                }
            }

            detachIngredients(conn, dishId);
            attachIngredients(conn, dishId, toSave.getIngredients());

            conn.commit();
            return findDishById(dishId);
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

    private void attachIngredients(Connection conn, Integer dishId, List<Ingredient> ingredients)
            throws SQLException {
        if (ingredients == null || ingredients.isEmpty()) return;

        String attachSql = "INSERT INTO DishIngredient (id_dish, id_ingredient, quantity_required, unit) VALUES (?, ?, ?, ?::unit_type)";
        try (PreparedStatement ps = conn.prepareStatement(attachSql)) {
            for (Ingredient ingredient : ingredients) {
                ps.setInt(1, dishId);
                ps.setInt(2, ingredient.getId());
                ps.setObject(3, ingredient.getQuantity()); // Nullable
                ps.setString(4, ingredient.getUnit() != null ? ingredient.getUnit().name() : null);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private List<Ingredient> findIngredientByDishId(Integer idDish) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        List<Ingredient> ingredients = new ArrayList<>();
        try {
            String sql = """
                    SELECT i.id, i.name, i.price, i.category, di.quantity_required, di.unit
                    FROM ingredient i
                    JOIN DishIngredient di ON i.id = di.id_ingredient
                    WHERE di.id_dish = ?;
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, idDish);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId((Integer) resultSet.getObject("id"));
                ingredient.setName(resultSet.getString("name"));
                ingredient.setPrice((Double) resultSet.getObject("price"));
                ingredient.setCategory(CategoryEnum.valueOf(resultSet.getString("category")));
                ingredient.setQuantity((Double) resultSet.getObject("quantity_required"));

                String unitStr = resultSet.getString("unit");
                if (unitStr != null) {
                    ingredient.setUnit(UnitEnum.valueOf(unitStr));
                }
                ingredients.add(ingredient);
            }
            return ingredients;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(connection);
        }
    }

    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) {
        if (newIngredients == null || newIngredients.isEmpty()) return List.of();

        DBConnection dbConnection = new DBConnection();
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            String insertSql = "INSERT INTO ingredient (id, name, category, price) VALUES (?, ?, ?::ingredient_category, ?) RETURNING id";
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                for (Ingredient ingredient : newIngredients) {
                    ps.setObject(1, ingredient.getId() != null ? ingredient.getId() : getNextSerialValue(conn, "ingredient", "id"));
                    ps.setString(2, ingredient.getName());
                    ps.setString(3, ingredient.getCategory().name());
                    ps.setObject(4, ingredient.getPrice());

                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        ingredient.setId(rs.getInt(1));
                    }
                }
                conn.commit();
                return newIngredients;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // --- Méthodes utilitaires pour les séquences ---

    private String getSerialSequenceName(Connection conn, String tableName, String columnName) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT pg_get_serial_sequence(?, ?)")) {
            ps.setString(1, tableName);
            ps.setString(2, columnName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    private int getNextSerialValue(Connection conn, String tableName, String columnName) throws SQLException {
        String sequenceName = getSerialSequenceName(conn, tableName, columnName);
        updateSequenceNextValue(conn, tableName, columnName, sequenceName);
        try (PreparedStatement ps = conn.prepareStatement("SELECT nextval(?)")) {
            ps.setString(1, sequenceName);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private void updateSequenceNextValue(Connection conn, String tableName, String columnName, String sequenceName) throws SQLException {
        String setValSql = String.format("SELECT setval('%s', (SELECT COALESCE(MAX(%s), 0) FROM %s))",
                sequenceName, columnName, tableName);
        try (PreparedStatement ps = conn.prepareStatement(setValSql)) {
            ps.executeQuery();
        }
    }
}