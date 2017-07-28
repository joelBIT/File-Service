package joelbits.service.listener;

import org.h2.tools.RunScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.FileReader;
import java.net.URL;
import java.sql.Connection;

public class CreateTableListener implements ServletContextListener {
    private final static Logger log = LoggerFactory.getLogger(CreateTableListener.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try (Connection connection = (Connection) servletContextEvent.getServletContext().getAttribute("connection")) {
            URL url = CreateTableListener.class.getClassLoader().getResource("files.sql");
            RunScript.execute(connection, new FileReader(url.getFile()));
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) { }
}
