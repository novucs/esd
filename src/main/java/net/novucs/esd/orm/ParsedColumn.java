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

/**
 * The type Parsed column.
 */
public class ParsedColumn {

  private final Class<?> type;
  private final String name;
  private final boolean primary;
  private final Class<?> foreignReference;
  private final boolean nullable;
  private final String unique;

  /**
   * Instantiates a new Parsed column.
   *
   * @param type             the type
   * @param name             the name
   * @param primary          the primary
   * @param foreignReference the foreign reference
   * @param nullable         the nullable
   * @param unique           the unique constraint
   */
  public ParsedColumn(Class<?> type, String name, boolean primary,
      Class<?> foreignReference, boolean nullable, String unique) {
    if (primary && (type != Integer.class || !"id".equals(name))) {
      throw new IllegalStateException("Primary keys must be integers named ID");
    }

    this.type = type;
    this.name = name;
    this.primary = primary;
    this.foreignReference = foreignReference;
    this.nullable = nullable;
    this.unique = unique;
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
   * Gets name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets sql name.
   *
   * @return the sql name
   */
  public String getSQLName() {
    return quoted(camelToSnake(name));
  }

  /**
   * Is primary boolean.
   *
   * @return the boolean
   */
  public boolean isPrimary() {
    return primary;
  }

  /**
   * Is foreign key boolean.
   *
   * @return the boolean
   */
  public boolean isForeignKey() {
    return foreignReference != null;
  }

  /**
   * Foreign key sql string.
   *
   * @return the string
   */
  public String foreignKeySQL() {
    return quoted(camelToSnake(foreignReference.getSimpleName())) + "(\"id\")";
  }

  /**
   * Gets foreign reference.
   *
   * @return the foreign reference
   */
  public Class<?> getForeignReference() {
    return foreignReference;
  }

  /**
   * Is nullable boolean.
   *
   * @return the boolean
   */
  public boolean isNullable() {
    return nullable;
  }

  /**
   * Check if the column has a unique constraint.
   *
   * @return <code>true</code> if the column has a unique constraint.
   */
  public boolean hasUniqueConstraint() {
    return unique != null && !unique.isEmpty();
  }

  /**
   * Get the unique constraint for this column.
   *
   * @return the unique constraint name.
   */
  public String getUniqueConstraint() {
    return hasUniqueConstraint() ? unique : null;
  }

  /**
   * Create sql string.
   *
   * @return the string
   */
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

  /**
   * Write boolean.
   *
   * @param statement the statement
   * @param model     the model
   * @param index     the index
   * @return the boolean
   * @throws SQLException the sql exception
   */
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

  /**
   * Read object.
   *
   * @param resultSet the result set
   * @param index     the index
   * @return the object
   * @throws SQLException the sql exception
   */
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
