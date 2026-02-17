 package org.example;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    /* ============================
           1) findDishById
           ============================ */
    public Dish findDishById(Integer id) {
        String sql = """
        SELECT d.id, d.name, d.dish_type,
               i.id as ingredient_id, i.name as ingredient_name,
               i.price, i.category
        FROM dish d
        LEFT JOIN dish_ingredient di ON di.dish_id = d.id
        LEFT JOIN ingredient i ON i.id = di.ingredient_id
        WHERE d.id = ?
    """;

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            Dish dish = null;
            List<Ingredient> ingredients = new ArrayList<>();

            while (rs.next()) {
                if (dish == null) {
                    dish = new Dish();
                    dish.setId(rs.getInt("id"));
                    dish.setName(rs.getString("name"));
                    dish.setDishTypeEnum(DishTypeEnum.valueOf(rs.getString("dish_type")));
                }

                int ingredientId = rs.getInt("ingredient_id");
                if (!rs.wasNull()) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setId(ingredientId);
                    ingredient.setName(rs.getString("ingredient_name"));
                    ingredient.setPrice(rs.getDouble("price"));
                    ingredient.setCategorieEnum(CategorieEnum.valueOf(rs.getString("category")));
                    ingredients.add(ingredient);
                }
            }

            if (dish == null) {
                throw new RuntimeException("Plat non trouvé");
            }

            dish.setIngredients(ingredients);
            return dish;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /* ============================
       2) findIngredients (pagination)
       ============================ */
    public List<Ingredient> findIngredients(int page, int size) {

        String sql = """
            SELECT id, name, price, category
            FROM ingredient
            ORDER BY id
            LIMIT ? OFFSET ?
        """;

        int offset = (page - 1) * size;
        List<Ingredient> ingredients = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, size);
            ps.setInt(2, offset);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Ingredient i = new Ingredient();
                i.setId(rs.getInt("id"));
                i.setName(rs.getString("name"));
                i.setPrice(rs.getDouble("price"));
                i.setCategorieEnum(CategorieEnum.valueOf(rs.getString("category")));
                ingredients.add(i);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return ingredients;
    }

    /* ============================
       3) createIngredients (atomicité)
       ============================ */
    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) {

        String checkSql = "SELECT COUNT(*) FROM ingredient WHERE name = ?";
        String insertSql = """
            INSERT INTO ingredient(name, price, category)
            VALUES (?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            for (Ingredient i : newIngredients) {
                try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                    check.setString(1, i.getName());
                    ResultSet rs = check.executeQuery();
                    rs.next();

                    if (rs.getInt(1) > 0) {
                        throw new RuntimeException("Ingredient already exists: " + i.getName());
                    }
                }

                try (PreparedStatement insert = conn.prepareStatement(insertSql)) {
                    insert.setString(1, i.getName());
                    insert.setDouble(2, i.getPrice());
                    insert.setString(3, i.getCategorieEnum().name());
                    insert.executeUpdate();
                }
            }

            conn.commit();
            return newIngredients;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* ============================
       4) saveDish
       ============================ */
    public Ingredient findIngredientByName(String name) {
        String sql = "SELECT id, name, price, category, id_dish FROM ingredient WHERE name = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(rs.getInt("id"));
                ingredient.setName(rs.getString("name"));
                ingredient.setPrice(rs.getDouble("price"));
                ingredient.setCategorieEnum(CategorieEnum.valueOf(rs.getString("category")));
                return ingredient;
            } else {
                throw new RuntimeException("Ingredient non trouvé : " + name);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Dish saveDish(Dish dish) {

        String insertDishSql = """
            INSERT INTO dish(name, dish_type)
            VALUES (?, ?)
            RETURNING id
        """;

        String updateDishSql = """
            UPDATE dish SET name = ?, dish_type = ?
            WHERE id = ?
        """;

        String clearIngredientsSql = """
            UPDATE ingredient SET id_dish = NULL
            WHERE id_dish = ?
        """;

        String attachIngredientSql = """
            UPDATE ingredient SET id_dish = ?
            WHERE id = ?
        """;

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            if (dish.getId() == null) {
                try (PreparedStatement ps = conn.prepareStatement(insertDishSql)) {
                    ps.setString(1, dish.getName());
                    ps.setString(2, dish.getDishTypeEnum().name());

                    ResultSet rs = ps.executeQuery();
                    rs.next();
                    dish.setId(rs.getInt(1));
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement(updateDishSql)) {
                    ps.setString(1, dish.getName());
                    ps.setString(2, dish.getDishTypeEnum().name());
                    ps.setInt(3, dish.getId());
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(clearIngredientsSql)) {
                    ps.setInt(1, dish.getId());
                    ps.executeUpdate();
                }
            }

            if (dish.getIngredients() != null) {
                for (Ingredient i : dish.getIngredients()) {
                    try (PreparedStatement ps = conn.prepareStatement(attachIngredientSql)) {
                        ps.setInt(1, dish.getId());
                        ps.setInt(2, i.getId());
                        ps.executeUpdate();
                    }
                }
            }

            conn.commit();
            return dish;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /* ============================
       5) findDishsByIngredientName
       ============================ */
    public List<Dish> findDishsByIngredientName(String ingredientName) {

        String sql = """
            SELECT DISTINCT d.id, d.name, d.dish_type
            FROM dish d
            JOIN ingredient i ON d.id = i.id_dish
            WHERE LOWER(i.name) LIKE LOWER(?)
        """;

        List<Dish> dishes = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + ingredientName + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Dish d = new Dish();
                d.setId(rs.getInt("id"));
                d.setName(rs.getString("name"));
                d.setDishTypeEnum(DishTypeEnum.valueOf(rs.getString("dish_type")));
                dishes.add(d);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return dishes;
    }

    /* ============================
       TD4 - Gestion des stocks
       ============================ */

    public Ingredient saveIngredient(Ingredient toSave) {
        String saveIngredientSql = """
        INSERT INTO ingredient(name, price, category)
        VALUES (?, ?, ?)
        ON CONFLICT (id) DO UPDATE 
        SET name = EXCLUDED.name, 
            price = EXCLUDED.price, 
            category = EXCLUDED.category
        RETURNING id
    """;

        String saveStockMovementSql = """
        INSERT INTO stock_movement(id, id_ingredient, quantity, type, unit, creation_datetime)
        VALUES (?, ?, ?, ?, ?, ?)
        ON CONFLICT (id) DO NOTHING
    """;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(saveIngredientSql)) {
                ps.setString(1, toSave.getName());
                ps.setDouble(2, toSave.getPrice());
                ps.setString(3, toSave.getCategorieEnum().name());

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    toSave.setId(rs.getInt("id"));
                }
            }

            if (toSave.getStockMovementList() != null) {
                for (StockMovement movement : toSave.getStockMovementList()) {
                    try (PreparedStatement ps = conn.prepareStatement(saveStockMovementSql)) {
                        ps.setInt(1, movement.getId());
                        ps.setInt(2, toSave.getId());
                        ps.setDouble(3, movement.getQuantity());
                        ps.setString(4, "OUT");
                        ps.setString(5, movement.getUnit());
                        ps.setTimestamp(6, Timestamp.valueOf(movement.getMovementDate()));
                        ps.executeUpdate();
                    }
                }
            }

            conn.commit();
            return toSave;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde de l'ingrédient", e);
        }
    }

    public double getStockValueAt(Integer ingredientId, Instant t) {
        String sql = """
        WITH initial AS (
            SELECT quantity 
            FROM initial_stock 
            WHERE id_ingredient = ?
        ),
        movements AS (
            SELECT 
                COALESCE(SUM(CASE WHEN type = 'IN' THEN quantity ELSE -quantity END), 0) as total_change
            FROM stock_movement 
            WHERE id_ingredient = ? 
            AND creation_datetime <= ?
        )
        SELECT 
            COALESCE((SELECT quantity FROM initial), 0) 
            + COALESCE((SELECT total_change FROM movements), 0) as current_stock
    """;

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, ingredientId);
            ps.setInt(2, ingredientId);
            ps.setTimestamp(3, Timestamp.from(t));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("current_stock");
            }
            return 0.0;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du calcul du stock", e);
        }
    }

    public List<StockMovement> getStockMovementsForIngredient(Integer ingredientId) {
        String sql = """
        SELECT sm.id, sm.quantity, sm.type, sm.unit, sm.creation_datetime,
               i.id as ingredient_id, i.name as ingredient_name
        FROM stock_movement sm
        JOIN ingredient i ON sm.id_ingredient = i.id
        WHERE sm.id_ingredient = ?
        ORDER BY sm.creation_datetime DESC
    """;

        List<StockMovement> movements = new ArrayList<>();

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, ingredientId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // Créer un Ingredient d'abord
                Ingredient ingredient = new Ingredient();
                ingredient.setId(rs.getInt("ingredient_id"));
                ingredient.setName(rs.getString("ingredient_name"));

                // Créer le StockMovement avec tous les arguments
                StockMovement movement = new StockMovement();
                movement.setId(rs.getInt("id"));
                movement.setIngredient(ingredient);
                movement.setQuantity(rs.getDouble("quantity"));
                movement.setUnit(rs.getString("unit"));
                movement.setMovementDate(rs.getTimestamp("creation_datetime").toLocalDateTime());

                movements.add(movement);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des mouvements", e);
        }

        return movements;
    }

    /* ============================
       ANNEXE 2 - Gestion des commandes
       ============================ */

    public Order saveOrder(Order orderToSave) {
        if (orderToSave.getDishOrders() != null) {
            for (DishOrder dishOrder : orderToSave.getDishOrders()) {
                Dish dish = findDishById(dishOrder.getDish().getId());
                checkStockForDish(dish, dishOrder.getQuantity());
            }
        }

        if (orderToSave.getTable() == null) {
            throw new RuntimeException("Une table doit être spécifiée pour la commande");
        }

        if (orderToSave.getArrivalDatetime() == null || orderToSave.getDepartureDatetime() == null) {
            throw new RuntimeException("Les dates d'arrivée et de départ doivent être spécifiées");
        }

        boolean isTableAvailable = isTableAvailable(
                orderToSave.getTable().getId(),
                orderToSave.getArrivalDatetime(),
                orderToSave.getDepartureDatetime()
        );

        if (!isTableAvailable) {
            List<RestaurantTable> availableTables = getAvailableTables(
                    orderToSave.getArrivalDatetime(),
                    orderToSave.getDepartureDatetime()
            );

            if (availableTables.isEmpty()) {
                throw new RuntimeException(
                        "La table " + orderToSave.getTable().getNumber() +
                                " n'est pas disponible et aucune autre table n'est disponible pour cette plage horaire."
                );
            } else {
                StringBuilder availableTableNumbers = new StringBuilder();
                for (RestaurantTable table : availableTables) {
                    availableTableNumbers.append(table.getNumber()).append(", ");
                }
                if (availableTableNumbers.length() > 0) {
                    availableTableNumbers.setLength(availableTableNumbers.length() - 2);
                }

                throw new RuntimeException(
                        "La table " + orderToSave.getTable().getNumber() +
                                " n'est pas disponible. Tables disponibles: " + availableTableNumbers
                );
            }
        }

        String insertOrderSql = """
        INSERT INTO "order" (total_ht, total_ttc, creation_datetime, 
                            id_table, arrival_datetime, departure_datetime)
        VALUES (?, ?, ?, ?, ?, ?)
        RETURNING id, reference, creation_datetime
    """;

        String insertDishOrderSql = """
        INSERT INTO dish_order (id_order, id_dish, quantity)
        VALUES (?, ?, ?)
    """;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(insertOrderSql)) {
                ps.setDouble(1, orderToSave.getTotalHt());
                ps.setDouble(2, orderToSave.getTotalTtc());
                ps.setTimestamp(3, Timestamp.valueOf(orderToSave.getCreationDatetime()));
                ps.setInt(4, orderToSave.getTable().getId());
                ps.setTimestamp(5, Timestamp.valueOf(orderToSave.getArrivalDatetime()));
                ps.setTimestamp(6, Timestamp.valueOf(orderToSave.getDepartureDatetime()));

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    orderToSave.setId(rs.getInt("id"));
                    orderToSave.setReference(rs.getString("reference"));
                    orderToSave.setCreationDatetime(rs.getTimestamp("creation_datetime").toLocalDateTime());
                }
            }

            if (orderToSave.getDishOrders() != null) {
                for (DishOrder dishOrder : orderToSave.getDishOrders()) {
                    try (PreparedStatement ps = conn.prepareStatement(insertDishOrderSql)) {
                        ps.setInt(1, orderToSave.getId());
                        ps.setInt(2, dishOrder.getDish().getId());
                        ps.setInt(3, dishOrder.getQuantity());
                        ps.executeUpdate();
                    }
                }
            }

            conn.commit();
            return orderToSave;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde de la commande", e);
        }
    }

    private void checkStockForDish(Dish dish, int quantity) {
        if (dish.getIngredients() != null) {
            for (Ingredient ingredient : dish.getIngredients()) {
                double currentStock = getStockValueAt(ingredient.getId(), Instant.now());
                double requiredForOneDish = 1.0;
                double totalRequired = requiredForOneDish * quantity;

                if (currentStock < totalRequired) {
                    throw new RuntimeException(
                            "Stock insuffisant pour l'ingrédient: " + ingredient.getName() +
                                    ". Stock actuel: " + currentStock +
                                    ", Nécessaire: " + totalRequired
                    );
                }
            }
        }
    }

    public Order findOrderByReference(String reference) {
        String sql = """
        SELECT o.id, o.reference, o.total_ht, o.total_ttc, o.creation_datetime,
               o.id_table, o.arrival_datetime, o.departure_datetime,
               rt.id as table_id, rt.number as table_number,
               do.id as do_id, do.quantity,
               d.id as dish_id, d.name as dish_name, d.dish_type
        FROM "order" o
        LEFT JOIN restaurant_table rt ON o.id_table = rt.id
        LEFT JOIN dish_order do ON o.id = do.id_order
        LEFT JOIN dish d ON do.id_dish = d.id
        WHERE o.reference = ?
        ORDER BY do.id
    """;

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, reference);
            ResultSet rs = ps.executeQuery();

            Order order = null;
            List<DishOrder> dishOrders = new ArrayList<>();

            while (rs.next()) {
                if (order == null) {
                    order = new Order();
                    order.setId(rs.getInt("id"));
                    order.setReference(rs.getString("reference"));
                    order.setTotalHt(rs.getDouble("total_ht"));
                    order.setTotalTtc(rs.getDouble("total_ttc"));
                    order.setCreationDatetime(rs.getTimestamp("creation_datetime").toLocalDateTime());

                    if (rs.getInt("id_table") != 0) {
                        RestaurantTable table = new RestaurantTable();
                        table.setId(rs.getInt("table_id"));
                        table.setNumber(rs.getInt("table_number"));
                        order.setTable(table);

                        Timestamp arrivalTimestamp = rs.getTimestamp("arrival_datetime");
                        Timestamp departureTimestamp = rs.getTimestamp("departure_datetime");

                        if (arrivalTimestamp != null) {
                            order.setArrivalDatetime(arrivalTimestamp.toLocalDateTime());
                        }
                        if (departureTimestamp != null) {
                            order.setDepartureDatetime(departureTimestamp.toLocalDateTime());
                        }
                    }
                }

                if (rs.getInt("do_id") != 0) {
                    Dish dish = new Dish();
                    dish.setId(rs.getInt("dish_id"));
                    dish.setName(rs.getString("dish_name"));
                    dish.setDishTypeEnum(DishTypeEnum.valueOf(rs.getString("dish_type")));

                    DishOrder dishOrder = new DishOrder();
                    dishOrder.setId(rs.getInt("do_id"));
                    dishOrder.setDish(dish);
                    dishOrder.setQuantity(rs.getInt("quantity"));

                    dishOrders.add(dishOrder);
                }
            }

            if (order == null) {
                throw new RuntimeException("Commande introuvable avec la référence: " + reference);
            }

            order.setDishOrders(dishOrders);
            return order;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la commande", e);
        }
    }

    public double calculateOrderTotal(List<DishOrder> dishOrders) {
        double total = 0.0;

        for (DishOrder dishOrder : dishOrders) {
            Dish dish = findDishById(dishOrder.getDish().getId());
            int quantity = dishOrder.getQuantity();
            total += (dish.getPrice() != null ? dish.getPrice() : 0) * quantity;
        }

        return total;
    }

    /* ============================
       Gestion des tables
       ============================ */

    public boolean isTableAvailable(Integer tableId, LocalDateTime arrivalTime, LocalDateTime departureTime) {
        String sql = """
        SELECT COUNT(*) as overlapping_orders
        FROM "order"
        WHERE id_table = ?
        AND (
            (arrival_datetime <= ? AND departure_datetime >= ?) OR
            (arrival_datetime <= ? AND departure_datetime >= ?) OR
            (? <= arrival_datetime AND ? >= departure_datetime)
        )
    """;

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, tableId);
            ps.setTimestamp(2, Timestamp.valueOf(departureTime));
            ps.setTimestamp(3, Timestamp.valueOf(arrivalTime));
            ps.setTimestamp(4, Timestamp.valueOf(arrivalTime));
            ps.setTimestamp(5, Timestamp.valueOf(departureTime));
            ps.setTimestamp(6, Timestamp.valueOf(arrivalTime));
            ps.setTimestamp(7, Timestamp.valueOf(departureTime));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("overlapping_orders") == 0;
            }
            return true;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la vérification de disponibilité de la table", e);
        }
    }

    public List<RestaurantTable> getAvailableTables(LocalDateTime arrivalTime, LocalDateTime departureTime) {
        String sql = """
        SELECT rt.id, rt.number
        FROM restaurant_table rt
        WHERE rt.id NOT IN (
            SELECT o.id_table
            FROM "order" o
            WHERE (
                (o.arrival_datetime <= ? AND o.departure_datetime >= ?) OR
                (o.arrival_datetime <= ? AND o.departure_datetime >= ?) OR
                (? <= o.arrival_datetime AND ? >= o.departure_datetime)
            )
            AND o.id_table IS NOT NULL
        )
        ORDER BY rt.number
    """;

        List<RestaurantTable> availableTables = new ArrayList<>();

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(departureTime));
            ps.setTimestamp(2, Timestamp.valueOf(arrivalTime));
            ps.setTimestamp(3, Timestamp.valueOf(arrivalTime));
            ps.setTimestamp(4, Timestamp.valueOf(departureTime));
            ps.setTimestamp(5, Timestamp.valueOf(arrivalTime));
            ps.setTimestamp(6, Timestamp.valueOf(departureTime));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RestaurantTable table = new RestaurantTable();
                table.setId(rs.getInt("id"));
                table.setNumber(rs.getInt("number"));
                availableTables.add(table);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des tables disponibles", e);
        }

        return availableTables;
    }

    public RestaurantTable findTableByNumber(Integer tableNumber) {
        String sql = "SELECT id, number FROM restaurant_table WHERE number = ?";

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, tableNumber);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                RestaurantTable table = new RestaurantTable();
                table.setId(rs.getInt("id"));
                table.setNumber(rs.getInt("number"));
                return table;
            } else {
                throw new RuntimeException("Table non trouvée avec le numéro: " + tableNumber);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la table", e);
        }
    }

    public List<RestaurantTable> getAvailableTablesAtDateTime(LocalDateTime dateTime) {
        LocalDateTime arrivalTime = dateTime;
        LocalDateTime departureTime = dateTime.plusHours(2);
        return getAvailableTables(arrivalTime, departureTime);
    }

    /* ============================
       Méthodes supplémentaires
       ============================ */

    public Ingredient findIngredientById(Integer idIngredient) {
        String sql = "SELECT id, name, price, category FROM ingredient WHERE id = ?";

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, idIngredient);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(rs.getInt("id"));
                ingredient.setName(rs.getString("name"));
                ingredient.setPrice(rs.getDouble("price"));
                ingredient.setCategorieEnum(CategorieEnum.valueOf(rs.getString("category")));
                return ingredient;
            } else {
                throw new RuntimeException("Ingrédient non trouvé avec l'ID: " + idIngredient);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de l'ingrédient", e);
        }
    }
}
