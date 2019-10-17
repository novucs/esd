package net.novucs.esd.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.novucs.esd.Main;
import org.apache.derby.jdbc.EmbeddedDriver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

public class TestMain {

  @ClassRule
  public static final EnvironmentVariables ENVIRONMENT = new EnvironmentVariables();

  @Before
  public void setUp() throws SQLException {
    DriverManager.registerDriver(new EmbeddedDriver());
  }

  @Test
  public void testMainCreatesPersonTable() throws SQLException {
    // Given
    String dbUrl = "jdbc:derby:memory:testDB;create=true";
    ENVIRONMENT.set("DB_URL", dbUrl);

    // When
    Main.main(new String[0]);

    // Assert
    List<String> tables = new ArrayList<>();
    try (Connection connection = DriverManager.getConnection(dbUrl);
        ResultSet resultSet = connection.getMetaData().getTables(
            null, null, null, new String[]{"TABLE"})) {
      while (resultSet.next()) {
        String tableName = resultSet.getString("TABLE_NAME");
        tables.add(tableName);
        if ("person".equalsIgnoreCase(tableName)) {
          return;
        }
      }
    }
    Assert.fail("Expected main to create the person table. Found tables: " + tables);
  }
}
