package net.novucs.esd.orm;

/**
 * The type Sql parameter.
 */
public class SQLParameter {

  private final Class<?> type;
  private final Object value;

  /**
   * Instantiates a new Sql parameter.
   *
   * @param type  the type
   * @param value the value
   */
  public SQLParameter(Class<?> type, Object value) {
    this.type = type;
    this.value = value;
  }

  /**
   * Gets type.
   *
   * @return the type
   */
  public Class<?> getType() {
    return type;
  }

  /**
   * Gets value.
   *
   * @return the value
   */
  public Object getValue() {
    return value;
  }
}
