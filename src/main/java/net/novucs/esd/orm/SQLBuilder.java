package net.novucs.esd.orm;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Sql builder.
 */
public class SQLBuilder {

  private final String query;
  private final List<SQLParameter> parameters;

  /**
   * Instantiates a new Sql builder.
   *
   * @param query the query
   */
  public SQLBuilder(String query) {
    this(query, new ArrayList<>());
  }

  /**
   * Instantiates a new Sql builder.
   *
   * @param query      the query
   * @param parameters the parameters
   */
  public SQLBuilder(String query, List<SQLParameter> parameters) {
    this.query = query;
    this.parameters = parameters;
  }

  /**
   * Gets query.
   *
   * @return the query
   */
  public String getQuery() {
    return query;
  }

  /**
   * Gets parameters.
   *
   * @return the parameters
   */
  public List<SQLParameter> getParameters() {
    return parameters;
  }
}
