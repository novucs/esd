package net.novucs.esd.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import net.novucs.esd.util.ReflectUtil;

// todo: support joins
public class Select<M> {

  private final transient Dao<M> dao;
  @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
  private transient Integer limit;
  @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
  private transient Where where;

  public Select(Dao<M> dao) {
    this.dao = dao;
  }

  public Select<M> where(Where where) {
    this.where = where;
    return this;
  }

  public Select<M> limit(int limit) {
    this.limit = limit;
    return this;
  }

  public SQLBuilder sql() {
    StringJoiner selectorJoiner = new StringJoiner(" ");
    selectorJoiner.add("SELECT");
    StringJoiner columnJoiner = new StringJoiner(", ");
    ParsedModel parsedModel = ParsedModel.of(dao.getModelClass());
    for (ParsedColumn column : parsedModel.getColumns().values()) {
      columnJoiner.add(column.getName());
    }

    selectorJoiner.add(columnJoiner.toString());
    selectorJoiner.add("FROM");
    selectorJoiner.add(parsedModel.getSQLTableName());

    List<SQLParameter> parameters = new ArrayList<>();
    if (this.where != null) {
      selectorJoiner.add("WHERE");
      SQLBuilder builder = this.where.sql();
      parameters.addAll(builder.getParameters());
      selectorJoiner.add(builder.getQuery());
    }

    if (this.limit != null) {
      selectorJoiner.add("FETCH FIRST");
      selectorJoiner.add(this.limit.toString());
      selectorJoiner.add("ROWS ONLY");
    }

    return new SQLBuilder(selectorJoiner.toString(), parameters);
  }

  public M first() throws SQLException {
    this.limit = 1;
    List<M> models = execute();
    if (models.isEmpty()) {
      return null;
    } else {
      return models.get(0);
    }
  }

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

  public List<M> all() throws SQLException {
    return execute();
  }

  private List<M> execute() throws SQLException {
    SQLBuilder builder = this.sql();

    try (Connection connection = this.dao.getConnectionSource().getConnection();
        PreparedStatement statement = connection.prepareStatement(builder.getQuery())) {

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

      try (ResultSet resultSet = statement.executeQuery()) {
        List<M> models = new ArrayList<>();
        while (resultSet.next()) {
          M model = getModel(resultSet);
          models.add(model);
        }
        return models;
      }
    }
  }

  private M getModel(ResultSet resultSet) throws SQLException {
    List<Object> attributes = new ArrayList<>();
    ParsedModel model = ParsedModel.of(dao.getModelClass());
    int i = 1;
    for (ParsedColumn column : model.getColumns().values()) {
      if (column.isPrimary()) {
        attributes.add(resultSet.getInt(i));
      } else if (column.getType() == String.class) {
        attributes.add(resultSet.getString(i));
      }
      i++;
    }
    return ReflectUtil.constructModel(dao.getModelClass(), attributes);
  }
}
