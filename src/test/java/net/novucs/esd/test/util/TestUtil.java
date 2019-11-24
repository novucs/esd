package net.novucs.esd.test.util;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.DriverManager;
import java.sql.SQLException;
import net.novucs.esd.lifecycle.DatabaseLifecycle;
import net.novucs.esd.orm.ConnectionSource;
import net.novucs.esd.orm.DaoManager;
import net.novucs.esd.util.ReflectUtil;

/**
 * The type Test utils.
 */
public final class TestUtil {

  private TestUtil() {
    throw new IllegalStateException();
  }

  /**
   * Assert not constructable.
   *
   * @param clazz the clazz
   * @throws ReflectiveOperationException the reflective operation exception
   */
  public static void assertNotConstructable(Class<?> clazz) throws ReflectiveOperationException {
    Constructor constructor = clazz.getDeclaredConstructor();
    constructor.setAccessible(true);
    try {
      constructor.newInstance();
    } catch (InvocationTargetException ex) {
      assertTrue("Construction must fail", ex.getCause() instanceof IllegalStateException);
      return;
    }
    fail("Construction must fail");
  }

  /**
   * Create test dao manager dao manager.
   *
   * @return the dao manager
   */
  public static DaoManager createTestDaoManager() throws SQLException {
    return createTestDaoManager(false);
  }

  /**
   * Create test dao manager dao manager.
   *
   * @param setupDatabaseLifecycle should the database lifecycle be setup?
   * @return the dao manager
   */
  public static DaoManager createTestDaoManager(boolean setupDatabaseLifecycle)
      throws SQLException {
    try {
      DriverManager.getConnection("jdbc:derby:memory:testDB;drop=true");
    } catch (SQLException ignore) { // NOPMD
    }
    String dbUrl = "jdbc:derby:memory:testDB;create=true";
    String dbUser = "impact";
    String dbPass = "derbypass";
    ConnectionSource connectionSource = new ConnectionSource(dbUrl, dbUser, dbPass);
    DaoManager daoManager = new DaoManager(connectionSource);

    if (setupDatabaseLifecycle) {
      daoManager.init(DatabaseLifecycle.MODEL_CLASSES);
      DatabaseLifecycle databaseLifecycle = new DatabaseLifecycle();
      try {
        ReflectUtil.setFieldValue(databaseLifecycle, "daoManager", daoManager);
      } catch (ReflectiveOperationException e) {
        throw new RuntimeException(e);  // NOPMD
      }
      databaseLifecycle.setupDevelopmentData();
    }

    return daoManager;
  }
}
