package net.novucs.esd.orm.clause;

import net.novucs.esd.orm.SQLBuilder;

/**
 * The interface Clause.
 */
public interface Clause {

  /**
   * Sql sql builder.
   *
   * @return the sql builder
   */
  SQLBuilder sql();
}
