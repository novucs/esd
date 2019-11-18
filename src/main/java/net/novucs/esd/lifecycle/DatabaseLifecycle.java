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

  public void setupDevelopmentData() throws SQLException {
    DateUtil dateUtil = new DateUtil();
    ZonedDateTime dateOfBirth = dateUtil.getDateFromString("2000-01-01");

    for (String roleName : Arrays
        .asList("User", "Member", "NewMember", "FullMember", "Administrator")) {

      Role role = new Role(
          roleName.equalsIgnoreCase("NewMember")
              || roleName.equalsIgnoreCase("FullMember") ? "Member" : roleName);

      daoManager.get(Role.class).insert(role);
      User user = new User(
          roleName + "Account",
          roleName + "@esd.net",
          Password.fromPlaintext("password1"),
          "1 ESD Lane",
          dateOfBirth,
          "ACTIVE",
          1
      );
      daoManager.get(User.class).insert(user);
      daoManager.get(UserRole.class).insert(new UserRole(user.getId(), role.getId()));

      if (roleName.equalsIgnoreCase("NewMember")) {
        Membership membership = new Membership(
            user.getId(),
            new BigDecimal("0"),
            "ACTIVE",
            ZonedDateTime.now().minusMonths(1),
            true
        );
        daoManager.get(Membership.class).insert(membership);
      } else if (roleName.equalsIgnoreCase("FullMember")) {
        Membership pastMembership = new Membership(
            user.getId(),
            new BigDecimal("0"),
            "EXPIRED",
            ZonedDateTime.now().minusMonths(15),
            true
        );
        Membership currentMembership = new Membership(
            user.getId(),
            new BigDecimal("0"),
            "ACTIVE",
            ZonedDateTime.now().minusMonths(3),
            false
        );
        daoManager.get(Membership.class).insert(pastMembership);
        daoManager.get(Membership.class).insert(currentMembership);
      }
    }
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
