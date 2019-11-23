package net.novucs.esd.test.controller;

import static net.novucs.esd.test.util.TestUtils.createTestDaoManager;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.novucs.esd.constants.ClaimStatusUtils;
import net.novucs.esd.controllers.member.MemberMakeClaimServlet;
import net.novucs.esd.lifecycle.DatabaseLifecycle;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Claim;
import net.novucs.esd.model.Membership;
import net.novucs.esd.model.User;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.DaoManager;
import net.novucs.esd.util.Password;
import net.novucs.esd.util.ReflectUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;

/**
 * The type Test member make claim servlet.
 */
public class TestMemberMakeClaimServlet {

  private static final String LAYOUT = "/layout.jsp";
  private static final String ACTIVE_MEMBERSHIP = "ACTIVE";
  private static final String USER_NAME = "User Name";
  private static final String USER_EMAIL = "user@email.com";
  private static final String USER_PASSWORD = "password1";
  private static final String USER_ADDRESS = "1 Esd Lane, UWE, A12 BC3";

  private static final String SESSION = "session";

  private static final String MEMBERSHIP_STATUS_ATTR = "membershipStatus";
  private static final String MEMBER_STATUS_NONE = "NONE";
  private static final String MEMBER_STATUS_FULL_CLAIM = "FULL_CLAIM";
  private static final String MEMBER_STATUS_FULL_WAIT = "FULL_WAIT";
  private static final String MEMBER_STATUS_FULL_USED = "FULL_USED";
  private static final String MEMBER_STATUS_SUSPENDED = "SUSPENDED";
  private static final String MEMBER_STATUS_EXPIRED = "EXPIRED";
  private transient Session userSession;

  /**
   * Initialise test.
   */
  @Before
  public void initialiseTest() {
    userSession = new Session();
  }

  private void setServletDaos(MemberMakeClaimServlet servlet,
      User user,
      boolean withOldMembership,
      boolean withCurrentMembership,
      boolean membershipSuspended,
      int claimCount,
      BigDecimal firstClaimAmount)
      throws SQLException, ReflectiveOperationException {

    DaoManager dm = createTestDaoManager();
    dm.init(DatabaseLifecycle.MODEL_CLASSES);
    Dao<User> userDao = dm.get(User.class);
    userDao.insert(user);
    Dao<Membership> membershipDao = dm.get(Membership.class);
    Dao<Claim> claimDao = dm.get(Claim.class);

    //Integer userId, BigDecimal balance, String status, ZonedDateTime startDate
    if (withOldMembership) {
      Membership oldMembership = new Membership(
          user.getId(),
          BigDecimal.valueOf(0),
          "EXPIRED",
          ZonedDateTime.now().minusMonths(15),
          false
      );
      membershipDao.insert(oldMembership);
    }
    if (withCurrentMembership) {
      Membership newMembership = new Membership(
          user.getId(),
          BigDecimal.valueOf(0),
          membershipSuspended ? "SUSPENDED" : ACTIVE_MEMBERSHIP,
          ZonedDateTime.now().minusMonths(3),
          !withOldMembership
      );
      membershipDao.insert(newMembership);
      for (int i = 0; i < claimCount; i++) {
        Claim claim = new Claim(newMembership.getId(),
            i == 0 ? firstClaimAmount : new BigDecimal(20), ZonedDateTime.now(),
            ClaimStatusUtils.PENDING);
        claimDao.insert(claim);
      }
    }

    ReflectUtil.setFieldValue(servlet, "userDao", userDao);
    ReflectUtil.setFieldValue(servlet, "membershipDao", membershipDao);
    ReflectUtil.setFieldValue(servlet, "claimDao", claimDao);
  }

  /**
   * Test request gets make claim page never a member.
   *
   * @throws ServletException             the servlet exception
   * @throws IOException                  the io exception
   * @throws ReflectiveOperationException the reflective operation exception
   * @throws SQLException                 the sql exception
   */
  @Test
  public void testRequestGetsMakeClaimPageNeverAMember()
      throws ServletException, IOException, ReflectiveOperationException, SQLException {

    MemberMakeClaimServlet servlet = new MemberMakeClaimServlet();
    HttpSession httpSession = mock(HttpSession.class);
    HttpServletRequest request = mock(HttpServletRequest.class);

    User user = new User(
        USER_NAME,
        USER_EMAIL,
        Password.fromPlaintext(USER_PASSWORD),
        USER_ADDRESS,
        ZonedDateTime.now().minusYears(20),
        "APPLICATION",
        0
    );

    setServletDaos(servlet,
        user,
        false,
        false,
        false,
        0,
        BigDecimal.ZERO);

    userSession.setUser(user);

    // When
    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);

