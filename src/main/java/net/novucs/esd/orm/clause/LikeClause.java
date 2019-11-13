package net.novucs.esd.orm.clause;

import static net.novucs.esd.util.StringUtil.quoted;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import net.novucs.esd.orm.SQLBuilder;
import net.novucs.esd.orm.SQLParameter;

/**
 * The type Similar clause.
 */
public class LikeClause implements Clause {

  private final String columnName;
  private final String value;

  /**
   * Instantiates a new Similar clause.
   *
   * @param columnName the column name
   * @param value      the value
   */
  public LikeClause(String columnName, String value) {
    this.columnName = columnName;
    this.value = value;
  }

  /**
   * Gets column name.
   *
   * @return the column name
   */
  public String getColumnName() {
    return columnName;
  }

  /**
   * Gets value.
   *
   * @return the value
   */
  public String getValue() {
    return value;
  }

  @Override
  public SQLBuilder sql() {
    StringJoiner clauseJoiner = new StringJoiner(" ");
    clauseJoiner.add("LOWER(");
    clauseJoiner.add(quoted(this.getColumnName()));
    clauseJoiner.add(") LIKE LOWER(?)");

    List<SQLParameter> parameters = new ArrayList<>();
    parameters.add(new SQLParameter(value.getClass(), value));
    return new SQLBuilder(clauseJoiner.toString(), parameters);
  }
}
