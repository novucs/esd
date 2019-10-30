package net.novucs.esd.orm;

import java.util.LinkedHashMap;

public class ParsedModel {

  private final String tableName;
  private final LinkedHashMap<String, ParsedColumn> columns;

  public ParsedModel(String tableName, LinkedHashMap<String, ParsedColumn> columns) {
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

  public LinkedHashMap<String, ParsedColumn> getColumns() {
    return columns;
  }
}
