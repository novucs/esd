package net.novucs.esd.orm;

import java.sql.SQLException;
import java.util.List;
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

    // See if we can get a result for Bob
    List<User> allUsers = userDao.select()
        .where(new Where()
            .eq("name", "bob")
            .and()
            .eq("id", 1)
            .or()
            .eq("id", 2)
        )
        .limit(2)
        .all();
    System.out.println(allUsers);

    User bobNameChange = userDao.selectById(1);
    bobNameChange.setName("bob");
    userDao.update(bobNameChange);
    System.out.println(bobNameChange.getId() + " : " + bobNameChange.getName());
    bobNameChange.setName("steve");
    userDao.update(bobNameChange);
    bobNameChange = userDao.selectById(1);
    System.out.println(bobNameChange.getId() + " : " + bobNameChange.getName());

    User jeff = new User("jeff");
    userDao.insert(jeff);
    System.out.println(jeff.getId() + " : " + jeff.getName());
    User user1 = userDao.select().where(new Where().eq("id", jeff.getId())).first();
    System.out.println("before delete:" + user1);
    userDao.delete(jeff);
    User user2 = userDao.select().where(new Where().eq("id", jeff.getId())).first();
    System.out.println("after delete:" + user2);
  }
}
