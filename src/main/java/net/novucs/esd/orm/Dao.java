package net.novucs.esd.orm;

import static net.novucs.esd.util.StringUtil.camelToSnake;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringJoiner;


public class Dao<M> {

  private final ConnectionSource connectionSource;
  private final Class<M> modelClass;
  private ParsedModel parsedModel = null;

  public Dao(ConnectionSource connectionSource, Class<M> model) {
    this.connectionSource = connectionSource;
    this.modelClass = model;
  }

  public M constructModel(List<Object> modelAttributes) {
    try {
      M model = this.modelClass.getConstructor().newInstance();
      Iterator<ParsedColumn> columns = this.getParsedModel().getColumns().values().iterator();
      Iterator<Object> valuesIterator = modelAttributes.iterator();

      while (columns.hasNext() && valuesIterator.hasNext()) {
        Object value = valuesIterator.next();
        ParsedColumn column = columns.next();

        Field field = this.modelClass.getDeclaredField(column.getName());
        field.setAccessible(true);
        field.set(model, value);
        field.setAccessible(false);
      }

      return model;
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException("Model classes must have an empty public constructor. "
          + modelClass.getSimpleName() + " violates this requirement.");
    }
  }

// todo: select, update, delete,

  // SELECT columnNames FROM tableName WHERE columnName1 = value1 LIMIT x GROUP BY y

  /*+

  SELECT
     id,
     name,
     age
   FROM
     user u
   INNER JOIN
     user_roles ur ON
       u.id = ur.uid
   WHERE
     name = 'bob'
   ;
   */


  public void selectById(int id) throws SQLException {
  }

  public Select<M> select() throws SQLException {
    return new Select<M>(this);
  }

  /*
  id condition and (blah == "xd" || something == "fldjsfdkshfjkdshbgfjhdks")

  SELECT id FROM user WHERE name = "bob" AND (age > 10 AND age < 20) LIMIT 3;

  Dao<User> userDao = new Dao<>();
  userDao.select()
    .where(Where()
      .eq("name", "bob")
      .and()
      .wrapper(Where()....)
      .eq("email", "blah@blah.com"))
    .limit(10)
   */

  // SELECT COUNT(*) FROM tableName;
  // UPDATE tableName SET columnName = value, columnName2 = value2 WHERE someColumn = someValue LIMIT x;
  // DELETE FROM tableName WHERE columnName = value LIMIT x;

  public void insert(M toInsert) throws SQLException {
    String query = insertSQL();
    Connection connection = this.connectionSource.getConnection();
    PreparedStatement statement = connection.prepareStatement(query);
    ParsedModel model = getParsedModel();

    int i = 1;

    for (ParsedColumn column : model.getColumns().values()) {
      if (column.isPrimary()) {
        continue;
      }

      if (column.getType() == String.class) {
        String value = getValue(toInsert, column);
        statement.setString(i, value);
      }

      i++;
    }

    statement.execute();
  }

  private <T> T getValue(M model, ParsedColumn column) {
    try {
      Field field = this.modelClass.getDeclaredField(column.getName());
      field.setAccessible(true);
      Object value = field.get(model);
      field.setAccessible(false);
      //noinspection unchecked
      return (T) value;
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new IllegalStateException("Model class has somehow changed during runtime? :/");
    }
  }

  private String insertSQL() {
    ParsedModel model = getParsedModel();
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

    Connection connection = this.connectionSource.getConnection();
    String query = createTableSQL();

    try (PreparedStatement createUserTable = connection.prepareStatement(query)) {
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
    ParsedModel model = getParsedModel();
    StringJoiner tableJoiner = new StringJoiner("");
    tableJoiner.add("CREATE TABLE \"");
    tableJoiner.add(model.getTableName());
    tableJoiner.add("\" ");

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

  public ParsedModel getParsedModel() {
    if (this.parsedModel != null) {
      return this.parsedModel;
    }

    String tableName = camelToSnake(this.modelClass.getSimpleName());
    LinkedHashMap<String, ParsedColumn> fields = new LinkedHashMap<>();

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

    return new ParsedModel(tableName, fields);
  }
}
