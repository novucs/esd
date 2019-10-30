package net.novucs.esd.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map.Entry;
import java.util.StringJoiner;
import net.novucs.esd.util.ReflectUtil;


public class Dao<M> {

  private final ConnectionSource connectionSource;
  private final Class<M> modelClass;

  public Dao(ConnectionSource connectionSource, Class<M> model) {
    this.connectionSource = connectionSource;
    this.modelClass = model;
  }

  public M selectById(int id) throws SQLException {
    return select().where(new Where().eq("id", id)).one();
  }

  public Select<M> select() {
    return new Select<M>(this);
  }

  public void delete(M toDelete) throws SQLException {
    String query = deleteSQL(toDelete);
    try (Connection connection = connectionSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.executeUpdate();
    }
  }

  private String deleteSQL(M toDelete) {
    ParsedModel model = ParsedModel.of(modelClass);
    ParsedColumn primaryKeyColumn = model.getPrimaryKey();
    Integer primaryKey = ReflectUtil.getValue(toDelete, primaryKeyColumn);
    StringJoiner deleteJoiner = new StringJoiner(" ");
    deleteJoiner.add("DELETE FROM");
    deleteJoiner.add(model.getSQLTableName());
    deleteJoiner.add("WHERE");
    deleteJoiner.add(primaryKeyColumn.getName());
    deleteJoiner.add("=");
    deleteJoiner.add(primaryKey.toString());
    return deleteJoiner.toString();
  }

  public void update(M toUpdate) throws SQLException {
    String query = updateSQL(toUpdate);
    try (Connection connection = connectionSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      ParsedModel model = ParsedModel.of(modelClass);
      int i = 1;
      for (ParsedColumn column : model.getColumns().values()) {
        if (column.isPrimary()) {
          continue;
        }
        Object value = ReflectUtil.getValue(toUpdate, column);
        if (value instanceof String) {
          statement.setString(i, (String) value);
        } else if (value instanceof Integer) {
          statement.setInt(i, (Integer) value);
        }
        i++;
      }
      statement.executeUpdate();
    }
  }

  private String updateSQL(M toUpdate) {
    ParsedModel model = ParsedModel.of(modelClass);

    StringJoiner updateJoiner = new StringJoiner(" ");

    updateJoiner.add("UPDATE");
    updateJoiner.add(model.getSQLTableName());

    updateJoiner.add("SET");
    for (ParsedColumn column : model.getColumns().values()) {
      if (column.isPrimary()) {
        continue;
      }
      updateJoiner.add(column.getName());
      updateJoiner.add("= ?");
    }

    updateJoiner.add("WHERE");
    ParsedColumn primaryKeyColumn = model.getPrimaryKey();
    updateJoiner.add(primaryKeyColumn.getName());
    updateJoiner.add("=");
    Integer primaryKey = ReflectUtil.getValue(toUpdate, primaryKeyColumn);
    updateJoiner.add(primaryKey.toString());

    return updateJoiner.toString();
  }

  public void insert(M toInsert) throws SQLException {
    String query = insertSQL();
    try (Connection connection = this.connectionSource.getConnection();
        PreparedStatement statement = connection
            .prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
      ParsedModel model = ParsedModel.of(modelClass);

      int i = 1;
      for (ParsedColumn column : model.getColumns().values()) {
        if (column.isPrimary()) {
          continue;
        }
        if (column.getType() == String.class) {
          String value = ReflectUtil.getValue(toInsert, column);
          statement.setString(i, value);
        }
        i++;
      }

      statement.executeUpdate();

      try (ResultSet rs = statement.getGeneratedKeys()) {
        if (rs.next()) {
          ParsedColumn primaryKeyColumn = model.getPrimaryKey();
          int primaryKey = rs.getInt(1);
          ReflectUtil.setValue(toInsert, primaryKeyColumn, primaryKey);
        }
      }
    }
  }

  private String insertSQL() {
    ParsedModel model = ParsedModel.of(modelClass);
    StringJoiner queryJoiner = new StringJoiner(" ");
    queryJoiner.add("INSERT INTO");
    queryJoiner.add(model.getSQLTableName());

    StringJoiner columnJoiner = new StringJoiner(", ", "(", ")");
    for (ParsedColumn column : model.getColumns().values()) {
      if (!column.isPrimary()) {
        columnJoiner.add(column.getName());
      }
    }

    queryJoiner.add(columnJoiner.toString());
    queryJoiner.add("VALUES");

    StringJoiner valueJoiner = new StringJoiner(",", "(", ")");
    for (ParsedColumn column : model.getColumns().values()) {
      if (!column.isPrimary()) {
        valueJoiner.add("?");
      }
    }

    queryJoiner.add(valueJoiner.toString());
    return queryJoiner.toString();
  }

  public void createTable() throws SQLException {
    Table[] tables = this.modelClass.getAnnotationsByType(Table.class);

    if (tables.length != 1) {
      throw new IllegalStateException("Model class must have a single @Table declaration. "
          + tables.length + " found on " + this.modelClass.getName());
    }

    String query = createTableSQL();
    try (Connection connection = this.connectionSource.getConnection();
        PreparedStatement createUserTable = connection.prepareStatement(query)) {
      createUserTable.execute();
    } catch (SQLException ex) {
      // Do nothing if already exists
      // http://db.apache.org/derby/docs/10.8/ref/rrefexcept71493.html
      if (!ex.getSQLState().equals("X0Y32")) {
        throw ex;
      }
    }
  }

  private String createTableSQL() {
    ParsedModel model = ParsedModel.of(modelClass);
    StringJoiner tableJoiner = new StringJoiner(" ");
    tableJoiner.add("CREATE TABLE");
    tableJoiner.add(model.getSQLTableName());

    StringJoiner contentsJoiner = new StringJoiner(", ", "(", ")");

    for (Entry<String, ParsedColumn> entry : model.getColumns().entrySet()) {
      String columnName = entry.getKey();
      ParsedColumn column = entry.getValue();
      StringJoiner columnJoiner = new StringJoiner(" ");
      columnJoiner.add(columnName);

      if (column.isPrimary()) {
        if (column.getType() != Integer.class) {
          throw new IllegalStateException("Primary keys must be of the type Integer. "
              + model.getTableName() + "." + columnName + " was found to be a(n) " + column
              .getType());
        }

        columnJoiner.add("INT NOT NULL GENERATED ALWAYS AS IDENTITY");
        columnJoiner.add("(START WITH 1, INCREMENT BY 1)");
      } else if (column.getType() == String.class) {
        columnJoiner.add("VARCHAR(255)");
      } else if (column.getType() == Integer.class) {
        columnJoiner.add("INT");
      }

      contentsJoiner.merge(columnJoiner);
    }

    for (Entry<String, ParsedColumn> entry : model.getColumns().entrySet()) {
      // todo: add foreign key / primary key constraints here :)
      String columnName = entry.getKey();
      ParsedColumn column = entry.getValue();
      StringJoiner constraintJoiner = new StringJoiner("");

      if (column.isPrimary()) {
        constraintJoiner.add("PRIMARY KEY (");
        constraintJoiner.add(columnName);
        constraintJoiner.add(")");
      }

      contentsJoiner.merge(constraintJoiner);
    }

    tableJoiner.add(contentsJoiner.toString());
    return tableJoiner.toString();
  }

  public ConnectionSource getConnectionSource() {
    return connectionSource;
  }

  public Class<M> getModelClass() {
    return modelClass;
  }
}
