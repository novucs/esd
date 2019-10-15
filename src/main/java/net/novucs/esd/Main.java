package net.novucs.esd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Main {

  private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

  private Main() {
  }

  public static void main(String[] args) {
    try {
      Map<String, String> env = System.getenv();
      String dbHost = env.getOrDefault("DB_HOST", "localhost");
      String dbPort = env.getOrDefault("DB_PORT", "1527");
       String dbUser = env.getOrDefault("DB_USER", "impact");
      String dbPass = env.getOrDefault("DB_PASS", "derbypass");
      String dbUrl = String.format("jdbc:derby://%s:%s/esd;create=true", dbHost, dbPort);
      try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPass)) {
        performDatabaseUpdates(connection);
      }
      LOGGER.info("Successfully completed transaction");
    } catch (SQLException ex) {
      LOGGER.log(Level.SEVERE, "Failed to communicate with database", ex);
    }
  }

  private static void performDatabaseUpdates(Connection connection) throws SQLException {
    try (PreparedStatement createUserTable = connection.prepareStatement(
        "CREATE TABLE person ("
            + "id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
            + "name VARCHAR(255), "
            + "PRIMARY KEY (id))")) {
      createUserTable.execute();
    }

    try (Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM person")) {
      ResultSetMetaData metaData = resultSet.getMetaData();
      int numberOfColumns = metaData.getColumnCount();
      StringJoiner headerJoiner = new StringJoiner("\t");
      for (int i = 1; i <= numberOfColumns; i++) {
        headerJoiner.add(metaData.getCatalogName(i));
      }
      LOGGER.info(headerJoiner.toString());

      while (resultSet.next()) {
        StringJoiner rowJoiner = new StringJoiner("\t");
        for (int i = 1; i <= numberOfColumns; i++) {
          rowJoiner.add(resultSet.getObject(i).toString());
        }
        LOGGER.info(rowJoiner.toString());
      }
    }
  }
}
