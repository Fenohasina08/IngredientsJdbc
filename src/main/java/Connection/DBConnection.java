package Connection;
import java.sql.Connection;
import java.sql.DriverManager;
public class DBConnection {
    public static Connection getDBConnection() {
        try {
            return DriverManager.getConnection("jdbc:postgresql://localhost:5432/mini_dish_db", "mini_dish_db_manager", "123456");
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
