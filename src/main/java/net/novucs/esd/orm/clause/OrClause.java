package net.novucs.esd.orm.clause;

import net.novucs.esd.orm.SQLBuilder;

/**
 * The type Or clause.
 */
public class OrClause implements Clause {

  @Override
  public SQLBuilder sql() {
    return new SQLBuilder("OR");
  }
}
