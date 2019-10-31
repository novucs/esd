package net.novucs.esd.test.util;

import static net.novucs.esd.test.util.TestUtils.assertNotConstructable;
import static org.junit.Assert.assertEquals;

import net.novucs.esd.util.StringUtil;
import org.junit.Test;

public class TestStringUtil {

  @Test
  public void testCamelToSnakeWorks() {
    String snake = StringUtil.camelToSnake("SomeClassName");
    assertEquals("Camel to snake should convert camel case names", "some_class_name", snake);
  }

  @Test
  public void testStringUtilCannotBeConstructed() throws ReflectiveOperationException {
    assertNotConstructable(StringUtil.class);
  }
}
