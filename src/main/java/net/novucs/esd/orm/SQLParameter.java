package net.novucs.esd.orm;

public class SQLParameter {

  private final Class<?> type;
  private final Object value;

  public SQLParameter(Class<?> type, Object value) {
    this.type = type;
    this.value = value;
  }

  public Class<?> getType() {
    return type;
  }

  public Object getValue() {
    return value;
  }
}
