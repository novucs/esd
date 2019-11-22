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
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.novucs.esd.controllers.MakePaymentServlet;
import net.novucs.esd.lifecycle.DatabaseLifecycle;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Application;
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
 * The type Test make payment servlet.
 */
public class TestMakePaymentServlet {

  private static final String LAYOUT = "/layout.jsp";
  private static final String ACTIVE_MEMBERSHIP = "ACTIVE";
  private static final String APPLICATION = "APPLICATION";
  private static final String USER_NAME = "User Name";
  private static final String USER_EMAIL = "user@email.com";
  private static final String USER_PASSWORD = "password1";
  private static final String USER_ADDRESS = "1 Esd Lane, UWE, A12 BC3";
  private static final String SESSION = "session";
  private static final int MEMBERSHIP_FEE = 10;
  private static final String AMOUNT_OWED = "amountOwed";
  private static final String DECIMAL_FORMAT = "#.##";
  private transient Session userSession;

  /**
   * Initialise test.
   */
  @Before
  public void initialiseTest() {
    userSession = new Session();
  }

  private void setServletDaos(MakePaymentServlet servlet,
      User user,
      boolean withOldMembership,
      boolean withCurrentMembership,
      BigDecimal applicationBalance,
      BigDecimal oldMembershipBalance,
      BigDecimal currentMembershipBalance)
      throws SQLException, ReflectiveOperationException {

    DaoManager dm = createTestDaoManager();
    dm.init(DatabaseLifecycle.MODEL_CLASSES);
    Dao<User> userDao = dm.get(User.class);
    userDao.insert(user);
    Dao<Membership> membershipDao = dm.get(Membership.class);
    Dao<Application> applicationDao = dm.get(Application.class);
    Application application = new Application(
        user.getId(),
        applicationBalance
    );
    //Integer userId, BigDecimal balance, String status, ZonedDateTime startDate
    if (withOldMembership) {
      Membership oldMembership = new Membership(
          user.getId(),
          oldMembershipBalance,
          "EXPIRED",
          ZonedDateTime.now().minusMonths(15),
          false
      );
      membershipDao.insert(oldMembership);
    }
    if (withCurrentMembership) {
      application.setStatus("CLOSED");
      Membership newMembership = new Membership(
          user.getId(),
          currentMembershipBalance,
          ACTIVE_MEMBERSHIP,
          ZonedDateTime.now().minusMonths(3),
          !withOldMembership
      );
      membershipDao.insert(newMembership);
    }
    applicationDao.insert(application);

    ReflectUtil.setFieldValue(servlet, "userDao", userDao);
    ReflectUtil.setFieldValue(servlet, "membershipDao", membershipDao);
    ReflectUtil.setFieldValue(servlet, "applicationDao", applicationDao);
  }

  /**
   * Test request gets make payment application to pay.
   *
   * @throws ServletException             the servlet exception
   * @throws IOException                  the io exception
   * @throws ReflectiveOperationException the reflective operation exception
   * @throws SQLException                 the sql exception
   */
  @Test
  public void testRequestGetsMakePaymentApplicationToPay()
      throws ServletException, IOException, ReflectiveOperationException, SQLException {

    MakePaymentServlet servlet = new MakePaymentServlet();
    HttpSession httpSession = mock(HttpSession.class);
    HttpServletRequest request = mock(HttpServletRequest.class);

    User user = new User(
        USER_NAME,
        USER_EMAIL,
        Password.fromPlaintext(USER_PASSWORD),
        USER_ADDRESS,
        ZonedDateTime.now().minusYears(20),
        APPLICATION,
        0
    );

    setServletDaos(servlet,
        user,
        false,
        false,
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        BigDecimal.ZERO);

    userSession.setUser(user);

    // When
    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);

    HttpServletResponse response = mock(HttpServletResponse.class);
    servlet.doGet(request, response);

