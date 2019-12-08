package net.novucs.esd.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

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

  /**
   * Parse a username, input of "John Smith" -> "j-smith".
   *
   * @param name the name
   * @return a parsed name
   */
  public static String parseUsername(String name) {
    ArrayList<String> names = new ArrayList<>(Arrays.asList(name.split(" ")));
    String lastName = names.get(names.size() - 1);
    names.remove(names.size() - 1);
    String initials = names.stream()
        .map(c -> String.valueOf(c.charAt(0)))
        .collect(Collectors.joining(""));

    // If they have no initials, just return their last name as their username
    if (initials.length() == 0) {
      return lastName.toLowerCase(Locale.UK);
    }
    return (initials + "-" + lastName).toLowerCase(Locale.UK);
  }
}
