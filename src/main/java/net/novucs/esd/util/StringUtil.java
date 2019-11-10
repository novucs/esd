package net.novucs.esd.util;

import java.util.Locale;

/**
 * The type String util.
 */
public final class StringUtil {

  private StringUtil() {
    throw new IllegalStateException();
  }

  /**
   * Camel to snake string.
   *
   * @param name the name
   * @return the string
   */
  public static String camelToSnake(String name) {
    String regex = "([a-z])([A-Z]+)";
    String replacement = "$1_$2";
    return name.replaceAll(regex, replacement).toLowerCase(Locale.UK);
  }

  /**
   * Quoted string.
   *
   * @param string the string
   * @return the string
   */
  public static String quoted(String string) {
    return "\"" + string + "\"";
  }
}
