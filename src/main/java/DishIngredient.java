public class DishIngredient {
    private final Ingredient ingredient; // L'objet Ingredient complet
    private final Double quantity;        // La dose pour la recette

    // Le constructeur pour créer l'objet
    public DishIngredient(Ingredient ingredient, Double quantity) {
        this.ingredient = ingredient;
        this.quantity = quantity;
    }

    // Les fameuses méthodes "get" dont nous avons besoin
    public Ingredient getIngredient() {
        return ingredient;
    }

    public Double getQuantity() {
        return quantity;
    }
}