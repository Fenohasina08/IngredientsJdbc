public enum UnitEnum {
    G(1.0),      // Référence Masse : 1 gramme
    KG(1000.0),  // 1 kg = 1000 grammes
    ML(1.0),     // Référence Volume : 1 millilitre
    L(1000.0),   // 1 litre = 1000 millilitres
    PCS(1.0);    // Pièce : pas de conversion

    private final Double factor;

    UnitEnum(Double factor) {
        this.factor = factor;
    }

    public Double getFactor() {
        return factor;
    }
}