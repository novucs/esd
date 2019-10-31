package net.novucs.esd.orm;

import static net.novucs.esd.util.StringUtil.camelToSnake;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringJoiner;
import net.novucs.esd.util.ReflectUtil;

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

  public boolean write(PreparedStatement statement, Object model, int index)
      throws SQLException {
    if (this.isPrimary()) {
      return false;
    }

    if (this.getType() == String.class) {
      statement.setString(index, ReflectUtil.getValue(model, this));
      return true;
    }

    if (this.getType() == Integer.class) {
      statement.setInt(index, ReflectUtil.getValue(model, this));
      return true;
    }

    throw new IllegalArgumentException("Unsupported write data type: " + this.getType());
  }

  public Object read(ResultSet resultSet, int index) throws SQLException {
    if (this.getType() == String.class) {
      return resultSet.getString(index);
    }

    if (this.getType() == Integer.class) {
      return resultSet.getInt(index);
    }

    throw new IllegalArgumentException("Unsupported read data type: " + this.getType());
  }
}
