import java.time.Instant;
public class StockMovement {
    private int id;
    private StockValue value;
    private MovementTypeEnum type;
    private Instant creationDatetime;

    public StockMovement(int id, StockValue value, MovementTypeEnum type, Instant creationDatetime) {
        this.id = id;
        this.value = value;
        this.type = type;
        this.creationDatetime = creationDatetime;
    }

    // Getters essentiels pour le calcul et la base de données
    public int getId() { return id; }
    public StockValue getValue() { return value; }
    public MovementTypeEnum getType() { return type; }
    public Instant getCreationDatetime() { return creationDatetime; }
}
