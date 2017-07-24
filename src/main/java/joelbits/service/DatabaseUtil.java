package joelbits.service;

import java.sql.Connection;
import java.sql.DriverManager;

class DatabaseUtil {
    private DatabaseUtil() {}

    static Connection getConnection() throws Exception {
        Class.forName("org.h2.Driver");
        return DriverManager.getConnection("jdbc:h2:~/test", "sa", "sa");
    }
}
