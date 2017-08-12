package joelbits.service.util;

import java.sql.Connection;
import java.sql.DriverManager;

public final class DatabaseUtil {
    private DatabaseUtil() {}

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
    }
}
