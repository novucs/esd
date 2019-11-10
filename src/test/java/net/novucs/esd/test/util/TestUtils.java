package net.novucs.esd.test.util;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import net.novucs.esd.orm.ConnectionSource;
import net.novucs.esd.orm.DaoManager;

/**
 * The type Test utils.
 */
public final class TestUtils {

  private TestUtils() {
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
  public static DaoManager createTestDaoManager() {
    String dbUrl = "jdbc:derby:memory:testDB;create=true";
    String dbUser = "impact";
    String dbPass = "derbypass";
    ConnectionSource connectionSource = new ConnectionSource(dbUrl, dbUser, dbPass);
    return new DaoManager(connectionSource);
  }
}
