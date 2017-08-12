package joelbits.service.listener;

import org.h2.tools.RunScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.FileReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CreateTableListener implements ServletContextListener {
    private final static Logger log = LoggerFactory.getLogger(CreateTableListener.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try (Connection connection = (Connection) servletContextEvent.getServletContext().getAttribute("connection")) {
            Class.forName("org.h2.Driver");
            URL url = CreateTableListener.class.getClassLoader().getResource("files.sql");
            RunScript.execute(connection, new FileReader(url.getFile()));
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try {
            Driver driver = DriverManager.getDriver("org.h2.Driver");
            DriverManager.deregisterDriver(driver);
        } catch (SQLException ex) {
            log.info("Could not deregister org.h2.Driver");
        }
    }
}
