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

/**
 * The type Main.
 */
public final class Main {

  private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

  private Main() {
  }

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    try {
      Map<String, String> env = System.getenv();
      String dbUrl = env.getOrDefault("DB_URL", "jdbc:derby://localhost:1527/esd;create=true");
      String dbUser = env.getOrDefault("DB_USER", "impact");
      String dbPass = env.getOrDefault("DB_PASS", "derbypass");
      try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPass)) {
        displayPeople(connection);
      }
      LOGGER.info("Successfully completed transaction");
    } catch (SQLException ex) {
      LOGGER.log(Level.SEVERE, "Failed to communicate with database", ex);
    }
  }

  private static void displayPeople(Connection connection) throws SQLException {
    try (PreparedStatement createUserTable = connection.prepareStatement(
        "CREATE TABLE person ("
            + "id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
            + "name VARCHAR(255), "
            + "PRIMARY KEY (id))")) {
      createUserTable.execute();
    } catch (SQLException ex) {
      // Do nothing if already exists
      // http://db.apache.org/derby/docs/10.8/ref/rrefexcept71493.html
      if (!ex.getSQLState().equals("X0Y32")) {
        throw ex;
      }
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
