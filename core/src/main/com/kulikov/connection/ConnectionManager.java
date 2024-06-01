package main.com.kulikov.connection;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.springframework.stereotype.Component;

/**
 * The ConnectionManager class manages connections to the database through a connection pool.
 */
@Component
public class ConnectionManager {

  /** The key to retrieve the database URL from properties. */
  private static final String URL_KEY = "db.url";
  /** The key to retrieve the database username from properties. */
  private static final String USERNAME_KEY = "db.username";
  /** The key to retrieve the database password from properties. */
  private static final String PASSWORD_KEY = "db.password";
  /** The key to retrieve the pool size from properties. */
  private static final String POOL_SIZE_KEY = "db.pool.size";
  /** The default pool size. */
  private static final Integer DEFAULT_POOL_SIZE = 10;

  /** The pool of database connections. */
  private static BlockingQueue<Connection> pool;
  /** The list of source connections. */
  private static List<Connection> sourceConnections;

  static {
    initConnectionPool();
  }

  /**
   * Retrieves a connection from the connection pool.
   *
   * @return A database connection
   */
  public static Connection get() {
    try {
      return pool.take();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Closes the connection pool by closing all connections.
   */
  public static void closePool() {
    try {
      for (Connection sourceConnection : sourceConnections) {
        sourceConnection.close();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Loads the database driver.
   */
  private static void loadDriver() {
    try {
      String driverName = "org.mariadb.jdbc.Driver";
      Class.forName(driverName);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Opens a new connection to the database.
   *
   * @return A database connection
   */
  private static Connection open() {
    try {
      return DriverManager.getConnection(
          PropertyManager.get(URL_KEY),
          PropertyManager.get(USERNAME_KEY),
          PropertyManager.get(PASSWORD_KEY)
      );
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Initializes the connection pool by creating connections and adding them to the pool.
   */
  private static void initConnectionPool() {
    String poolSize = PropertyManager.get(POOL_SIZE_KEY);
    int size = poolSize == null ? DEFAULT_POOL_SIZE : Integer.parseInt(poolSize);
    pool = new ArrayBlockingQueue<>(size);
    sourceConnections = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      Connection connection = open();
      Connection proxyConnection = (Connection) Proxy.newProxyInstance(
          ConnectionManager.class.getClassLoader(),
          new Class[]{Connection.class},
          ((proxy, method, args) -> method.getName().equals("close")
              ? pool.add((Connection) proxy)
              : method.invoke(connection, args)));
      pool.add(proxyConnection);
      sourceConnections.add(connection);
    }
  }
}

