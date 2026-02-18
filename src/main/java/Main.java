import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Main {
    private static Map<Integer, String> ingredientNameCache = new HashMap<>();

    private static String getIngredientName(DataRetriever dataRetriever, int id) {
        return ingredientNameCache.computeIfAbsent(id, k -> {
            Ingredient ing = dataRetriever.findIngredientById(id);
            return ing != null ? ing.getName() : "Inconnu";
        });
    }

    public static void main(String[] args) {
        DataRetriever dataRetriever = new DataRetriever();

        System.out.println("=== Tests existants des plats ===");
        Dish saladeVerte = dataRetriever.findDishById(1);
        System.out.println(saladeVerte);

        Dish poulet = dataRetriever.findDishById(2);
        System.out.println(poulet);

        Dish rizLegume = dataRetriever.findDishById(3);
        rizLegume.setPrice(100.0);
        Dish newRizLegume = dataRetriever.saveDish(rizLegume);
        System.out.println(newRizLegume);

        System.out.println("\n=== Tests des nouvelles fonctionnalités Order ===");

        try {
            Order existingOrder = dataRetriever.findOrderByReference("CMD001");
            System.out.println("Test 0 - Commande existante trouvée: " + existingOrder);
        } catch (Exception e) {
            System.out.println("Test 0 échoué: " + e.getMessage());
        }

        try {
            Order oldOrder = dataRetriever.findOrderByReference("REF_ANCIENNE");
            System.out.println("Test 1a - Commande trouvée: " + oldOrder);
            System.out.println("  orderType: " + oldOrder.getOrderType());
            System.out.println("  status: " + oldOrder.getStatus());

            Order savedOldOrder = dataRetriever.saveOrder(oldOrder);
            System.out.println("Test 1b - Commande sauvegardée: " + savedOldOrder);
        } catch (Exception e) {
            System.out.println("Test 1 échoué: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            Order newOrder = new Order();
            newOrder.setReference("TEST123");
            newOrder.setCreationDatetime(Instant.now());
            newOrder.setOrderType(OrderType.EAT_IN);
            newOrder.setStatus(OrderStatus.CREATED);
            newOrder.setDishOrderList(new ArrayList<>());

            System.out.println("Test 2a - Tentative de sauvegarde nouvelle commande...");
            Order savedNewOrder = dataRetriever.saveOrder(newOrder);
            System.out.println("Test 2b - Nouvelle commande créée: " + savedNewOrder);
        } catch (Exception e) {
            System.out.println("Test 2 échoué: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            System.out.println("Test 3a - Recherche commande TEST123...");
            Order order = dataRetriever.findOrderByReference("TEST123");
            System.out.println("  Statut actuel: " + order.getStatus());
            order.setStatus(OrderStatus.READY);
            Order updatedOrder = dataRetriever.saveOrder(order);
            System.out.println("Test 3b - Commande mise à jour: " + updatedOrder);
            System.out.println("  Nouveau statut: " + updatedOrder.getStatus());
        } catch (Exception e) {
            System.out.println("Test 3 échoué: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== Question 1 : Test du calcul de stock (push-down) ===");
        Instant now = Instant.now();
        Integer ingredientId = 1;

        Ingredient laitue = dataRetriever.findIngredientById(ingredientId);
        StockValue stockObjet = laitue.getStockValueAt(now);
        System.out.println("Stock (objet) : " + stockObjet);

        StockValue stockDB = dataRetriever.getStockValueAt(ingredientId, now);
        System.out.println("Stock (DB)    : " + stockDB);

        if (stockObjet.getQuantity().equals(stockDB.getQuantity()) &&
                stockObjet.getUnit() == stockDB.getUnit()) {
            System.out.println("✅ Les deux approches sont cohérentes.");
        } else {
            System.out.println("❌ Différence détectée !");
        }

        System.out.println("\n=== Question 2a : Test du coût du plat (push-down) ===");
        Integer dishId = 1;
        Dish dish = dataRetriever.findDishById(dishId);
        Double costObjet = dish.getDishCost();
        Double costDB = dataRetriever.getDishCost(dishId);
        System.out.println("Coût (objet) : " + costObjet);
        System.out.println("Coût (DB)    : " + costDB);
        if (costObjet.equals(costDB)) {
            System.out.println("✅ OK");
        } else {
            System.out.println("❌ Différence");
        }

        System.out.println("\n=== Question 2b : Test de la marge du plat (push-down) ===");
        Double marginObjet = dish.getGrossMargin();
        Double marginDB = dataRetriever.getGrossMargin(dishId);
        System.out.println("Marge (objet) : " + marginObjet);
        System.out.println("Marge (DB)    : " + marginDB);
        if (marginObjet == null && marginDB == null) {
            System.out.println("✅ OK (tous deux null)");
        } else if (marginObjet != null && marginObjet.equals(marginDB)) {
            System.out.println("✅ OK");
        } else {
            System.out.println("❌ Différence");
        }

        System.out.println("\n=== Question 3 : Évolution du stock sur une période ===");
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 5);
        Map<Integer, Map<LocalDate, Double>> evolution = dataRetriever.getStockEvolution("DAY", start, end);

        System.out.println("Évolution du stock par ingrédient :");
        System.out.println("----------------------------------------------------------");

        if (evolution.isEmpty()) {
            System.out.println("Aucune donnée pour cette période.");
        } else {
            List<LocalDate> dates = new ArrayList<>(evolution.values().iterator().next().keySet());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");

            System.out.print("Ingrédient\t");
            for (LocalDate date : dates) {
                System.out.print(date.format(formatter) + "\t");
            }
            System.out.println();

            for (Map.Entry<Integer, Map<LocalDate, Double>> entry : evolution.entrySet()) {
                int id = entry.getKey();
                String name = getIngredientName(dataRetriever, id);
                System.out.print(name + " (id " + id + ")\t");
                for (LocalDate date : dates) {
                    Double stock = entry.getValue().get(date);
                    System.out.print(stock + "\t\t");
                }
                System.out.println();
            }
        }

        System.out.println("\n=== Vérification finale ===");
        String[] testRefs = {"CMD001", "CMD002", "CMD003", "REF_ANCIENNE", "TEST123"};
        for (String ref : testRefs) {
            try {
                Order o = dataRetriever.findOrderByReference(ref);
                System.out.println("✓ " + ref + " - Type: " + o.getOrderType() + ", Status: " + o.getStatus());
            } catch (Exception e) {
                System.out.println("✗ " + ref + " non trouvée");
            }
        }
    }
}