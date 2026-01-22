import Connection.*;
import classes.*;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        try (Connection c = DBConnection.getDBConnection()) {
            DataRetriever dt = new DataRetriever(c);
            System.out.println("--- RESULTATS TD3 (Calcul des Marges) ---");
            System.out.println("Marge Salade fraîche (ID 1) : " + dt.getGrossMargin(1));
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}
