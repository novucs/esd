package net.novucs.esd.util;

import java.util.Locale;

public final class StringUtil {

  private StringUtil() {
    throw new IllegalStateException();
  }

  public static String camelToSnake(String name) {
    String regex = "([a-z])([A-Z]+)";
    String replacement = "$1_$2";
    return name.replaceAll(regex, replacement).toLowerCase(Locale.UK);
  }
}
