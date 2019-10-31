package net.novucs.esd.orm;

import static net.novucs.esd.util.StringUtil.camelToSnake;

import java.util.StringJoiner;

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

  public String createSQL() {
    StringJoiner columnJoiner = new StringJoiner(" ");
    columnJoiner.add(this.getSQLName());
    if (this.isPrimary()) {
      columnJoiner.add("INT NOT NULL GENERATED ALWAYS AS IDENTITY");
      columnJoiner.add("(START WITH 1, INCREMENT BY 1)");
    } else if (this.isForeignKey()) {
      columnJoiner.add("INT REFERENCES");
      columnJoiner.add(this.foreignKeySQL());
    } else if (this.getType() == String.class) {
      columnJoiner.add("VARCHAR(255)");
    } else if (this.getType() == Integer.class) {
      columnJoiner.add("INT");
    }
    if (!this.isNullable()) {
      columnJoiner.add("NOT NULL");
    }
    return columnJoiner.toString();
  }
}
