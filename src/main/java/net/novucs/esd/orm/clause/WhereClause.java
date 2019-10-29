package net.novucs.esd.orm.clause;

import net.novucs.esd.orm.Where;

public class WhereClause implements Clause {
  private final Where where;

  public WhereClause(Where where) {
    this.where = where;
  }

  @Override
  public String sql() {
    return "(" + this.where.sql() + ")";
  }
}