    HttpServletResponse response = mock(HttpServletResponse.class);
    servlet.doGet(request, response);

    // Assert
    verify(request).setAttribute(MEMBERSHIP_STATUS_ATTR, MEMBER_STATUS_NONE);
  }

  /**
   * Test request gets make claim page no current membership.
   *
   * @throws ServletException the servlet exception
   * @throws IOException      the io exception
   */
  @Test
  public void testRequestGetsMakeClaimPageNoCurrentMembership()
      throws ServletException, IOException {

    MemberMakeClaimServlet servlet = new MemberMakeClaimServlet();
    HttpSession httpSession = mock(HttpSession.class);
    HttpServletRequest request = mock(HttpServletRequest.class);

    User user = new User(
        USER_NAME,
        USER_EMAIL,
        Password.fromPlaintext(USER_PASSWORD),
        USER_ADDRESS,
        ZonedDateTime.now().minusYears(20),
        "APPLICATION",
        0
    );

    try {
      setServletDaos(servlet,
          user,
          true,
          false,
          false,
          0,
          BigDecimal.ZERO);
    } catch (SQLException | ReflectiveOperationException e) {
      Logger.getLogger(MemberMakeClaimServlet.class.getName()).log(Level.SEVERE, null, e);

    }
    userSession.setUser(user);

    // When
    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);

    HttpServletResponse response = mock(HttpServletResponse.class);
    servlet.doGet(request, response);

    // Assert
    verify(request).setAttribute(MEMBERSHIP_STATUS_ATTR, MEMBER_STATUS_EXPIRED);
  }

  /**
   * Test request gets make claim page membership six month wait.
   *
   * @throws ServletException the servlet exception
   * @throws IOException      the io exception
   */
  @Test
  public void testRequestGetsMakeClaimPageMembershipSixMonthWait()
      throws ServletException, IOException {

    MemberMakeClaimServlet servlet = new MemberMakeClaimServlet();
    HttpSession httpSession = mock(HttpSession.class);
    HttpServletRequest request = mock(HttpServletRequest.class);

    User user = new User(
        USER_NAME,
        USER_EMAIL,
        Password.fromPlaintext(USER_PASSWORD),
        USER_ADDRESS,
        ZonedDateTime.now().minusYears(20),
        "APPLICATION",
        0
    );

    try {
      setServletDaos(servlet,
          user,
          false,
          true,
          false,
          0,
          BigDecimal.ZERO);
    } catch (SQLException | ReflectiveOperationException e) {
      Logger.getLogger(MemberMakeClaimServlet.class.getName()).log(Level.SEVERE, null, e);
    }
    userSession.setUser(user);

    // When
    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);

    HttpServletResponse response = mock(HttpServletResponse.class);
    servlet.doGet(request, response);

    // Assert
    verify(request).setAttribute(MEMBERSHIP_STATUS_ATTR, MEMBER_STATUS_FULL_WAIT);
  }

  /**
   * Test request gets make claim page membership used claim quota.
   *
   * @throws ServletException the servlet exception
   * @throws IOException      the io exception
   */
  @Test
  public void testRequestGetsMakeClaimPageMembershipUsedClaimQuota()
      throws ServletException, IOException {

    MemberMakeClaimServlet servlet = new MemberMakeClaimServlet();
    HttpSession httpSession = mock(HttpSession.class);
    HttpServletRequest request = mock(HttpServletRequest.class);

    User user = new User(
        USER_NAME,
        USER_EMAIL,
        Password.fromPlaintext(USER_PASSWORD),
        USER_ADDRESS,
        ZonedDateTime.now().minusYears(20),
        ACTIVE_MEMBERSHIP,
        0
    );

    try {
      setServletDaos(servlet,
          user,
          true,
          true,
          false,
          2,
          new BigDecimal(25));
    } catch (SQLException | ReflectiveOperationException e) {
      Logger.getLogger(MemberMakeClaimServlet.class.getName()).log(Level.SEVERE, null, e);

    }
    userSession.setUser(user);

    // When
    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession()).thenReturn(httpSession);
    when(request.getSession(anyBoolean())).thenReturn(httpSession);

    HttpServletResponse response = mock(HttpServletResponse.class);
    servlet.doGet(request, response);

    // Assert
    verify(request).setAttribute(MEMBERSHIP_STATUS_ATTR, MEMBER_STATUS_FULL_USED);

  }

  /**
   * Test request gets make claim page membership suspended.
   *
   * @throws ServletException the servlet exception
   * @throws IOException      the io exception
   */
  @Test
  public void testRequestGetsMakeClaimPageMembershipSuspended()
      throws ServletException, IOException {

    MemberMakeClaimServlet servlet = new MemberMakeClaimServlet();
    HttpSession httpSession = mock(HttpSession.class);
    HttpServletRequest request = mock(HttpServletRequest.class);

    User user = new User(
        USER_NAME,
        USER_EMAIL,
        Password.fromPlaintext(USER_PASSWORD),
        USER_ADDRESS,
        ZonedDateTime.now().minusYears(20),
        ACTIVE_MEMBERSHIP,
        0
    );

    try {
      setServletDaos(servlet,
          user,
          true,
          true,
          true,
          0,
          new BigDecimal(25));
    } catch (SQLException | ReflectiveOperationException e) {
      Logger.getLogger(MemberMakeClaimServlet.class.getName()).log(Level.SEVERE, null, e);

    }
    userSession.setUser(user);

    // When
    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession()).thenReturn(httpSession);
    when(request.getSession(anyBoolean())).thenReturn(httpSession);

    HttpServletResponse response = mock(HttpServletResponse.class);
    servlet.doGet(request, response);

    // Assert
    verify(request).setAttribute(MEMBERSHIP_STATUS_ATTR, MEMBER_STATUS_SUSPENDED);
  }

  /**
   * Test request gets make claim page membership eligible for claim full amount.
   *
   * @throws ServletException the servlet exception
   * @throws IOException      the io exception
   */
  @Test
  public void testRequestGetsMakeClaimPageMembershipEligibleForClaimFullAmount()
      throws ServletException, IOException {

    MemberMakeClaimServlet servlet = new MemberMakeClaimServlet();
    HttpSession httpSession = mock(HttpSession.class);
    HttpServletRequest request = mock(HttpServletRequest.class);

    User user = new User(
        USER_NAME,
        USER_EMAIL,
        Password.fromPlaintext(USER_PASSWORD),
        USER_ADDRESS,
        ZonedDateTime.now().minusYears(20),
        ACTIVE_MEMBERSHIP,
        0
    );

    try {
      setServletDaos(servlet,
          user,
          true,
          true,
          false,
          0,
          new BigDecimal(25));
    } catch (SQLException | ReflectiveOperationException e) {
      Logger.getLogger(MemberMakeClaimServlet.class.getName()).log(Level.SEVERE, null, e);

    }
    userSession.setUser(user);

    // When
    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession()).thenReturn(httpSession);
    when(request.getSession(anyBoolean())).thenReturn(httpSession);

    HttpServletResponse response = mock(HttpServletResponse.class);
    servlet.doGet(request, response);

    // Assert
    verify(request).setAttribute(MEMBERSHIP_STATUS_ATTR, MEMBER_STATUS_FULL_CLAIM);
  }

  /**
   * Test request gets make claim page membership eligible for claim limited amount.
   *
   * @throws ServletException the servlet exception
   * @throws IOException      the io exception
   */
  @Test
  public void testRequestGetsMakeClaimPageMembershipEligibleForClaimLimitedAmount()
      throws ServletException, IOException {
    MemberMakeClaimServlet servlet = new MemberMakeClaimServlet();
    HttpSession httpSession = mock(HttpSession.class);
    HttpServletRequest request = mock(HttpServletRequest.class);

    User user = new User(
        USER_NAME,
        USER_EMAIL,
        Password.fromPlaintext(USER_PASSWORD),
        USER_ADDRESS,
        ZonedDateTime.now().minusYears(20),
        ACTIVE_MEMBERSHIP,
        0
    );

    try {
      setServletDaos(servlet,
          user,
          true,
          true,
          false,
          1,
          new BigDecimal(50));
    } catch (SQLException | ReflectiveOperationException e) {
      Logger.getLogger(MemberMakeClaimServlet.class.getName()).log(Level.SEVERE, null, e);

    }
    userSession.setUser(user);

    // When
    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession()).thenReturn(httpSession);
    when(request.getSession(anyBoolean())).thenReturn(httpSession);

    HttpServletResponse response = mock(HttpServletResponse.class);
    servlet.doGet(request, response);

    DecimalFormat df = new DecimalFormat("#.##");
    // Assert
    verify(request).setAttribute(MEMBERSHIP_STATUS_ATTR, MEMBER_STATUS_FULL_CLAIM);
    verify(request).setAttribute("maxClaimValue", df.format(50));
  }
}