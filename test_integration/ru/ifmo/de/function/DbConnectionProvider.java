package ru.ifmo.de.function;

import oracle.jdbc.pool.OracleConnectionCacheManager;
import oracle.jdbc.pool.OracleDataSource;


import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * Database connection provider
 *
 * Singleton
 *
 * @author Sotnik E.A.
 * @version 1.0
 */
public class DbConnectionProvider {

    private static  String URL;
    private static  String USER;
    private static  String PASSWORD;

    static {
        try {
            Properties dbProperties = new Properties();
            dbProperties.load(new FileInputStream("db.properties"));

            URL = dbProperties.getProperty("URL");
            USER = dbProperties.getProperty("USER");
            PASSWORD = dbProperties.getProperty("PASS");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static DbConnectionProvider self = new DbConnectionProvider();

    /**
     * If database connection ready
     */
    private volatile boolean connReady = false;

    /**
     * Database pool
     */
    private OracleDataSource pool = null;



    /**
     * Getting database connection provider instance
     * @return Database connection provider instance
     */
    public static DbConnectionProvider getInstance() {
        return self;
    }



    /**
     * Checking if database connection ready
     * @return Database initialized flag
     */
    public static boolean isConnReady() {
        return self.connReady;
    }

    /**
     * Getting database connection
     * @param checkIfReady Database connection ready checking flag
     * @return Database connection
     */
    public static Connection connect(boolean checkIfReady) {
        if (checkIfReady && !isConnReady()) {
            //Logger.getLogger("general").warn("Database connection provider. Database connection not ready.");
            System.out.println("Database connection provider. Database connection not ready.");
            return null;
        }

        try {
            return DriverManager.getConnection(
                    URL,
                    USER,
                    PASSWORD
            );
        }
        catch (Exception e) {
            //Logger.getLogger("general").error("Database connection provider. Connection failed. " + e.getMessage(), e);
            System.out.println("Database connection provider. Connection failed. " + e.getMessage());
        }

        return null;
    }

    /**
     * Getting database connection with checking if database initialized
     * @return Database connection
     */
    public static Connection connect() {
        return connect(true);
    }

    /**
     * Closing database connection
     * @param conn Database connection
     */
    public static void disconnect(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            //Logger.getLogger("general").error("Database connection provider. Unable to disconnect. " + e.getMessage(), e);
            System.out.println("Database connection provider. Unable to disconnect. " + e.getMessage());
        }
    }



    /**
     * Database connection initialization
     */
    public static void init() {
        //Logger.getLogger("general").info("Database connection provider. Initialization.");
        System.out.println("Database connection provider. Initialization.");

        self.connReady = false;

        try {

            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());

            if (self.pool != null) {
                //Logger.getLogger("general").info("Database connection provider. Closing connection.");
                System.out.println("Database connection provider. Closing connection.");
                self.pool.close();
            }

            //Logger.getLogger("general").info("Database connection provider. Creating new connection.");
            System.out.println("Database connection provider. Creating new connection.");

            self.pool = new OracleDataSource();
            self.pool.setUser(USER);
            self.pool.setPassword(PASSWORD);
            self.pool.setURL(URL);
            self.pool.setConnectionCachingEnabled(true);

            Properties cacheProps = new Properties();
            cacheProps.setProperty("MinLimit", "5");
            cacheProps.setProperty("MaxLimit", "" + Integer.MAX_VALUE);
            cacheProps.setProperty("InitialLimit", "15");
            cacheProps.setProperty("InactivityTimeout", "5");
            cacheProps.setProperty("AbandonedConnectionTimeout", "3600");
            cacheProps.setProperty("PropertyCheckInterval", "1");

            OracleConnectionCacheManager manager = OracleConnectionCacheManager.getConnectionCacheManagerInstance();
            manager.createCache("ANT_DB_POOL", self.pool, cacheProps);

            //Logger.getLogger("general").info("Database connection provider. OracleDataSource created.");
            System.out.println("Database connection provider. OracleDataSource created.");

            Connection conn = connect(false);
            if (conn != null) {
                //Logger.getLogger("general").info("Database connection provider. Connection established.");
                System.out.println("Database connection provider. Connection established.");
                conn.close();
                self.connReady = true;
            } else {
                //Logger.getLogger("general").error("Database connection provider. Error while initialization database: undefined error.");
                System.out.println("Database connection provider. Error while initialization database: undefined error.");
                System.exit(1);
            }
        } catch (Exception e) {
            //Logger.getLogger("general").error("Database connection provider. Error while initialization database. " + e.getMessage(), e);
            System.out.println("Database connection provider. Error while initialization database. " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

}
