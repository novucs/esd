package net.novucs.esd.test.lifecycle;

import static org.junit.Assert.assertEquals;

import java.sql.DriverManager;
import java.sql.SQLException;
import net.novucs.esd.lifecycle.DatabaseLifecycle;
import net.novucs.esd.model.User;
import org.apache.derby.jdbc.EmbeddedDriver;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

public class TestDatabaseLifecycle {

  @ClassRule
  public static final EnvironmentVariables ENVIRONMENT = new EnvironmentVariables();
  private static final String DB_URL = "jdbc:derby:memory:testDB;create=true";

  @Before
  public void setUp() throws SQLException {
    DriverManager.registerDriver(new EmbeddedDriver());
  }

  @Test
  public void testDatabaseIsCreatedOnInit() throws SQLException {
    // Given
    ENVIRONMENT.set("DB_URL", DB_URL);
    DatabaseLifecycle lifecycle = new DatabaseLifecycle();

    // When
    lifecycle.init();
    User bob = lifecycle.getDaoManager().get(User.class).selectById(1);

    // Assert
    assertEquals("Database must be created on init", bob.getName(), "bob");
  }
}