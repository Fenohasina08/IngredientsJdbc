package classes;
import java.time.Instant;
public class StockMovement {
    private int id;
    private StockValue value;
    private MovementTypeEnum type;
    private Instant creationDatetime;
    public StockMovement(int id, StockValue value, MovementTypeEnum type, Instant date) {
        this.id = id; this.value = value; this.type = type; this.creationDatetime = date;
    }
    public double getQuantity() { return value.getQuantity(); }
    public MovementTypeEnum getType() { return type; }
    public Instant getCreationDatetime() { return creationDatetime; }
}
