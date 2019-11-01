package net.novucs.esd.test.model;

import static net.novucs.esd.test.util.TestUtils.createTestDaoManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.novucs.esd.model.Application;
import net.novucs.esd.model.Claim;
import net.novucs.esd.model.Membership;
import net.novucs.esd.model.Role;
import net.novucs.esd.model.RolePermission;
import net.novucs.esd.model.User;
import net.novucs.esd.model.UserLog;
import net.novucs.esd.model.UserRole;
import net.novucs.esd.model.UserSession;
import net.novucs.esd.orm.DaoManager;
import org.junit.Assert;
import org.junit.Test;

public class Models {

  @Test
  public void testModelsInitialise() throws SQLException {
    DaoManager daoManager = createTestDaoManager();
    List<Class<?>> modelClasses = Arrays.asList(
        Application.class,
        Claim.class,
        Membership.class,
        Role.class,
        RolePermission.class,
        User.class,
        UserLog.class,
        UserRole.class,
        UserSession.class);
    daoManager.init(modelClasses);

    List<Class<?>> registered = new ArrayList<>();
    for (Class<?> modelClass : modelClasses) {
      if (daoManager.get(modelClass) != null) {
        registered.add(modelClass);
      }
    }

    Assert.assertEquals("DAO Manager must have initialised all the provided models "
        + "successfully", modelClasses, registered);
  }
}
