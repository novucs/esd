package net.novucs.esd.orm.clause;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import net.novucs.esd.orm.SQLBuilder;
import net.novucs.esd.orm.SQLParameter;

public class EqClause implements Clause {

  private final String columnName;
  private final Object value;

  public EqClause(String columnName, Object value) {
    this.columnName = columnName;
    this.value = value;
  }

  public String getColumnName() {
    return columnName;
  }

  public Object getValue() {
    return value;
  }

  @Override
  public SQLBuilder sql() {
    StringJoiner clauseJoiner = new StringJoiner(" ");
    clauseJoiner.add(this.getColumnName());
    clauseJoiner.add("= ?");

    List<SQLParameter> parameters = new ArrayList<>();
    parameters.add(new SQLParameter(value.getClass(), value));
    return new SQLBuilder(clauseJoiner.toString(), parameters);
  }
}
