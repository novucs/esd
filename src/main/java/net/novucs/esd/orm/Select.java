package net.novucs.esd.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * The type Select.
 *
 * @param <M> the type parameter
 */
// todo: support joins
@SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
public class Select<M> {

  private final transient Dao<M> dao;
  private transient Integer offset;
  private transient Integer limit;
  private transient Where where;
  private transient String count;

  /**
   * Instantiates a new Select.
   *
   * @param dao the dao
   */
  public Select(Dao<M> dao) {
    this.dao = dao;
  }

  /**
   * Where select.
   *
   * @param where the where
   * @return the select
   */
  public Select<M> where(Where where) {
    this.where = where;
    return this;
  }

  /**
   * How far the selection should offset all matched results by.
   *
   * @param offset how far to offset the results by.
   * @return <code>this</code> Select - fluent API.
   */
  public Select<M> offset(int offset) {
    this.offset = offset;
    return this;
  }

  public long count(String count) throws SQLException {
    this.count = count;
    SQLBuilder builder = this.sql();

    try (Connection connection = this.dao.getConnectionSource().getConnection();
        PreparedStatement statement = connection.prepareStatement(builder.getQuery())) {

      setParameters(builder, statement);
      try (ResultSet resultSet = statement.executeQuery()) {
        resultSet.next();
        return resultSet.getLong(1);
      }
    }
  }

  /**
   * Limit select.
   *
   * @param limit the limit
   * @return the select
   */
  public Select<M> limit(int limit) {
    this.limit = limit;
    return this;
  }

  /**
   * Sql sql builder.
   *
   * @return the sql builder
   */
  public SQLBuilder sql() {
    StringJoiner selectorJoiner = new StringJoiner(" ");
    selectorJoiner.add("SELECT");

    // If there is a count in this select statement, we need to
    // check if it just trying to count all of the rows or if it were to count
    // a specific subset of the data. Support currently exists for count all rows.
    boolean countAll = this.count != null && this.count.equals("*");

    if (this.count != null) {
      selectorJoiner.add("COUNT");
      selectorJoiner.add(String.format("(%s)", this.count));
    }

    if (!countAll) {
      StringJoiner columnJoiner = new StringJoiner(", ");
      for (ParsedColumn column : dao.getParsedModel().getColumns().values()) {
        columnJoiner.add(column.getSQLName());
      }
      selectorJoiner.add(columnJoiner.toString());
    }

    selectorJoiner.add("FROM");
    selectorJoiner.add(dao.getParsedModel().getSQLTableName());

    List<SQLParameter> parameters = new ArrayList<>();
    if (this.where != null) {
      selectorJoiner.add("WHERE");
      SQLBuilder builder = this.where.sql();
      parameters.addAll(builder.getParameters());
      selectorJoiner.add(builder.getQuery());
    }

    if (!countAll) {
      selectorJoiner.add("ORDER BY");
      selectorJoiner.add(dao.getParsedModel().getPrimaryKey().getSQLName());
    }

    if (this.offset != null) {
      selectorJoiner.add("OFFSET");
      selectorJoiner.add(this.offset.toString());
      selectorJoiner.add("ROWS");
    }

    if (this.limit != null) {
      selectorJoiner.add("FETCH NEXT");
      selectorJoiner.add(this.limit.toString());
      selectorJoiner.add("ROWS ONLY");
    }


    return new SQLBuilder(selectorJoiner.toString(), parameters);
  }

  /**
   * First m.
   *
   * @return the m
   * @throws SQLException the sql exception
   */
  public M first() throws SQLException {
    this.limit = 1;
    List<M> models = execute();
    if (models.isEmpty()) {
      return null;
    } else {
      return models.get(0);
    }
  }

  /**
   * One m.
   *
   * @return the m
   * @throws SQLException the sql exception
   */
  public M one() throws SQLException {
    List<M> models = execute();
    if (models.isEmpty()) {
      throw new SQLException("Expected one result, found none");
    }
    if (models.size() > 1) {
      throw new SQLException("Expected only one result, found multiple");
    }
    return models.get(0);
  }

  /**
   * All list.
   *
   * @return the list
   * @throws SQLException the sql exception
   */
  public List<M> all() throws SQLException {
    return execute();
  }

  private List<M> execute() throws SQLException {
    SQLBuilder builder = this.sql();

    try (Connection connection = this.dao.getConnectionSource().getConnection();
        PreparedStatement statement = connection.prepareStatement(builder.getQuery())) {

      setParameters(builder, statement);

      try (ResultSet resultSet = statement.executeQuery()) {
        List<M> models = new ArrayList<>();
        while (resultSet.next()) {
          M model = dao.getParsedModel().read(resultSet);
          models.add(model);
        }
        return models;
      }
    }
  }

  private void setParameters(SQLBuilder builder, PreparedStatement statement) throws SQLException {
    int i = 1;
    for (SQLParameter parameter : builder.getParameters()) {
      Object value = parameter.getValue();
      if (value instanceof String) {
        statement.setString(i, (String) value);
      } else if (value instanceof Integer) {
        statement.setInt(i, (Integer) value);
      }
      i++;
    }
  }
}
