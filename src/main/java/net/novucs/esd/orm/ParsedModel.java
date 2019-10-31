package net.novucs.esd.orm;

import static net.novucs.esd.util.StringUtil.camelToSnake;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ParsedModel {

  private static final Map<Class<?>, ParsedModel> MODEL_CACHE = new ConcurrentHashMap<>();
  private final String tableName;
  private final Map<String, ParsedColumn> columns;

  public ParsedModel(String tableName, Map<String, ParsedColumn> columns) {
    this.tableName = tableName;
    this.columns = columns;
  }

  public String getTableName() {
    return tableName;
  }

  public String getSQLTableName() {
    return "\"" + tableName + "\"";
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

  public static ParsedModel of(Class<?> model) {
    if (MODEL_CACHE.containsKey(model)) {
      return MODEL_CACHE.get(model);
    }

    LinkedHashMap<String, ParsedColumn> fields = new LinkedHashMap<>();

    for (Field field : model.getDeclaredFields()) {
      Column[] columns = field.getAnnotationsByType(Column.class);
      if (columns.length > 1) {
        throw new IllegalStateException("Fields should only have one @Column declaration. "
            + columns.length + " found on " + model.getName() + " " + field.getName());
      }
      if (columns.length == 1) {
        Column column = columns[0];
        Class<?> type = field.getType();
        Class<?> foreignReference = column.foreign() == void.class ? null : column.foreign();
        ParsedColumn parsedColumn = new ParsedColumn(
            type, field.getName(), column.primary(), foreignReference);
        fields.put(field.getName(), parsedColumn);
      }
    }

    String tableName = camelToSnake(model.getSimpleName());
    ParsedModel parsedModel = new ParsedModel(tableName, fields);
    MODEL_CACHE.put(model, parsedModel);
    return parsedModel;
  }
}
