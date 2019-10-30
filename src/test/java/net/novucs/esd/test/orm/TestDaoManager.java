package net.novucs.esd.test.orm;

import java.sql.SQLException;
import java.util.List;
import net.novucs.esd.model.User;
import net.novucs.esd.orm.ConnectionSource;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Where;
import org.junit.Test;

public class TestDaoManager {

  @Test
  public void testRequestGetsMapAttribute() throws SQLException {
    String dbUrl = "jdbc:derby:memory:testDB;create=true";
    String dbUser = "impact";
    String dbPass = "derbypass";

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

    userDao.insert(new User("old name"));
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
