package net.novucs.esd.orm.clause;

import static net.novucs.esd.util.StringUtil.quoted;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import net.novucs.esd.orm.SQLBuilder;
import net.novucs.esd.orm.SQLParameter;

/**
 * The type Eq clause.
 */
public class CompareClause implements Clause {

  private final String columnName;
  private final CompareType compareType;
  private final Object value;

  /**
   * Instantiates a new Eq clause.
   *
   * @param columnName  the column name
   * @param compareType how the column should be compared to the value
   * @param value       the value
   */
  public CompareClause(String columnName, CompareType compareType, Object value) {
    this.columnName = columnName;
    this.compareType = compareType;
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
   * Gets the compare type.
   *
   * @return the compare type.
   */
  public CompareType getCompareType() {
    return compareType;
  }

  /**
   * Gets value.
   *
   * @return the value
   */
  public Object getValue() {
    return value;
  }

  @Override
  public SQLBuilder sql() {
    StringJoiner clauseJoiner = new StringJoiner(" ");
    clauseJoiner.add(quoted(this.getColumnName()));

    switch (this.compareType) {
      case EQUALS:
        clauseJoiner.add("= ?");
        break;
      case NOT_EQUALS:
        clauseJoiner.add("!= ?");
        break;
      case GREATER_THAN:
        clauseJoiner.add("> ?");
        break;
      case LESS_THAN:
        clauseJoiner.add("< ?");
        break;
      default:
        throw new IllegalArgumentException("Unsupported operation: " + this.compareType.name());
    }

    List<SQLParameter> parameters = new ArrayList<>();
    parameters.add(new SQLParameter(value.getClass(), value));
    return new SQLBuilder(clauseJoiner.toString(), parameters);
  }
}
