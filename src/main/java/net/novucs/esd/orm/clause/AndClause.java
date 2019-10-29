package net.novucs.esd.orm.clause;

public class AndClause implements Clause {
  @Override
  public String sql() {
    return " AND ";
  }
}
