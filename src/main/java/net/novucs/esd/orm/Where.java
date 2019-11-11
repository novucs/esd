package net.novucs.esd.orm;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import net.novucs.esd.orm.clause.AndClause;
import net.novucs.esd.orm.clause.Clause;
import net.novucs.esd.orm.clause.EqClause;
import net.novucs.esd.orm.clause.OrClause;
import net.novucs.esd.orm.clause.WhereClause;

/**
 * The type Where.
 */
public class Where {

  private final List<Clause> clauses = new ArrayList<>();

  /**
   * Gets clauses.
   *
   * @return the clauses
   */
  public List<Clause> getClauses() {
    return clauses;
  }

  /**
   * Eq where.
   *
   * @param columnName the column name
   * @param value      the value
   * @return the where
   */
  public Where eq(String columnName, Object value) {
    clauses.add(new EqClause(columnName, value));
    return this;
  }

  /**
   * Where where.
   *
   * @param where the where
   * @return the where
   */
  public Where where(Where where) {
    clauses.add(new WhereClause(where));
    return this;
  }

  /**
   * And where.
   *
   * @return the where
   */
  public Where and() {
    clauses.add(new AndClause());
    return this;
  }

  /**
   * Or where.
   *
   * @return the where
   */
  public Where or() {
    clauses.add(new OrClause());
    return this;
  }

  /**
   * Sql sql builder.
   *
   * @return the sql builder
   */
  public SQLBuilder sql() {
    StringJoiner clauseJoiner = new StringJoiner(" ");
    List<SQLParameter> parameters = new ArrayList<>();
    for (Clause clause : this.clauses) {
      SQLBuilder builder = clause.sql();
      parameters.addAll(builder.getParameters());
      clauseJoiner.add(builder.getQuery());
    }
    return new SQLBuilder(clauseJoiner.toString(), parameters);
  }
}
