package net.novucs.esd.lifecycle;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import net.novucs.esd.model.Application;
import net.novucs.esd.model.Claim;
import net.novucs.esd.model.Membership;
import net.novucs.esd.model.Role;
import net.novucs.esd.model.RolePermission;
import net.novucs.esd.model.User;
import net.novucs.esd.model.UserLog;
import net.novucs.esd.model.UserRole;
import net.novucs.esd.orm.ConnectionSource;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.DaoManager;
import net.novucs.esd.util.DateUtil;
import net.novucs.esd.util.Password;

@Startup
@Singleton
public class DatabaseLifecycle {

  public static final List<Class<?>> MODEL_CLASSES = Collections.unmodifiableList(Arrays.asList(
      Application.class,
      Claim.class,
      Membership.class,
      Role.class,
      RolePermission.class,
      User.class,
      UserLog.class,
      UserRole.class
  ));

  private DaoManager daoManager;

  @PostConstruct
  @SuppressWarnings("PMD.UnusedPrivateMethod")
  public void init() {
    Map<String, String> env = System.getenv();
    String dbUrl = env.getOrDefault("DB_URL", "jdbc:derby://localhost:1527/esd;create=true");
    String dbUser = env.getOrDefault("DB_USER", "impact");
    String dbPass = env.getOrDefault("DB_PASS", "derbypass");
    boolean developmentMode = env.getOrDefault("DEVELOPMENT_MODE", "0").equals("1");
    ConnectionSource connectionSource = new ConnectionSource(dbUrl, dbUser, dbPass);
    daoManager = new DaoManager(connectionSource);
    try {
      daoManager.init(MODEL_CLASSES);

      if (developmentMode) {
        setupDevelopmentData();
      }
    } catch (SQLException e) {
      throw new IllegalStateException("Failed to connect to database", e);
    }
  }

  @PreDestroy
  @SuppressWarnings("PMD.UnusedPrivateMethod")
  public void cleanUp() {
    // Perform app cleanup tasks here
  }

  @Produces
  public <T> Dao<T> produceDao(InjectionPoint injectionPoint) {
    ParameterizedType type = (ParameterizedType) injectionPoint.getType();
    Type argType = type.getActualTypeArguments()[0];
    @SuppressWarnings("unchecked")
    Class<T> clazz = (Class<T>) argType;
    return daoManager.get(clazz);
  }

  private void setupDevelopmentData() throws SQLException {
    DateUtil dateUtil = new DateUtil();
    ZonedDateTime dateOfBirth = dateUtil.getDateFromString("2000-01-01");
    daoManager.get(User.class).insert(new User(
        "bob",
        "bob@bob.net",
        Password.fromPlaintext("bob"),
        "boblane",
        dateOfBirth,
        "great"));
  }

  public DaoManager getDaoManager() {
    return daoManager;
  }
}
