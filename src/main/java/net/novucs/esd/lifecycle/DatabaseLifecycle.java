package net.novucs.esd.lifecycle;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
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
import net.novucs.esd.model.Payment;
import net.novucs.esd.model.Role;
import net.novucs.esd.model.RolePermission;
import net.novucs.esd.model.User;
import net.novucs.esd.model.UserLog;
import net.novucs.esd.model.UserRole;
import net.novucs.esd.orm.ConnectionSource;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.DaoManager;
import net.novucs.esd.orm.Where;
import net.novucs.esd.util.DateUtil;
import net.novucs.esd.util.Password;

/**
 * The type Database lifecycle.
 */
@Startup
@Singleton
public class DatabaseLifecycle {

  /**
   * The constant MODEL_CLASSES.
   */
  public static final List<Class<?>> MODEL_CLASSES = Collections.unmodifiableList(Arrays.asList(
      Application.class,
      Claim.class,
      Membership.class,
      Payment.class,
      Role.class,
      RolePermission.class,
      User.class,
      UserLog.class,
      UserRole.class
  ));

  private DaoManager daoManager;

  /**
   * Init.
   */
  @PostConstruct
  @SuppressWarnings("PMD.UnusedPrivateMethod")
  public void init() {
    Map<String, String> env = System.getenv();
    String dbUrl = env.getOrDefault("DB_URL", "jdbc:derby://localhost:1527/esd;create=true");
    String dbUser = env.getOrDefault("DB_USER", "impact");
    String dbPass = env.getOrDefault("DB_PASS", "derbypass");
    boolean developmentMode = env.getOrDefault("DEVELOPMENT_MODE", "1").equals("1");
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

  /**
   * Clean up.
   */
  @PreDestroy
  @SuppressWarnings("PMD.UnusedPrivateMethod")
  public void cleanUp() {
    // Perform app cleanup tasks here
  }

  /**
   * Produce dao dao.
   *
   * @param <T>            the type parameter
   * @param injectionPoint the injection point
   * @return the dao
   */
  @Produces
  public <T> Dao<T> produceDao(InjectionPoint injectionPoint) {
    ParameterizedType type = (ParameterizedType) injectionPoint.getType();
    Type argType = type.getActualTypeArguments()[0];
    @SuppressWarnings("unchecked")
    Class<T> clazz = (Class<T>) argType;
    return daoManager.get(clazz);
  }

  private void setupDummyUser(String name, String roleName) throws SQLException {
    Role role = daoManager.get(Role.class).select().where(new Where().eq("name", roleName)).first();
    User user = new User(
        name,
        name + "@esd.net",
        Password.fromPlaintext("password1"),
        "1 ESD Lane",
        new DateUtil().getDateFromString("2000-01-01"),
        "ACTIVE",
        1
    );
    daoManager.get(User.class).insert(user);
    daoManager.get(UserRole.class).insert(new UserRole(user.getId(), role.getId()));

    if ("NewMember".equalsIgnoreCase(roleName)) {
      daoManager.get(Membership.class).insert(new Membership(
          user.getId(), BigDecimal.ZERO, "ACTIVE", ZonedDateTime.now().minusMonths(1), true
      ));
    }

    if ("FullMember".equalsIgnoreCase(roleName)) {
      // Past membership
      daoManager.get(Membership.class).insert(new Membership(
          user.getId(), BigDecimal.ZERO, "EXPIRED", ZonedDateTime.now().minusMonths(15), true
      ));

      // Current membership
      daoManager.get(Membership.class).insert(new Membership(
          user.getId(), BigDecimal.ZERO, "ACTIVE", ZonedDateTime.now().minusMonths(3), false
      ));
    }
  }

  private boolean developmentDataExists() throws SQLException {
    return daoManager.get(Role.class)
        .select()
        .where(new Where().eq("name", "NewMember"))
        .first() != null;
  }

  public void setupDevelopmentData() throws SQLException {
    if (developmentDataExists()) {
      return;
    }

    for (String roleName : Role.DEFAULT_VALUES) {
      daoManager.get(Role.class).insert(new Role(roleName));
      setupDummyUser(roleName, roleName);
    }

    setupDummyUser("Larry", Role.DEFAULT_VALUES.get(0));
    setupDummyUser("Garry", Role.DEFAULT_VALUES.get(1));
    setupDummyUser("Harry", Role.DEFAULT_VALUES.get(2));
    setupDummyUser("Barry", Role.DEFAULT_VALUES.get(3));
    setupDummyUser("Jeff", Role.DEFAULT_VALUES.get(4));
  }

  /**
   * Gets dao manager.
   *
   * @return the dao manager
   */
  public DaoManager getDaoManager() {
    return daoManager;
  }
}
