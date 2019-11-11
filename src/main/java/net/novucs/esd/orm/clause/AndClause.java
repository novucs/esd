package net.novucs.esd.orm.clause;

import net.novucs.esd.orm.SQLBuilder;

/**
 * The type And clause.
 */
public class AndClause implements Clause {

  @Override
  public SQLBuilder sql() {
    return new SQLBuilder("AND");
  }
}
