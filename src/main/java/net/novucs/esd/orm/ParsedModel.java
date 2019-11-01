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

public class ParsedModel<M> {

  private static final Map<Class<?>, ParsedModel> MODEL_CACHE = new ConcurrentHashMap<>();
  private final Class<M> modelClass;
  private final String tableName;
  private final Map<String, ParsedColumn> columns;

  public ParsedModel(Class<M> modelClass, String tableName, Map<String, ParsedColumn> columns) {
    this.modelClass = modelClass;
    this.tableName = tableName;
    this.columns = columns;
  }

  public String getTableName() {
    return tableName;
  }

  public String getSQLTableName() {
    return quoted(tableName);
  }

  public ParsedColumn getPrimaryKey() {
    for (ParsedColumn column : columns.values()) {
      if (column.isPrimary()) {
        return column;
      }
    }
    throw new IllegalStateException("Table " + tableName + " does not have a primary key");
  }

  public Map<String, ParsedColumn> getColumns() {
    return columns;
  }

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

  public void setStatementValues(PreparedStatement statement, M model) throws SQLException {
    int i = 1;
    for (ParsedColumn column : this.getColumns().values()) {
      if (column.write(statement, model, i)) {
        i++;
      }
    }
  }

  public M read(ResultSet resultSet) throws SQLException {
    List<Object> attributes = new ArrayList<>();
    int i = 1;
    for (ParsedColumn column : this.getColumns().values()) {
      attributes.add(column.read(resultSet, i));
      i++;
    }
    return ReflectUtil.constructModel(modelClass, attributes);
  }

  public Class<M> getModelClass() {
    return modelClass;
  }
}
