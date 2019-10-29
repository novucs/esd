package net.novucs.esd.orm.clause;

public class OrClause implements Clause {
  @Override
  public String sql() {
    return " OR ";
  }
}
