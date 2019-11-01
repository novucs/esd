package net.novucs.esd.orm;

import static net.novucs.esd.util.StringUtil.camelToSnake;
import static net.novucs.esd.util.StringUtil.quoted;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.StringJoiner;
import net.novucs.esd.util.Password;
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
    return quoted(camelToSnake(name));
  }

  public boolean isPrimary() {
    return primary;
  }

  public boolean isForeignKey() {
    return foreignReference != null;
  }

  public String foreignKeySQL() {
    return quoted(camelToSnake(foreignReference.getSimpleName())) + "(\"id\")";
  }

  public Class<?> getForeignReference() {
    return foreignReference;
  }

  public boolean isNullable() {
    return nullable;
  }

  public String createSQL() {
    StringJoiner columnJoiner = new StringJoiner(" ");
    columnJoiner.add(getSQLName());
    if (isPrimary()) {
      columnJoiner.add("INT NOT NULL GENERATED ALWAYS AS IDENTITY");
      columnJoiner.add("(START WITH 1, INCREMENT BY 1)");
    } else if (isForeignKey()) {
      columnJoiner.add("INT REFERENCES");
      columnJoiner.add(foreignKeySQL());
    } else if (type == String.class || type == Password.class) {
      columnJoiner.add("VARCHAR(255)");
    } else if (type == Integer.class) {
      columnJoiner.add("INT");
    } else if (type == ZonedDateTime.class) {
      columnJoiner.add("TIMESTAMP");
    }
    if (!isNullable()) {
      columnJoiner.add("NOT NULL");
    }
    return columnJoiner.toString();
  }

  public boolean write(PreparedStatement statement, Object model, int index)
      throws SQLException {
    if (isPrimary()) {
      return false;
    }

    if (type == String.class) {
      statement.setString(index, ReflectUtil.getValue(model, this));
      return true;
    }

    if (type == Integer.class) {
      statement.setInt(index, ReflectUtil.getValue(model, this));
      return true;
    }

    if (type == Password.class) {
      Password password = ReflectUtil.getValue(model, this);
      statement.setString(index, password.toHashAndSalt());
      return true;
    }

    if (type == ZonedDateTime.class) {
      ZonedDateTime zonedDateTime = ReflectUtil.getValue(model, this);
      statement.setTimestamp(index, Timestamp.from(zonedDateTime.toInstant()));
      return true;
    }

    throw new IllegalArgumentException("Unsupported write data type: " + type);
  }

  public Object read(ResultSet resultSet, int index) throws SQLException {
    if (type == String.class) {
      return resultSet.getString(index);
    }

    if (type == Integer.class) {
      return resultSet.getInt(index);
    }

    if (type == Password.class) {
      String hashAndSalt = resultSet.getString(index);
      return Password.fromHashAndSalt(hashAndSalt);
    }

    if (type == ZonedDateTime.class) {
      Timestamp timestamp = resultSet.getTimestamp(index);
      return ZonedDateTime.ofInstant(timestamp.toInstant(), ZoneOffset.UTC);
    }

    throw new IllegalArgumentException("Unsupported read data type: " + type);
  }
}
