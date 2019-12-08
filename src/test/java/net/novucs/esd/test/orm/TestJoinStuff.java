package net.novucs.esd.test.orm;

import static net.novucs.esd.test.util.TestUtils.createTestDaoManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import net.novucs.esd.lifecycle.DatabaseLifecycle;
import net.novucs.esd.model.Role;
import net.novucs.esd.model.User;
import net.novucs.esd.model.UserRole;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.DaoManager;
import net.novucs.esd.orm.Where;
import net.novucs.esd.util.ReflectUtil;
import org.junit.Test;

/**
 * The type Test dao manager.
 */
public class TestJoinStuff {

  @Test
  public void testLoginSuccess() throws SQLException, ReflectiveOperationException {
    DaoManager dm = createTestDaoManager();
    dm.init(DatabaseLifecycle.MODEL_CLASSES);

    DatabaseLifecycle databaseLifecycle = new DatabaseLifecycle();
    ReflectUtil.setFieldValue(databaseLifecycle, "daoManager", dm);

    for (int i = 0; i < 5; i++) {
      databaseLifecycle.setupDevelopmentData();
    }

    Dao<User> userDao = dm.get(User.class);
    Dao<UserRole> userRoleDao = dm.get(UserRole.class);
    Dao<Role> roleDao = dm.get(Role.class);

    List<User> users = userDao.select().where(new Where().search("admin esd", "name", "email")).all();

    for (User user : users) {
      System.out.println(user.getId() + " " + user.getName());
    }



//    List<UserRole> userRoles = userRoleDao.select().where(new Where().eq("user_id", user.getId())).all();
//    List<Role> roles = userRoles.stream().map(userRole -> {
//      try {
//        return roleDao.selectById(userRole.getRoleId());
//      } catch (SQLException e) {
//        Logger.getLogger(getClass().getName()).log(Level.WARNING, "Failed to get role", e);
//        return null;
//      }
//    }).collect(Collectors.toList());
//
//    if (roles.contains(null)) {
//      throw new SQLException("Failed to load user roles");
//    }

//    System.out.println(roles);
  }
}
