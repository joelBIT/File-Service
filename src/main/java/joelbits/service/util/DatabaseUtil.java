package joelbits.service.util;

import java.sql.Connection;
import java.sql.DriverManager;

public final class DatabaseUtil {
    private DatabaseUtil() {}

    public static Connection getConnection() throws Exception {
        Class.forName("org.h2.Driver");
        return DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
    }
}
