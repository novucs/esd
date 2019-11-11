package net.novucs.esd.orm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * The type Connection source.
 */
public class ConnectionSource {

  private final String dbUrl;
  private final String dbUser;
  private final String dbPass;

  /**
   * Instantiates a new Connection source.
   *
   * @param dbUrl  the db url
   * @param dbUser the db user
   * @param dbPass the db pass
   */
  public ConnectionSource(String dbUrl, String dbUser, String dbPass) {
    this.dbUrl = dbUrl;
    this.dbUser = dbUser;
    this.dbPass = dbPass;
  }

  /**
   * Gets connection.
   *
   * @return the connection
   * @throws SQLException the sql exception
   */
  public Connection getConnection() throws SQLException {
    // todo: turn this into a viable connection pool
    return DriverManager.getConnection(dbUrl, dbUser, dbPass);
  }

  /**
   * Gets db url.
   *
   * @return the db url
   */
  public String getDbUrl() {
    return dbUrl;
  }

  /**
   * Gets db user.
   *
   * @return the db user
   */
  public String getDbUser() {
    return dbUser;
  }

  /**
   * Gets db pass.
   *
   * @return the db pass
   */
  public String getDbPass() {
    return dbPass;
  }
}
