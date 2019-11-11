package net.novucs.esd.test.util;

import static net.novucs.esd.test.util.TestUtils.assertNotConstructable;
import static org.junit.Assert.assertEquals;

import net.novucs.esd.util.StringUtil;
import org.junit.Test;

/**
 * The type Test string util.
 */
public class TestStringUtil {

  /**
   * Test camel to snake works.
   */
  @Test
  public void testCamelToSnakeWorks() {
    String snake = StringUtil.camelToSnake("SomeClassName");
    assertEquals("Camel to snake should convert camel case names", "some_class_name", snake);
  }

  /**
   * Test string util cannot be constructed.
   *
   * @throws ReflectiveOperationException the reflective operation exception
   */
  @Test
  public void testStringUtilCannotBeConstructed() throws ReflectiveOperationException {
    assertNotConstructable(StringUtil.class);
  }
}
