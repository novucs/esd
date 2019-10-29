package net.novucs.esd.orm;

import java.util.ArrayList;
import java.util.List;

public class SQLBuilder {

  private final String query;
  private final List<SQLParameter> parameters;

  public SQLBuilder(String query) {
    this(query, new ArrayList<>());
  }

  public SQLBuilder(String query, List<SQLParameter> parameters) {
    this.query = query;
    this.parameters = parameters;
  }

  public String getQuery() {
    return query;
  }

  public List<SQLParameter> getParameters() {
    return parameters;
  }
}
