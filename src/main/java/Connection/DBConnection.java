package Connection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DBConnection {
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(System.getenv("JDBC_URL"), System.getenv("USER"), System.getenv("PASS"));
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
