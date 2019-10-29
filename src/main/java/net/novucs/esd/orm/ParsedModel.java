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

  public LinkedHashMap<String, ParsedColumn> getColumns() {
    return columns;
  }
}
