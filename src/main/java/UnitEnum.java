public enum UnitEnum {
    G(1.0),    // Référence pour la masse
    KG(1000.0), // 1 kg = 1000 g
    ML(1.0),   // Référence pour le volume
    L(1000.0),  // 1 L = 1000 ml
    PCS(1.0);  // Unité par pièce (pas de conversion)

    private final Double factor;

    UnitEnum(Double factor) {
        this.factor = factor;
    }

    public Double getFactor() {
        return factor;
    }
}