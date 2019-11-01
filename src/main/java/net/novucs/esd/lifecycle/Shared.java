package net.novucs.esd.lifecycle;


import net.novucs.esd.model.*;
import net.novucs.esd.orm.ConnectionSource;
import net.novucs.esd.orm.DaoManager;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

@Startup
@Singleton
public class Shared {

  private static DaoManager dbManager;

  public Shared() throws SQLException {
    Map<String, String> env = System.getenv();
    String dbUrl = env.getOrDefault("DB_URL", "jdbc:derby://localhost:1527/esd;create=true");
    String dbUser = env.getOrDefault("DB_USER", "impact");
    String dbPass = env.getOrDefault("DB_PASS", "derbypass");
    dbManager = new DaoManager(new ConnectionSource(dbUrl, dbUser, dbPass));
    dbManager.init(Arrays.asList(
        User.class, Role.class, UserRole.class, Application.class, Claim.class, Membership.class,
        RolePermission.class, UserSession.class, UserLog.class
    ));
  }

  public static DaoManager db() {
    return dbManager;
  }
}
