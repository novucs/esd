package net.novucs.esd.test;

import static net.novucs.esd.test.ReflectUtil.executePrivateStaticMethod;
import static net.novucs.esd.test.ReflectUtil.setStaticFieldValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.novucs.esd.Main;
import org.apache.derby.jdbc.EmbeddedDriver;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

public class TestMain {

  @ClassRule
  public static final EnvironmentVariables ENVIRONMENT = new EnvironmentVariables();
  private static final String DB_URL = "jdbc:derby:memory:testDB;create=true";

  @Before
  public void setUp() throws SQLException {
    DriverManager.registerDriver(new EmbeddedDriver());
  }

  @Test
  public void testMainExecutesWithoutErrors() throws ReflectiveOperationException {
    // Given
    ENVIRONMENT.set("DB_URL", DB_URL);
    Logger logger = mock(Logger.class);
    setStaticFieldValue(Main.class, "LOGGER", logger);

    // When
    Main.main(new String[]{});

    // Assert
    verify(logger, times(0)).log(eq(Level.SEVERE), any(String.class), any(SQLException.class));
  }

  @Test
  public void testMainExceptsWithBadDatabaseUrl() throws ReflectiveOperationException {
    // Given
    Logger logger = mock(Logger.class);
    setStaticFieldValue(Main.class, "LOGGER", logger);
    ENVIRONMENT.set("DB_URL", "this is a bad database url");

    // When
    Main.main(new String[0]);

    // Assert
    verify(logger, times(1)).log(eq(Level.SEVERE), any(String.class), any(SQLException.class));
  }

  @SuppressWarnings("PMD.CloseResource")
  @Test(expected = SQLException.class)
  public void testFailsOnNonExistsStatusCode() throws Throwable {
    // Given
    Connection connection = mock(Connection.class);
    PreparedStatement statement = mock(PreparedStatement.class);
    SQLException exception = mock(SQLException.class);

    // When
    when(exception.getSQLState()).thenAnswer(invocation -> "unexpected state");
    when(connection.prepareStatement(any(String.class))).thenAnswer(invocation -> statement);
    when(statement.execute()).thenThrow(exception);
    executePrivateStaticMethod(Main.class, "displayPeople",
        new Class[]{Connection.class}, connection);
  }

  @Test
  public void testMainPrintsContentsOfPerson() throws Throwable {
    // Given
    ENVIRONMENT.set("DB_URL", DB_URL);
    String[] names = {"Bob", "Steve", "Jeff"};
    Logger logger = mock(Logger.class);
    setStaticFieldValue(Main.class, "LOGGER", logger);

    try (Connection connection = DriverManager.getConnection(DB_URL)) {
      // Ensure tables are created.
      executePrivateStaticMethod(Main.class, "displayPeople",
          new Class[]{Connection.class}, connection);

      // Insert test data.
      try (PreparedStatement insertion = connection
          .prepareStatement("INSERT INTO person (name) VALUES (?)")) {
        for (String name : names) {
          insertion.setString(1, name);
          insertion.addBatch();
        }
        insertion.executeBatch();
      }

      // When
      executePrivateStaticMethod(Main.class, "displayPeople",
          new Class[]{Connection.class}, connection);
    }

    // Assert
    for (String name : names) {
      verify(logger, atLeastOnce()).info(contains(name));
    }
  }
}
