package net.novucs.esd.orm;

import static net.novucs.esd.util.StringUtil.camelToSnake;

public class ParsedColumn {

  private final Class<?> type;
  private final String name;
  private final boolean primary;
  private final Class<?> foreignReference;
  private final boolean nullable;

  public ParsedColumn(Class<?> type, String name, boolean primary,
      Class<?> foreignReference, boolean nullable) {
    if (primary && (type != Integer.class || !"id".equals(name))) {
      throw new IllegalStateException("Primary keys must be integers named ID");
    }

    this.type = type;
    this.name = name;
    this.primary = primary;
    this.foreignReference = foreignReference;
    this.nullable = nullable;
  }

  public Class<?> getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public String getSQLName() {
    return camelToSnake(name);
  }

  public boolean isPrimary() {
    return primary;
  }

  public boolean isForeignKey() {
    return foreignReference != null;
  }

  public String foreignKeySQL() {
    return "\"" + camelToSnake(foreignReference.getSimpleName()) + "\"(id)";
  }

  public Class<?> getForeignReference() {
    return foreignReference;
  }

  public boolean isNullable() {
    return nullable;
  }
}
