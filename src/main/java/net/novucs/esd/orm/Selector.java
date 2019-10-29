package net.novucs.esd.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class Selector<M> {

  private final Dao<M> dao;
  private Integer limit = null;
  private Where where = null;

  public Selector(Dao<M> dao) {
    this.dao = dao;
  }

  public Selector<M> where(Where where) {
    this.where = where;
    return this;
  }

  public Selector<M> limit(int limit) {
    this.limit = limit;
    return this;
  }

  public String sql() {
    StringJoiner selectorJoiner = new StringJoiner("");
    selectorJoiner.add("SELECT ");
    StringJoiner columnJoiner = new StringJoiner(", ");
    for (ParsedColumn column : this.dao.getParsedModel().getColumns().values()) {
      columnJoiner.add(column.getName());
    }

    selectorJoiner.add(columnJoiner.toString());
    selectorJoiner.add(" FROM \"");
    selectorJoiner.add(this.dao.getParsedModel().getTableName());
    selectorJoiner.add("\" ");

    if (this.where != null) {
      selectorJoiner.add(" WHERE ");
      selectorJoiner.add(this.where.sql());
    }

    if (this.limit != null) {
      selectorJoiner.add(" LIMIT ");
      selectorJoiner.add(this.limit.toString());
    }

    return selectorJoiner.toString();
  }

//  private void nestedSetQueryParameter(PreparedStatement statement, Where where) {
//    for (Clause clause : where.getClauses()) {
//
//    }
//  }

  public M first() throws SQLException {
    Connection connection = this.dao.getConnectionSource().getConnection();
    String sql = this.sql();
    System.out.println(sql);
    PreparedStatement statement = connection.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();
    List<Object> values = new ArrayList<>();
    int i = 1;

    if (resultSet.next()) {
      ParsedModel model = this.dao.getParsedModel();
      for (ParsedColumn column : model.getColumns().values()) {
        if (column.isPrimary()) {
          values.add(resultSet.getInt(i));
        } else if (column.getType() == String.class) {
          values.add(resultSet.getString(i));
        }
        i++;
      }
    }

    return this.dao.constructModel(values);
  }

//  public M one() {
//
//  }
//
//  public List<M> all() {
//
//  }
}
