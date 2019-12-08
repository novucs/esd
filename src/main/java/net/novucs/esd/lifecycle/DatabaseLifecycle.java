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
import net.novucs.esd.model.ApplicationStatus;
import net.novucs.esd.model.Claim;
import net.novucs.esd.model.ClaimStatus;
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
import net.novucs.esd.util.StringUtil;

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
    Role role = daoManager.get(Role.class)
        .select().where(new Where().eq("name", roleName)).first();
    User user = new User(
        name,
        StringUtil.parseUsername(name),
        name.replace(" ", "") + "@esd.net",
        Password.fromPlaintext(password),
        "1 ESD Lane",
        new DateUtil().getDateFromString("2000-01-01"),
        "ACTIVE",
        1
    );
    daoManager.get(User.class).insert(user);
    daoManager.get(UserRole.class).insert(new UserRole(user.getId(), role.getId()));

    if ("User".equalsIgnoreCase(name)) {
      Application application = new Application(user.getId());
      daoManager.get(Application.class).insert(application);
    } else if ("Approved User".equalsIgnoreCase(name)) {
      Application application = new Application(user.getId());
      application.setStatus(ApplicationStatus.APPROVED);
      daoManager.get(Application.class).insert(application);
    } else if ("Member".equalsIgnoreCase(name)) {
      Application application = new Application(user.getId());
      application.setStatus(ApplicationStatus.APPROVED);
      daoManager.get(Application.class).insert(application);

      daoManager.get(Membership.class).insert(new Membership(
          user.getId(),
          ZonedDateTime.now().minusMonths(7),
          true
      ));
    } else if ("New Member".equalsIgnoreCase(name)) {
      Application application = new Application(user.getId());
      application.setStatus(ApplicationStatus.PAID);
      daoManager.get(Application.class).insert(application);
      daoManager.get(Membership.class).insert(new Membership(
          user.getId(),
          ZonedDateTime.now().minusMonths(1),
          true
      ));
    } else if ("Full Member".equalsIgnoreCase(name)) {
      Application application = new Application(user.getId());
      application.setStatus(ApplicationStatus.APPROVED);
      daoManager.get(Application.class).insert(application);
      // Current membership
      daoManager.get(Membership.class).insert(new Membership(
          user.getId(),
          ZonedDateTime.now().minusMonths(3),
          false
      ));
    } else if ("Expired Member".equalsIgnoreCase(name)) {
      Application application = new Application(user.getId());
      application.setStatus(ApplicationStatus.APPROVED);
      daoManager.get(Application.class).insert(application);
      // Current membership
      daoManager.get(Membership.class).insert(new Membership(
          user.getId(),
          ZonedDateTime.now().minusMonths(13),
          false
      ));
    }
  }

  private User setupUser(String name, String roleName, String password, String address,
      String dateOfBirth) throws SQLException {
    Role role = daoManager.get(Role.class)
        .select().where(new Where().eq("name", roleName)).first();
    User user = new User(
        name,
        StringUtil.parseUsername(name),
        name.replace(" ", "") + "@esd.net",
        Password.fromPlaintext(password),
        address,
        new DateUtil().getDateFromString(dateOfBirth),
        "ACTIVE",
        1
    );
    daoManager.get(User.class).insert(user);
    daoManager.get(UserRole.class).insert(new UserRole(user.getId(), role.getId()));

    // Setup user's membership
    Application application = new Application(user.getId());
    application.setStatus(ApplicationStatus.APPROVED);
    daoManager.get(Application.class).insert(application);
    // Current membership
    daoManager.get(Membership.class).insert(new Membership(
        user.getId(),
        ZonedDateTime.now().minusMonths(3),
        false
    ));

    return user;
  }

  private void pushClaim(Integer userId, String date, String rationale, Integer amount)
      throws SQLException {
    Membership userMembership = daoManager.get(Membership.class).select()
        .where(new Where().eq("user_id", userId)).first();
    if (userMembership == null) {
      // Shouldn't ever hit this but just incase it does...
      return;
    }

    // Insert our claim
    daoManager.get(Claim.class).insert(new Claim(
        userMembership.getId(),
        BigDecimal.valueOf(amount),
        new DateUtil().getDateFromString(date),
        ClaimStatus.APPROVED,
        rationale
    ));
  }

  private void pushPayment(Integer userId, String paymentDateTime)
      throws SQLException {
    daoManager.get(Payment.class).insert(new Payment(
        userId,
        BigDecimal.valueOf(Membership.ANNUAL_FEE_POUNDS),
        null,
        "FEE",
        new DateUtil().getDateTimeFromString(paymentDateTime)
    ));
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

    // Dummy Users
    setupDummyUser("New Member", "Member", "password1");
    setupDummyUser("Full Member", "Member", "password1");
    setupDummyUser("Expired Member", "Member", "password1");
    setupDummyUser("Member", "Member", "password1");
    setupDummyUser("User", "User", "password1");
    setupDummyUser("Approved Member", "User", "password1");
    setupDummyUser("Administrator", "Administrator", "password1");

    // ESD Users
    User eSimons = setupUser("Edward Simons", "Member", "221165",
        "123 Kings Street, Aberdeen, AB12 2AB", "1965-11-22");
    User mMalcolm = setupUser("Michael Malcolm", "Member", "080890",
        "3 London Road, Luton, LU1 1QY", "1990-08-08");
    User meAydin = setupUser("Mehmet Edward Aydin", "Member", "201068",
        "29 Station Rd, London, N3 2SG", "1968-10-20");
    User rFrench = setupUser("Rob French", "Member", "211268",
        "13 Stafford Street, Aberdeen, AB12 1AQ", "1968-12-21");
    User mWood = setupUser("Mike Wood", "Member", "180882",
        "10 London Avenue, Luton, LU12 3SB", "1982-08-18");
    User eAydin = setupUser("Emin Aydin", "Member", "101068",
        "148 Station Rd, London, N3 2SG", "1968-10-10");
    User member3 = setupUser("Member 3", "Member", "020398",
        "Address Three", "1998-03-02");
    User member4 = setupUser("Member 4", "Member", "070887",
        "Address Four", "1987-07-08");
    User member5 = setupUser("Member 5", "Member", "020388",
        "Address Five", "1988-03-02");
    User member6 = setupUser("Member 6", "Member", "070897",
        "Address Six", "1997-07-08");

    // Add Payments
    pushPayment(eSimons.getId(), "2015-01-07 10:08:21");
    pushPayment(mMalcolm.getId(), "2015-01-24 11:28:25");
    pushPayment(meAydin.getId(), "2015-01-26 18:00:00");
    pushPayment(rFrench.getId(), "2015-01-28 09:12:00");
    pushPayment(mWood.getId(), "2015-10-25 08:44:13");
    pushPayment(eAydin.getId(), "2015-10-26 10:08:21");
    pushPayment(eSimons.getId(), "2016-01-25 11:00:00");
    pushPayment(mMalcolm.getId(), "2016-01-25 11:18:21");
    pushPayment(meAydin.getId(), "2016-02-05 16:38:13");
    pushPayment(mWood.getId(), "2016-10-12 09:44:18");
    pushPayment(eAydin.getId(), "2016-10-20 14:42:45");
    pushPayment(member3.getId(), "2016-01-23 01:01:01");
    pushPayment(member4.getId(), "2016-05-16 11:13:11");
    pushPayment(member5.getId(), "2016-06-13 00:30:13");
    pushPayment(member6.getId(), "2016-11-06 07:13:00");

    // Add Claims
    pushClaim(meAydin.getId(), "2016-04-16", "change mirror", 120);
    pushClaim(meAydin.getId(), "2016-09-08", "repair scratch", 90);
    pushClaim(eSimons.getId(), "2016-10-10", "polishing tyres", 75);
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
