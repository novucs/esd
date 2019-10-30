package net.novucs.esd.orm.clause;

import net.novucs.esd.orm.SQLBuilder;
import net.novucs.esd.orm.Where;

public class WhereClause implements Clause {

  private final transient Where where;

  public WhereClause(Where where) {
    this.where = where;
  }

  @Override
  public SQLBuilder sql() {
    SQLBuilder builder = where.sql();
    return new SQLBuilder("(" + builder.getQuery() + ")", builder.getParameters());
  }
}
