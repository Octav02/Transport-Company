package ro.mpp2024.utils.dbutils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JdbcUtils {

    private Properties jdbcProps;

    private static final Logger logger = LogManager.getLogger();

    private Connection connection = null;
    public JdbcUtils(Properties jdbcProps) {
        this.jdbcProps = jdbcProps;
    }

    private Connection getNewConnection() {
        logger.traceEntry();

        String url = jdbcProps.getProperty("jdbc.url");
        String username = jdbcProps.getProperty("jdbc.username");
        String password = jdbcProps.getProperty("jdbc.password");
        logger.info("trying to connect to database ... {}", url);
        logger.info("user: {}",username);

        Connection newConnection = null;
        try {
            if (username != null && password != null) {
                newConnection = DriverManager.getConnection(url,username,password);
            }
            else {
                newConnection = DriverManager.getConnection(url);

            }
        }
        catch (SQLException e) {
            logger.error("Error getting connection " + e);
        }
        logger.traceExit(connection);
        return newConnection;
    }

    public Connection getConnection() {
        logger.traceEntry();
        try {
            if (connection == null || connection.isClosed()) {
                connection = getNewConnection();
            }
        }catch (SQLException e) {
            logger.error("Database Error " + e);
        }

        logger.traceExit(connection);
        return connection;
    }

}
