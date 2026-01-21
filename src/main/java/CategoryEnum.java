public enum CategoryEnum {
    DAIRY("Dairy"),
    MEAT("Meat"),
    VEGETABLE("Vegetable");

    private final String label;

    // Le constructeur de l'enum
    CategoryEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}