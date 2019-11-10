package net.novucs.esd.orm;

import static net.novucs.esd.util.StringUtil.camelToSnake;
import static net.novucs.esd.util.StringUtil.quoted;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.novucs.esd.util.ReflectUtil;

/**
 * The type Parsed model.
 *
 * @param <M> the type parameter
 */
public class ParsedModel<M> {

  private static final Map<Class<?>, ParsedModel> MODEL_CACHE = new ConcurrentHashMap<>();
  private final Class<M> modelClass;
  private final String tableName;
  private final Map<String, ParsedColumn> columns;

  /**
   * Instantiates a new Parsed model.
   *
   * @param modelClass the model class
   * @param tableName  the table name
   * @param columns    the columns
   */
  public ParsedModel(Class<M> modelClass, String tableName, Map<String, ParsedColumn> columns) {
    this.modelClass = modelClass;
    this.tableName = tableName;
    this.columns = columns;
  }

  /**
   * Gets table name.
   *
   * @return the table name
   */
  public String getTableName() {
    return tableName;
  }

  /**
   * Gets sql table name.
   *
   * @return the sql table name
   */
  public String getSQLTableName() {
    return quoted(tableName);
  }

  /**
   * Gets primary key.
   *
   * @return the primary key
   */
  public ParsedColumn getPrimaryKey() {
    for (ParsedColumn column : columns.values()) {
      if (column.isPrimary()) {
        return column;
      }
    }
    throw new IllegalStateException("Table " + tableName + " does not have a primary key");
  }

  /**
   * Gets columns.
   *
   * @return the columns
   */
  public Map<String, ParsedColumn> getColumns() {
    return columns;
  }

  /**
   * Of parsed model.
   *
   * @param <M>        the type parameter
   * @param modelClass the model class
   * @return the parsed model
   */
  public static <M> ParsedModel<M> of(Class<M> modelClass) {
    if (MODEL_CACHE.containsKey(modelClass)) {
      //noinspection unchecked
      return (ParsedModel<M>) MODEL_CACHE.get(modelClass);
    }

    LinkedHashMap<String, ParsedColumn> columns = new LinkedHashMap<>();

    for (Field field : modelClass.getDeclaredFields()) {
      Column[] columnAnnotations = field.getAnnotationsByType(Column.class);
      if (columnAnnotations.length == 0) {
        continue;
      }

      if (columnAnnotations.length > 1) {
        throw new IllegalStateException("Fields should only have one @Column declaration. "
            + columnAnnotations.length + " found on " + modelClass.getName() + " " + field
            .getName());
      }

      Column column = columnAnnotations[0];
      Class<?> type = field.getType();
      Class<?> foreignReference = column.foreign() == void.class ? null : column.foreign();
      ParsedColumn parsedColumn = new ParsedColumn(
          type, field.getName(), column.primary(), foreignReference, column.nullable());
      columns.put(field.getName(), parsedColumn);
    }

    String tableName = camelToSnake(modelClass.getSimpleName());
    ParsedModel<M> parsedModel = new ParsedModel<>(modelClass, tableName, columns);
    MODEL_CACHE.put(modelClass, parsedModel);
    return parsedModel;
  }

  /**
   * Sets statement values.
   *
   * @param statement the statement
   * @param model     the model
   * @throws SQLException the sql exception
   */
  public void setStatementValues(PreparedStatement statement, M model) throws SQLException {
    int i = 1;
    for (ParsedColumn column : this.getColumns().values()) {
      if (column.write(statement, model, i)) {
        i++;
      }
    }
  }

  /**
   * Read m.
   *
   * @param resultSet the result set
   * @return the m
   * @throws SQLException the sql exception
   */
  public M read(ResultSet resultSet) throws SQLException {
    List<Object> attributes = new ArrayList<>();
    int i = 1;
    for (ParsedColumn column : this.getColumns().values()) {
      attributes.add(column.read(resultSet, i));
      i++;
    }
    return ReflectUtil.constructModel(modelClass, attributes);
  }

  /**
   * Gets model class.
   *
   * @return the model class
   */
  public Class<M> getModelClass() {
    return modelClass;
  }
}
