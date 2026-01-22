import Connection.DataRetriever;
import Connection.DBConnection;
import classes.*;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        // On appelle la méthode statique de la classe DBConnection du package Connection
        try (Connection c = DBConnection.getDBConnection()) {
            DataRetriever dt = new DataRetriever(c);
            System.out.println("--- RESULTATS TD3 ---");
            
            int[] ids = {1, 2, 4}; 
            for (int id : ids) {
                Dish d = dt.findDishById(id);
                if (d != null) {
                    System.out.println("\nPlat: " + d.getName());
                    System.out.printf(" > Coût de revient: %.2f\n", dt.getDishCost(id));
                    try {
                        System.out.printf(" > Marge brute: %.2f\n", dt.getGrossMargin(id));
                    } catch (Exception e) {
                        System.out.println(" > Marge: Erreur - " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur fatale : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
