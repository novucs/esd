package net.novucs.esd.orm;

import java.sql.SQLException;
import java.util.Map;
import net.novucs.esd.model.User;

public class DaoManager {

  public static void main(String[] args) throws SQLException {
    Map<String, String> env = System.getenv();
    String dbUrl = env.getOrDefault("DB_URL", "jdbc:derby://localhost:1527/esd;create=true");
    String dbUser = env.getOrDefault("DB_USER", "impact");
    String dbPass = env.getOrDefault("DB_PASS", "derbypass");

    ConnectionSource connectionSource = new ConnectionSource(dbUrl, dbUser, dbPass);
    Dao<User> userDao = new Dao<>(connectionSource, User.class);
    userDao.createTable();
  }
}
