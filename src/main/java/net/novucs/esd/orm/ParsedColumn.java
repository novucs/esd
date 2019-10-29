package net.novucs.esd.orm;

public class ParsedColumn {
  private final Class<?> type;
  private final String name;
  private final boolean primary;

  public ParsedColumn(Class<?> type, String name, boolean primary) {
    this.type = type;
    this.name = name;
    this.primary = primary;
  }

  public Class<?> getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public boolean isPrimary() {
    return primary;
  }
}
