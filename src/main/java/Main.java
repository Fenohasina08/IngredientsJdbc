import java.time.Instant;
import java.util.ArrayList;

public class Main {
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