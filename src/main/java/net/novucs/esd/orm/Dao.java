package net.novucs.esd.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import net.novucs.esd.util.ReflectUtil;

/**
 * The type Dao.
 *
 * @param <M> the type parameter
 */
public class Dao<M> {

  private final ConnectionSource connectionSource;
  private final Class<M> modelClass;
  private final ParsedModel<M> parsedModel;

  /**
   * Instantiates a new Dao.
   *
   * @param connectionSource the connection source
   * @param modelClass       the model class
   */
  public Dao(ConnectionSource connectionSource, Class<M> modelClass) {
    this.connectionSource = connectionSource;
    this.modelClass = modelClass;
    this.parsedModel = ParsedModel.of(modelClass);
  }

  /**
   * Select by id m.
   *
   * @param id the id
   * @return the m
   * @throws SQLException the sql exception
   */
  public M selectById(int id) throws SQLException {
    return select().where(new Where().eq("id", id)).first();
  }

  /**
   * Select select.
   *
   * @return the select
   */
  public Select<M> select() {
    return new Select<M>(this);
  }

  public void delete(List<M> models) throws SQLException {
    //noinspection unchecked,SuspiciousToArrayCall
    delete((M[]) models.toArray(new Object[0]));
  }

  /**
   * Delete.
   *
   * @param models the to models delete
   * @throws SQLException the sql exception
   */
  @SafeVarargs
  public final void delete(M... models) throws SQLException {
    String query = deleteSQL();
    try (Connection connection = connectionSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      ParsedColumn primaryKeyColumn = parsedModel.getPrimaryKey();
      for (M model : models) {
        Integer primaryKey = ReflectUtil.getValue(model, primaryKeyColumn);
        statement.setInt(1, primaryKey);
        statement.addBatch();
      }
      statement.executeBatch();
    }
  }

  private String deleteSQL() {
    ParsedColumn primaryKeyColumn = parsedModel.getPrimaryKey();
    StringJoiner deleteJoiner = new StringJoiner(" ");
    deleteJoiner.add("DELETE FROM");
    deleteJoiner.add(parsedModel.getSQLTableName());
    deleteJoiner.add("WHERE");
    deleteJoiner.add(primaryKeyColumn.getSQLName());
    deleteJoiner.add("= ?");
    return deleteJoiner.toString();
  }

  /**
   * Update.
   *
   * @param model the model
   * @throws SQLException the sql exception
   */
  public void update(M model) throws SQLException {
    String query = updateSQL(model);
    try (Connection connection = connectionSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      parsedModel.setStatementValues(statement, model);
      statement.executeUpdate();
    }
  }

  private String updateSQL(M toUpdate) {
    StringJoiner updateJoiner = new StringJoiner(" ");

    updateJoiner.add("UPDATE");
    updateJoiner.add(parsedModel.getSQLTableName());

    updateJoiner.add("SET");
    StringJoiner setJoiner = new StringJoiner(", ");
    for (ParsedColumn column : parsedModel.getColumns().values()) {
      if (!column.isPrimary()) {
        setJoiner.add(column.getSQLName() + " = ?");
      }
    }
    updateJoiner.merge(setJoiner);

    updateJoiner.add("WHERE");
    ParsedColumn primaryKeyColumn = parsedModel.getPrimaryKey();
    updateJoiner.add(primaryKeyColumn.getSQLName());
    updateJoiner.add("=");
    Integer primaryKey = ReflectUtil.getValue(toUpdate, primaryKeyColumn);
    updateJoiner.add(primaryKey.toString());

    return updateJoiner.toString();
  }

  public void insert(List<M> models) throws SQLException {
    //noinspection unchecked,SuspiciousToArrayCall
    insert((M[]) models.toArray(new Object[0]));
  }

