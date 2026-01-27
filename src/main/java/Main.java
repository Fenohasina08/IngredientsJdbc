import java.time.Instant;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        DataRetriever dataRetriever = new DataRetriever();

//        System.out.println("------------- Test A -------------");
//        Dish dishA = dataRetriever.findDishById(3);
//        if (dishA != null) {
//            dishA.prettyPrint();
//        } else {
//            System.out.println("⚠️ Plat non trouvé !");
//        }
//
//        System.out.println("------------- Test B -------------");
//        Dish dishB = dataRetriever.findDishById(2);
//        if (dishB != null) {
//            dishB.prettyPrint();
//        } else {
//            System.out.println("⚠️ Plat non trouvé !");
//        }
//
//
//        System.out.println("------------- Test C -------------");
//        List<Ingredient> ingredientsC = dataRetriever.findIngredients(1, 2);
//        printIngredients(ingredientsC);
//
//
//        System.out.println("------------- Test D -------------");
//        List<Ingredient> ingredientsD = dataRetriever.findIngredients(3, 5);
//        printIngredients(ingredientsD);
//
//
//        System.out.println("------------- Test E -------------");
//        List<Dish> dishesE = dataRetriever.findDishsByIngredientName("laitue");
//        printDishes(dishesE);
//
//
//        System.out.println("------------- Test F -------------");
//        List<Ingredient> ingredientsF = dataRetriever.findIngredientsByCriteria(null, CategoryEnum.VEGETABLE, null, 1, 10);
//        printIngredients(ingredientsF);
//
//
//        System.out.println("------------- Test G -------------");
//        List<Ingredient> ingredientsG = dataRetriever.findIngredientsByCriteria("cho", null, "Sal", 1, 10);
//        printIngredients(ingredientsG);
//
//        System.out.println("------------- Test H -------------");
//        List<Ingredient> ingredientsH = dataRetriever.findIngredientsByCriteria("cho", null, "gâteau", 1, 10);
//        printIngredients(ingredientsH);
//
//        System.out.println("---Test I ---");
//        List<Ingredient> newIngredients1 = List.of(
//                new Ingredient( 6,"Farine", 1200.0, CategoryEnum.OTHER),
//                new Ingredient( 7,"Levure", 500.0, CategoryEnum.OTHER));
//        try {
//            List<Ingredient> created1 = dataRetriever.createIngredients(newIngredients1);
//            System.out.println("Test 1 réussi : ingrédients créés :");
//            for (Ingredient ing : created1) {
//                System.out.println("- " + ing.getName() + " (Id=" + ing.getId() + ")");
//            }
//        } catch (RuntimeException e) {
//            System.out.println("Test 1 échoué : " + e.getMessage());
//        }
//
//        System.out.println("---Test J ---");
//        List<Ingredient> newIngredients2 = List.of(
//                new Ingredient( null,"Poivron", 1200.0, CategoryEnum.OTHER),
//                new Ingredient( null,"Viande de Porc", 15000.0, CategoryEnum.ANIMAL));
//        try {
//            List<Ingredient> created1 = dataRetriever.createIngredients(newIngredients2);
//            System.out.println("Test 1 réussi : ingrédients créés :");
//            for (Ingredient ing : created1) {
//                System.out.println("- " + ing.getName() + " (Id=" + ing.getId() + ")");
//            }
//        } catch (RuntimeException e) {
//            System.out.println("Test 1 échoué : " + e.getMessage());
//        }
//
//        System.out.println("------------- Save Plat ------------");
//        DishIngredient di1 = new DishIngredient();
//        di1.setIngredient(new Ingredient(4, "Chocolat", 3000.0, CategoryEnum.OTHER));
//        di1.setQuantity(1.0);
//        di1.setUnit(Unit.KG);
//
//        DishIngredient di2 = new DishIngredient();
//        di2.setIngredient(new Ingredient(6, "Farine", 1200.0, CategoryEnum.OTHER));
//        di2.setQuantity(1.0);
//        di2.setUnit(Unit.KG);
//
//        DishIngredient di3 = new DishIngredient();
//        di3.setIngredient(new Ingredient(5, "Beurre", 2500.0, CategoryEnum.DAIRY));
//        di3.setQuantity(1.0);
//        di3.setUnit(Unit.KG);
//
//        DishIngredient di4 = new DishIngredient();
//        di4.setIngredient(new Ingredient(1, "Laitue", 800.0, CategoryEnum.VEGETABLE));
//        di4.setQuantity(1.0);
//        di4.setUnit(Unit.KG);
//
//        Dish newDish = new Dish();
//        newDish.setId(3);
//        newDish.setName("Riz aux légumes");
//        newDish.setDishType(DishTypeEnum.MAIN);
//        newDish.setPrice(10000.0);
//        newDish.setIngredients(List.of(di1, di2,di3,di4));
//        Dish savedDish = dataRetriever.saveDish(newDish);
//        System.out.println("----- Plat enregistré ------");
//        savedDish.prettyPrint();
//        System.out.println("---------------------------------");
//
//        System.out.println("------Couts----- :");
//        Dish dishE = dataRetriever.findDishById(3);
//        System.out.println("Le prix du "+ dishE.getName()+ " est ; " + dishE.getPrice()+ "Ar");
//        System.out.println("Total ingredient par plat (depenses) : " + dishE.getDishCost() +"Ar");
//        System.out.println("Benefice : " + dishE.getGrossMargin() + "Ar");
//        System.out.println("---------------------------------");
//    }
//    public static void printIngredients(List<Ingredient> ingredients) {
//        System.out.println("🥬 Ingrédients");
//        System.out.println("---------------------------------");
//
//        if (ingredients == null || ingredients.isEmpty()) {
//            System.out.println("⚠️ Aucun ingrédient trouvé !");
//        } else {
//
//            for (Ingredient ing : ingredients) {
//                System.out.println( "️⃣ " + ing.getName());
//                ing.prettyPrint();
//                System.out.println();
//            }
//        }
//
//        System.out.println("---------------------------------");
//    }
//
//
//    public static void printDishes(List<Dish> dishes) {
//        System.out.println("🍽️ Plats");
//        System.out.println("---------------------------------");
//
//        if (dishes == null || dishes.isEmpty()) {
//            System.out.println("⚠️ Aucun plat trouvé !");
//        } else {
//
//            for (Dish dish : dishes) {
//                System.out.println("️⃣ " + dish.getName());
//                dish.prettyPrint();
//                System.out.println();
//            }
//        }
//
//        System.out.println("---------------------------------");

        Ingredient ingredient = new Ingredient();
        ingredient.setId(1);
        ingredient.setName("Huile");
        ingredient.setPrice(2500.0);
        ingredient.setCategory(CategoryEnum.OTHER);

        StockValue value = new StockValue();
        value.setQuantity(1.0);
        value.setUnit(Unit.KG);

        StockMovement movement = new StockMovement();
//        movement.setId(1);
        movement.setValue(value);
        movement.setType(MovementTypeEnum.OUT);
        movement.setCreationDateTime(Instant.now());

        ingredient.setStockMovementList(List.of(movement));

        Ingredient savedIngredient = dataRetriever.saveIngredient(ingredient);

        System.out.println("Ingredient sauvegardé avec ID : " + savedIngredient.getId());
        System.out.println("Mouvement sauvegardé avec ID : " + savedIngredient.getStockMovementList().get(0).getId());
 }
}
