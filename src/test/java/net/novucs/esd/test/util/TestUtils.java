package net.novucs.esd.test.util;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class TestUtils {

  private TestUtils() {
    throw new IllegalStateException();
  }

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
}
