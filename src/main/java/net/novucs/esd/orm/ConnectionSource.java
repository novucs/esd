package net.novucs.esd.orm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionSource {

  private final String dbUrl;
  private final String dbUser;
  private final String dbPass;

  public ConnectionSource(String dbUrl, String dbUser, String dbPass) {
    this.dbUrl = dbUrl;
    this.dbUser = dbUser;
    this.dbPass = dbPass;
  }

  public Connection getConnection() throws SQLException {
    // todo: turn this into a viable connection pool
    return DriverManager.getConnection(dbUrl, dbUser, dbPass);
  }

  public String getDbUrl() {
    return dbUrl;
  }

  public String getDbUser() {
    return dbUser;
  }

  public String getDbPass() {
    return dbPass;
  }
}