    DecimalFormat df = new DecimalFormat(DECIMAL_FORMAT);
    // Assert
    verify(request).setAttribute(AMOUNT_OWED, df.format(BigDecimal.valueOf(MEMBERSHIP_FEE)));
  }

  /**
   * Test request gets make payment application settled.
   *
   * @throws ServletException             the servlet exception
   * @throws IOException                  the io exception
   * @throws SQLException                 the sql exception
   * @throws ReflectiveOperationException the reflective operation exception
   */
  @Test
  public void testRequestGetsMakePaymentApplicationSettled()
      throws ServletException, IOException, SQLException, ReflectiveOperationException {

    MakePaymentServlet servlet = new MakePaymentServlet();
    HttpSession httpSession = mock(HttpSession.class);
    HttpServletRequest request = mock(HttpServletRequest.class);

    User user = new User(
        USER_NAME,
        USER_EMAIL,
        Password.fromPlaintext(USER_PASSWORD),
        USER_ADDRESS,
        ZonedDateTime.now().minusYears(20),
        APPLICATION,
        0
    );

    setServletDaos(servlet,
        user,
        false,
        false,
        BigDecimal.valueOf(MEMBERSHIP_FEE),
        BigDecimal.ZERO,
        BigDecimal.ZERO);

    userSession.setUser(user);

    // When
    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);

    HttpServletResponse response = mock(HttpServletResponse.class);
    servlet.doGet(request, response);

    DecimalFormat df = new DecimalFormat(DECIMAL_FORMAT);
    // Assert
    verify(request).setAttribute(AMOUNT_OWED, df.format(BigDecimal.ZERO));
  }

  /**
   * Test request gets make payment membership to pay.
   *
   * @throws ServletException             the servlet exception
   * @throws IOException                  the io exception
   * @throws SQLException                 the sql exception
   * @throws ReflectiveOperationException the reflective operation exception
   */
  @Test
  public void testRequestGetsMakePaymentMembershipToPay()
      throws ServletException, IOException, SQLException, ReflectiveOperationException {

    MakePaymentServlet servlet = new MakePaymentServlet();
    HttpSession httpSession = mock(HttpSession.class);
    HttpServletRequest request = mock(HttpServletRequest.class);

    User user = new User(
        USER_NAME,
        USER_EMAIL,
        Password.fromPlaintext(USER_PASSWORD),
        USER_ADDRESS,
        ZonedDateTime.now().minusYears(20),
        APPLICATION,
        0
    );

    setServletDaos(servlet,
        user,
        false,
        true,
        BigDecimal.valueOf(MEMBERSHIP_FEE),
        BigDecimal.ZERO,
        BigDecimal.ZERO);

    userSession.setUser(user);

    // When
    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);

    HttpServletResponse response = mock(HttpServletResponse.class);
    servlet.doGet(request, response);

    DecimalFormat df = new DecimalFormat(DECIMAL_FORMAT);
    // Assert
    verify(request).setAttribute(AMOUNT_OWED, df.format(BigDecimal.valueOf(MEMBERSHIP_FEE)));
  }

  /**
   * Test request gets make payment membership to pay multiple.
   *
   * @throws ServletException             the servlet exception
   * @throws IOException                  the io exception
   * @throws SQLException                 the sql exception
   * @throws ReflectiveOperationException the reflective operation exception
   */
  @Test
  public void testRequestGetsMakePaymentMembershipToPayMultiple()
      throws ServletException, IOException, SQLException, ReflectiveOperationException {

    MakePaymentServlet servlet = new MakePaymentServlet();
    HttpSession httpSession = mock(HttpSession.class);
    HttpServletRequest request = mock(HttpServletRequest.class);

    User user = new User(
        USER_NAME,
        USER_EMAIL,
        Password.fromPlaintext(USER_PASSWORD),
        USER_ADDRESS,
        ZonedDateTime.now().minusYears(20),
        APPLICATION,
        0
    );

    setServletDaos(servlet,
        user,
        true,
        true,
        BigDecimal.valueOf(MEMBERSHIP_FEE),
        BigDecimal.ZERO,
        BigDecimal.ZERO);

    userSession.setUser(user);

    // When
    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);

    HttpServletResponse response = mock(HttpServletResponse.class);
    servlet.doGet(request, response);

    DecimalFormat df = new DecimalFormat(DECIMAL_FORMAT);
    // Assert
    verify(request).setAttribute(AMOUNT_OWED,
        df.format(BigDecimal.valueOf(MEMBERSHIP_FEE).multiply(BigDecimal.valueOf(2))));

  }

  /**
   * Test request gets make payment membership to pay single.
   *
   * @throws ServletException             the servlet exception
   * @throws IOException                  the io exception
   * @throws SQLException                 the sql exception
   * @throws ReflectiveOperationException the reflective operation exception
   */
  @Test
  public void testRequestGetsMakePaymentMembershipToPaySingle()
      throws ServletException, IOException, SQLException, ReflectiveOperationException {

    MakePaymentServlet servlet = new MakePaymentServlet();
    HttpSession httpSession = mock(HttpSession.class);
    HttpServletRequest request = mock(HttpServletRequest.class);

    User user = new User(
        USER_NAME,
        USER_EMAIL,
        Password.fromPlaintext(USER_PASSWORD),
        USER_ADDRESS,
        ZonedDateTime.now().minusYears(20),
        APPLICATION,
        0
    );

    setServletDaos(servlet,
        user,
        true,
        true,
        BigDecimal.valueOf(MEMBERSHIP_FEE),
        BigDecimal.valueOf(MEMBERSHIP_FEE),
        BigDecimal.ZERO);

    userSession.setUser(user);

    // When
    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);

    HttpServletResponse response = mock(HttpServletResponse.class);
    servlet.doGet(request, response);

    DecimalFormat df = new DecimalFormat(DECIMAL_FORMAT);
    // Assert
    verify(request).setAttribute(AMOUNT_OWED, df.format(BigDecimal.valueOf(MEMBERSHIP_FEE)));
  }

  /**
   * Test request gets make payment membership to pay settled.
   *
   * @throws ServletException             the servlet exception
   * @throws IOException                  the io exception
   * @throws SQLException                 the sql exception
   * @throws ReflectiveOperationException the reflective operation exception
   */
  @Test
  public void testRequestGetsMakePaymentMembershipToPaySettled()
      throws ServletException, IOException, SQLException, ReflectiveOperationException {

    MakePaymentServlet servlet = new MakePaymentServlet();
    HttpSession httpSession = mock(HttpSession.class);
    HttpServletRequest request = mock(HttpServletRequest.class);

    User user = new User(
        USER_NAME,
        USER_EMAIL,
        Password.fromPlaintext(USER_PASSWORD),
        USER_ADDRESS,
        ZonedDateTime.now().minusYears(20),
        APPLICATION,
        0
    );

    setServletDaos(servlet,
        user,
        true,
        true,
        BigDecimal.valueOf(MEMBERSHIP_FEE),
        BigDecimal.valueOf(MEMBERSHIP_FEE),
        BigDecimal.valueOf(MEMBERSHIP_FEE));

    userSession.setUser(user);

    // When
    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);

    HttpServletResponse response = mock(HttpServletResponse.class);
    servlet.doGet(request, response);

    DecimalFormat df = new DecimalFormat(DECIMAL_FORMAT);
    // Assert
    verify(request).setAttribute(AMOUNT_OWED, df.format(BigDecimal.ZERO));
  }
}