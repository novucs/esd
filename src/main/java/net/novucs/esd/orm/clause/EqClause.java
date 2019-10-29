package net.novucs.esd.orm.clause;

import java.util.StringJoiner;

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
  public String sql() {
    StringJoiner clauseJoiner = new StringJoiner("");

//    clauseJoiner.add("\"");
    clauseJoiner.add(this.getColumnName());

    if (this.getValue() instanceof Integer) {
//      clauseJoiner.add("\" = ");
      clauseJoiner.add(" = ");
      clauseJoiner.add(this.getValue().toString());
    }
    else if(this.getValue() instanceof String) {
//      clauseJoiner.add("\" = \"");
      clauseJoiner.add(" = '");
      clauseJoiner.add(this.getValue().toString());
      clauseJoiner.add("'");
    }

    return clauseJoiner.toString();
  }
}
