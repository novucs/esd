package net.novucs.esd.orm;

import static net.novucs.esd.util.StringUtil.camelToSnake;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;

public class Dao<M> {

  private final ConnectionSource connectionSource;
  private final Class<M> modelClass;

  public Dao(ConnectionSource connectionSource, Class<M> model) {
    this.connectionSource = connectionSource;
    this.modelClass = model;
  }

  public void createTable() throws SQLException {
    Table[] tables = this.modelClass.getAnnotationsByType(Table.class);

    if (tables.length != 1) {
      throw new IllegalStateException("Model class must have a single @Table declaration. "
          + tables.length + " found on " + this.modelClass.getName());
    }

    Table table = tables[0];
    Map<String, ParsedColumn> fields = parseModelClass();

//    "CREATE TABLE person ("
//        + "id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
//        + "name VARCHAR(255), "
//        + "PRIMARY KEY (id))"

    String query = createTableSQL(fields);
    System.out.println(query);
    Connection connection = connectionSource.getConnection();

    PreparedStatement createUserTable = connection.prepareStatement(query);
    createUserTable.execute();
  }

  private String createTableSQL(Map<String, ParsedColumn> fields) {
    String tableName = camelToSnake(this.modelClass.getSimpleName());
    StringJoiner tableJoiner = new StringJoiner("");
    tableJoiner.add("CREATE TABLE \"");
    tableJoiner.add(tableName);
    tableJoiner.add("\" ");

    StringJoiner contentsJoiner = new StringJoiner(", ", "(", ")");

    for (Entry<String, ParsedColumn> entry : fields.entrySet()) {
      String columnName = entry.getKey();
      ParsedColumn column = entry.getValue();
      StringJoiner columnJoiner = new StringJoiner(" ");
      columnJoiner.add(columnName);

      if (column.primary) {
        if (column.type != Integer.class) {
          throw new IllegalStateException("Primary keys must be of the type Integer. "
              + tableName + "." + columnName + " was found to be a(n) " + column.type);
        }

        columnJoiner
            .add("INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)");
      } else if (column.type == String.class) {
        columnJoiner.add("VARCHAR(255)");
      } else if (column.type == Integer.class) {
        columnJoiner.add("INT");
      }

      contentsJoiner.merge(columnJoiner);
    }

    for (Entry<String, ParsedColumn> entry : fields.entrySet()) {
      // todo: add foreign key / primary key constraints here :)
      String columnName = entry.getKey();
      ParsedColumn column = entry.getValue();
      StringJoiner constraintJoiner = new StringJoiner("");

      if (column.primary) {
        constraintJoiner.add("PRIMARY KEY (");
        constraintJoiner.add(columnName);
        constraintJoiner.add(")");
      }

      contentsJoiner.merge(constraintJoiner);
    }

    tableJoiner.add(contentsJoiner.toString());
    return tableJoiner.toString();
  }

  private Map<String, ParsedColumn> parseModelClass() {
    Map<String, ParsedColumn> fields = new HashMap<>();

    for (Field field : this.modelClass.getDeclaredFields()) {
      Column[] columns = field.getAnnotationsByType(Column.class);
      if (columns.length > 1) {
        throw new IllegalStateException("Fields should only have one @Column declaration. "
            + columns.length + " found on " + this.modelClass.getName() + " " + field.getName());
      }
      if (columns.length == 1) {
        Column column = columns[0];
        String columnName = camelToSnake(field.getName());
        Class<?> type = field.getType();
        fields.put(columnName, new ParsedColumn(type, columnName, column.primary()));
      }
    }
    return fields;
  }

  static class ParsedColumn {

    final Class<?> type;
    final String name;
    final boolean primary;

    ParsedColumn(Class<?> type, String name, boolean primary) {
      this.type = type;
      this.name = name;
      this.primary = primary;
    }
  }
}
