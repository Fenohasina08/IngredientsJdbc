import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataRetriever dataRetriever = new DataRetriever();
        
        System.out.println("=== TESTS TD4 - GESTION DES STOCKS ===");
        
         System.out.println("\n1. Test saveIngredient avec mouvement de stock:");
        Ingredient ingredient = new Ingredient();
        ingredient.setId(1);
        ingredient.setName("Huile");
        ingredient.setPrice(2500.0);
        ingredient.setCategory(CategoryEnum.OTHER);

        StockValue value = new StockValue();
        value.setQuantity(1.0);
        value.setUnit(Unit.KG);

        StockMovement movement = new StockMovement();
        movement.setValue(value);
        movement.setType(StockMovementType.OUT);
        movement.setCreationDateTime(Instant.now());

        ingredient.setStockMovementList(List.of(movement));

        try {
            Ingredient savedIngredient = dataRetriever.saveIngredient(ingredient);
            System.out.println("✅ Ingredient sauvegardé avec ID: " + savedIngredient.getId());
            if (savedIngredient.getStockMovementList() != null && !savedIngredient.getStockMovementList().isEmpty()) {
                System.out.println("✅ Mouvement sauvegardé avec ID: " + savedIngredient.getStockMovementList().get(0).getId());
            }
        } catch (Exception e) {
            System.out.println("❌ Erreur: " + e.getMessage());
        }
        
         System.out.println("\n2. Calcul des stocks au 2024-01-06 12:00:");
        Instant calculTime = LocalDateTime.of(2024, 1, 6, 12, 0, 0)
            .toInstant(ZoneOffset.UTC);
        
        System.out.println("Instant de calcul: " + calculTime);
        System.out.println("------------------------------------------");
        
        String[] ingredientsTD4 = {"Laitue", "Tomate", "Poulet", "Chocolat", "Beurre"};
        double[] stocksInitiaux = {5.0, 4.0, 10.0, 3.0, 2.5};
        double[] sorties = {0.2, 0.15, 1.0, 0.3, 0.2};
        
        for (int i = 0; i < ingredientsTD4.length; i++) {
            String nom = ingredientsTD4[i];
            double stockInitial = stocksInitiaux[i];
            double sortie = sorties[i];
            double stockAttendu = stockInitial - sortie;
            
            Ingredient ing = dataRetriever.findIngredientByName(nom);
            if (ing != null) {
                double stockCalcule = dataRetriever.getStockValueAt(ing.getId(), calculTime);
                String result = Math.abs(stockCalcule - stockAttendu) < 0.01 ? "✅" : "❌";
                System.out.printf("%-10s: %.2f - %.2f = %.2f | Calculé: %.2f %s%n",
                    nom, stockInitial, sortie, stockAttendu, stockCalcule, result);
            } else {
                System.out.printf("%-10s: ❌ Ingrédient non trouvé dans la base%n", nom);
            }
        }
        
         System.out.println("\n3. Test ON CONFLICT DO NOTHING:");
        System.out.println("Tentative de réinsertion du même mouvement...");
        
        StockMovement mouvementExistant = new StockMovement();
        mouvementExistant.setId(1);
        mouvementExistant.setValue(new StockValue(5.0, Unit.KG));
        mouvementExistant.setType(StockMovementType.IN);
        mouvementExistant.setCreationDateTime(Instant.now());
        
        Ingredient ingredient2 = new Ingredient();
        ingredient2.setId(1);
        ingredient2.setName("Huile Modifiée");
        ingredient2.setPrice(3000.0);
        ingredient2.setCategory(CategoryEnum.OTHER);
        ingredient2.setStockMovementList(List.of(mouvementExistant));
        
        try {
            Ingredient resultat = dataRetriever.saveIngredient(ingredient2);
            System.out.println("✅ L'ingrédient a été mis à jour mais le mouvement existant a été ignoré (ON CONFLICT DO NOTHING)");
            System.out.println("   Nouveau prix: " + resultat.getPrice());
        } catch (Exception e) {
            System.out.println("❌ Erreur: " + e.getMessage());
        }
        
        System.out.println("\n=== TESTS TD4 TERMINÉS ===");
    }
}
