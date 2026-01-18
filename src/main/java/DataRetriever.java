import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    // --- RÉCUPÉRATION (FIND) ---

    /**
     * Récupère un plat par son ID, incluant ses ingrédients grâce à une jointure.
     */
    public Dish findDishById(Integer id) {
        DBConnection dbConnection = new DBConnection();
        try (Connection connection = dbConnection.getConnection()) {
            String sql = "SELECT id, name, dish_type, selling_price FROM dish WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, id);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        Dish dish = new Dish();
                        dish.setId((Integer) resultSet.getObject("id"));
                        dish.setName(resultSet.getString("name"));
                        dish.setDishType(DishTypeEnum.valueOf(resultSet.getString("dish_type")));
                        dish.setPrice((Double) resultSet.getObject("selling_price"));

                        // On récupère les ingrédients via la table de jointure
                        dish.setIngredients(findIngredientByDishId(id));
                        return dish;
                    }
                }
            }
            throw new RuntimeException("Dish not found: " + id);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding dish", e);
        }
    }

    /**
     * Récupère la liste des ingrédients d'un plat via la table de jointure DishIngredient.
     */
    private List<Ingredient> findIngredientByDishId(Integer idDish) {
        List<Ingredient> ingredients = new ArrayList<>();
        DBConnection dbConnection = new DBConnection();
        try (Connection connection = dbConnection.getConnection()) {
            String sql = """
                    SELECT i.id, i.name, i.price, i.category, di.quantity_required, di.unit
                    FROM ingredient i
                    JOIN DishIngredient di ON i.id = di.id_ingredient
                    WHERE di.id_dish = ?;
                    """;
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, idDish);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
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
                }
            }
            return ingredients;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding ingredients for dish", e);
        }
    }

    // --- SAUVEGARDE (SAVE) ---

    /**
     * Sauvegarde un plat et synchronise tous ses ingrédients.
     */
    public Dish saveDish(Dish toSave) {
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
            try {
                try (PreparedStatement ps = conn.prepareStatement(upsertDishSql)) {
                    ps.setObject(1, toSave.getId() != null ? toSave.getId() : getNextSerialValue(conn, "dish", "id"));
                    ps.setString(2, toSave.getName());
                    ps.setString(3, toSave.getDishType().name());
                    ps.setObject(4, toSave.getPrice());

                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        dishId = rs.getInt(1);
                    }
                }

                // Synchronisation des ingrédients (nouvelle entité DishIngredient)
                detachIngredients(conn, dishId);
                attachIngredients(conn, dishId, toSave.getIngredients());

                conn.commit();
                return findDishById(dishId);
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving dish", e);
        }
    }

    /**
     * Méthode pour la nouvelle entité DishIngredient (Lien spécifique).
     * Elle permet de sauvegarder ou mettre à jour un lien unique.
     */
    public void saveDishIngredient(Integer dishId, Integer ingredientId, Double quantity, UnitEnum unit) {
        String sql = """
                INSERT INTO DishIngredient (id_dish, id_ingredient, quantity_required, unit)
                VALUES (?, ?, ?, ?::unit_type)
                ON CONFLICT (id_dish, id_ingredient)
                DO UPDATE SET
                    quantity_required = EXCLUDED.quantity_required,
                    unit = EXCLUDED.unit;
                """;

        DBConnection dbConnection = new DBConnection();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dishId);
            ps.setInt(2, ingredientId);
            ps.setObject(3, quantity);
            ps.setString(4, unit != null ? unit.name() : null);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving link between dish and ingredient", e);
        }
    }

    // --- MÉTHODES PRIVÉES DE LIAISON ---

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
                ps.setObject(3, ingredient.getQuantity());
                ps.setString(4, ingredient.getUnit() != null ? ingredient.getUnit().name() : null);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // --- UTILITAIRES SÉQUENCES (inchangés) ---

    private int getNextSerialValue(Connection conn, String tableName, String columnName) throws SQLException {
        String sequenceName;
        try (PreparedStatement ps = conn.prepareStatement("SELECT pg_get_serial_sequence(?, ?)")) {
            ps.setString(1, tableName);
            ps.setString(2, columnName);
            try (ResultSet rs = ps.executeQuery()) {
                sequenceName = rs.next() ? rs.getString(1) : null;
            }
        }
        if (sequenceName == null) throw new SQLException("Sequence not found");

        // Mise à jour pour éviter les conflits d'ID
        String setValSql = String.format("SELECT setval('%s', (SELECT COALESCE(MAX(%s), 0) FROM %s))",
                sequenceName, columnName, tableName);
        try (Statement st = conn.createStatement()) { st.executeQuery(setValSql); }

        try (PreparedStatement ps = conn.prepareStatement("SELECT nextval(?)")) {
            ps.setString(1, sequenceName);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

}