  /**
   * Insert.
   *
   * @param models the models
   * @throws SQLException the sql exception
   */
  @SafeVarargs
  public final void insert(M... models) throws SQLException {
    String query = insertSQL();
    try (Connection connection = this.connectionSource.getConnection();
        PreparedStatement statement = connection
            .prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

      for (M model : models) {
        parsedModel.setStatementValues(statement, model);
        statement.executeUpdate();

        try (ResultSet rs = statement.getGeneratedKeys()) {
          if (!rs.next()) {
            throw new SQLException("Failed to fetch all generated keys");
          }

          ParsedColumn primaryKeyColumn = parsedModel.getPrimaryKey();
          int primaryKey = rs.getInt(1);
          ReflectUtil.setValue(model, primaryKeyColumn, primaryKey);
        }
      }
    }
  }

  private String insertSQL() {
    StringJoiner queryJoiner = new StringJoiner(" ");
    queryJoiner.add("INSERT INTO");
    queryJoiner.add(parsedModel.getSQLTableName());

    StringJoiner columnJoiner = new StringJoiner(", ", "(", ")");
    for (ParsedColumn column : parsedModel.getColumns().values()) {
      if (!column.isPrimary()) {
        columnJoiner.add(column.getSQLName());
      }
    }

    queryJoiner.add(columnJoiner.toString());
    queryJoiner.add("VALUES");

    StringJoiner valueJoiner = new StringJoiner(",", "(", ")");
    for (ParsedColumn column : parsedModel.getColumns().values()) {
      if (!column.isPrimary()) {
        valueJoiner.add("?");
      }
    }

    queryJoiner.add(valueJoiner.toString());
    return queryJoiner.toString();
  }

  /**
   * Create table.
   *
   * @throws SQLException the sql exception
   */
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
    StringJoiner tableJoiner = new StringJoiner(" ");
    tableJoiner.add("CREATE TABLE");
    tableJoiner.add(parsedModel.getSQLTableName());

    StringJoiner contentsJoiner = new StringJoiner(", ", "(", ")");

    for (ParsedColumn column : parsedModel.getColumns().values()) {
      contentsJoiner.add(column.createSQL());
    }

    Map<String, List<ParsedColumn>> uniqueConstraints = new HashMap<>();

    for (ParsedColumn column : parsedModel.getColumns().values()) {
      StringJoiner constraintJoiner = new StringJoiner("");
      if (column.isPrimary()) {
        constraintJoiner.add("PRIMARY KEY (");
        constraintJoiner.add(column.getSQLName());
        constraintJoiner.add(")");
      }

      if (column.hasUniqueConstraint()) {
        List<ParsedColumn> constraints = uniqueConstraints.get(column.getUniqueConstraint());

        if (constraints == null) {
          constraints = new ArrayList<>();
        }

        constraints.add(column);
        uniqueConstraints.put(column.getUniqueConstraint(), constraints);
      }

      contentsJoiner.merge(constraintJoiner);
    }

    for (Map.Entry<String, List<ParsedColumn>> entry : uniqueConstraints.entrySet()) {
      String constraintName = entry.getKey();
      List<ParsedColumn> columns = entry.getValue();

      StringJoiner constraintJoiner = new StringJoiner(" ");
      constraintJoiner.add("CONSTRAINT");
      constraintJoiner.add(constraintName);
      constraintJoiner.add("UNIQUE");

      StringJoiner columnsJoiner = new StringJoiner(", ", "(", ")");

      for (ParsedColumn column : columns) {
        columnsJoiner.add(column.getSQLName());
      }

      constraintJoiner.add(columnsJoiner.toString());
      contentsJoiner.merge(constraintJoiner);
    }

    tableJoiner.add(contentsJoiner.toString());
    return tableJoiner.toString();
  }

  /**
   * Gets connection source.
   *
   * @return the connection source
   */
  public ConnectionSource getConnectionSource() {
    return connectionSource;
  }

  /**
   * Gets model class.
   *
   * @return the model class
   */
  public Class<M> getModelClass() {
    return modelClass;
  }

  /**
   * Gets parsed model.
   *
   * @return the parsed model
   */
  public ParsedModel<M> getParsedModel() {
    return parsedModel;
  }
}
