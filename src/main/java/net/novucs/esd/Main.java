package net.novucs.esd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public class Main {

  public static void main(String[] args) {
    try {
      Map<String, String> env = System.getenv();
      String dbHost = env.getOrDefault("DB_HOST", "localhost");
      String dbPort = env.getOrDefault("DB_PORT", "1527");
      String dbUser = env.getOrDefault("DB_USER", "impact");
      String dbPass = env.getOrDefault("DB_PASS", "derbypass");
      String dbUrl = String.format("jdbc:derby://%s:%s/esd;create=true", dbHost, dbPort);
      Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
      try {
        performDatabaseUpdates(connection);
      } finally {
        connection.close();
      }
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
    System.out.println("Success");
  }

  private static void performDatabaseUpdates(Connection connection) throws SQLException {
    try (PreparedStatement createUserTable = connection.prepareStatement(
        "CREATE TABLE person ("
            + "id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
            + "name VARCHAR(255), "
            + "PRIMARY KEY (id))")) {
      createUserTable.execute();
    }

    try (Statement statement = connection.createStatement()) {
      try (ResultSet resultSet = statement.executeQuery("SELECT * FROM person")) {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int numberOfColumns = metaData.getColumnCount();
        for (int i = 1; i <= numberOfColumns; i++) {
          System.out.print(metaData.getColumnName(i) + "\t");
        }
        System.out.println();

        while (resultSet.next()) {
          for (int i = 1; i <= numberOfColumns; i++) {
            System.out.print(resultSet.getObject(i) + "\t");
          }
          System.out.println();
        }
      }
    }
  }
}
