package net.novucs.esd.util;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import net.novucs.esd.model.Application;
import net.novucs.esd.model.Claim;
import net.novucs.esd.model.Membership;
import net.novucs.esd.model.Role;
import net.novucs.esd.model.RolePermission;
import net.novucs.esd.model.User;
import net.novucs.esd.model.UserLog;
import net.novucs.esd.model.UserRole;
import net.novucs.esd.model.UserSession;
import net.novucs.esd.orm.ConnectionSource;
import net.novucs.esd.orm.DaoManager;

public final class DBUtil {

  private static DaoManager dbManager;

  private DBUtil() {
    throw new IllegalStateException();
  }

  public static DaoManager get() throws SQLException {
    if (dbManager != null) {
      return dbManager;
    }

    Map<String, String> env = System.getenv();
    String dbUrl = env.getOrDefault("DB_URL", "jdbc:derby://localhost:1527/esd;create=true");
    String dbUser = env.getOrDefault("DB_USER", "impact");
    String dbPass = env.getOrDefault("DB_PASS", "derbypass");
    dbManager = new DaoManager(new ConnectionSource(dbUrl, dbUser, dbPass));
    dbManager.init(Arrays.asList(
        User.class, Role.class, UserRole.class, Application.class, Claim.class, Membership.class,
        RolePermission.class, UserSession.class, UserLog.class
    ));
    return dbManager;
  }
}
