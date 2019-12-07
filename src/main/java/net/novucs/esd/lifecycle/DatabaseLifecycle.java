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
import net.novucs.esd.model.Notification;
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
      UserRole.class,
      Notification.class
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

  @SuppressWarnings("PMD.AvoidDuplicateLiterals")
  private void setupDummyUser(String name, String roleName, String password) throws SQLException {
    Role role = daoManager.get(Role.class).select().where(new Where().eq("name", roleName)).first();
    User user = new User(
        name,
        name + "@esd.net",
        Password.fromPlaintext(password),
        "1 ESD Lane",
        new DateUtil().getDateFromString("2000-01-01"),
        "ACTIVE",
        1
    );
    daoManager.get(User.class).insert(user);
    daoManager.get(UserRole.class).insert(new UserRole(user.getId(), role.getId()));
    
    if ("User".equalsIgnoreCase(name)) {
      Application application = new Application(user.getId(), BigDecimal.ZERO);
      daoManager.get(Application.class).insert(application);
    } else if ("Member".equalsIgnoreCase(name)) {
      Application application = new Application(user.getId(), BigDecimal.TEN);
      application.setStatus("APPROVED");
      daoManager.get(Application.class).insert(application);
      
      daoManager.get(Membership.class).insert(new Membership(
          user.getId(), BigDecimal.TEN, "ACTIVE", ZonedDateTime.now().minusMonths(7), true
      ));
    } else if ("NewMember".equalsIgnoreCase(name)) {
      Application application = new Application(user.getId(), BigDecimal.TEN);
      application.setStatus("APPROVED");
      daoManager.get(Application.class).insert(application);

      daoManager.get(Membership.class).insert(new Membership(
          user.getId(), BigDecimal.TEN, "ACTIVE", ZonedDateTime.now().minusMonths(1), true
      ));
    } else if ("FullMember".equalsIgnoreCase(name)) {
      Application application = new Application(user.getId(), BigDecimal.TEN);
      application.setStatus("APPROVED");
      daoManager.get(Application.class).insert(application);

      // Past membership
      daoManager.get(Membership.class).insert(new Membership(
          user.getId(), BigDecimal.TEN, "EXPIRED", ZonedDateTime.now().minusMonths(15), true
      ));

      // Current membership
      daoManager.get(Membership.class).insert(new Membership(
          user.getId(), BigDecimal.TEN, "ACTIVE", ZonedDateTime.now().minusMonths(3), false
      ));
    }
  }

  @SuppressWarnings("PMD.AvoidDuplicateLiterals")
  private boolean developmentDataExists() throws SQLException {
    return daoManager.get(Role.class)
        .select()
        .where(new Where().eq("name", "Member"))
        .first() != null;
  }

  @SuppressWarnings("PMD.AvoidDuplicateLiterals")
  public void setupDevelopmentData() throws SQLException {
    if (developmentDataExists()) {
      return;
    }

    for (String roleName : Role.DEFAULT_VALUES) {
      daoManager.get(Role.class).insert(new Role(roleName));
    }

    setupDummyUser("NewMember", "Member", "password1");
    setupDummyUser("FullMember", "Member", "password1");
    setupDummyUser("Member", "Member", "password1");
    setupDummyUser("User", "User", "password1");
    setupDummyUser("Administrator", "Administrator", "password1");

    setupDummyUser("e-simons", "Member", "221165");
    setupDummyUser("m-malcolm", "Member", "080890");
    setupDummyUser("me-aydin", "Member", "201068");
    setupDummyUser("r-french", "Member", "211268");
    setupDummyUser("m-wood", "Member", "180882");
    setupDummyUser("e-aydin", "Member", "101068");
    setupDummyUser("mem-3", "Member", "020398");
    setupDummyUser("mem-4", "Member", "070887");
    setupDummyUser("mem-5", "Member", "020388'");
    setupDummyUser("mem-6", "Member", "070897");

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
