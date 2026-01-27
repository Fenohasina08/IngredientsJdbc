package classes;

import Connection.DBConnection;
import Connection.DataRetriever;
import java.sql.Connection;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try (Connection connection = DBConnection.getDBConnection()) {
            DataRetriever retriever = new DataRetriever(connection);

            // 1. Tester le calcul de marge (Salade Fraîche - ID 1)
            double marge = retriever.getGrossMargin(1);
            System.out.println("--- MARGE TD3 ---");
            System.out.println("Marge pour Salade fraîche : " + marge + " Ar");

            // 2. Tester la recherche par ingrédient (Tomate - ID 2)
            System.out.println("\n--- RECHERCHE PAR INGRÉDIENT ---");
            List<Dish> platsAvecTomate = retriever.findDishByIngredient(2);
            for (Dish d : platsAvecTomate) {
                System.out.println("Plat contenant de la tomate : " + d.getName());
            }

            // 3. Tester la sauvegarde d'un nouveau plat
            System.out.println("\n--- SAUVEGARDE NOUVEAU PLAT ---");
            Dish pizza = new Dish("Pizza Margherita", DishType.MAIN, 25000.0);
            retriever.saveDish(pizza);
            System.out.println("Nouveau plat enregistré avec l'ID : " + pizza.getId());

        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}