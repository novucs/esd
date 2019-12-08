package net.novucs.esd.test.controller;

import static net.novucs.esd.test.util.TestUtil.createTestDaoManager;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.novucs.esd.controllers.MakePaymentServlet;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Application;
import net.novucs.esd.model.ApplicationStatus;
import net.novucs.esd.model.Membership;
import net.novucs.esd.model.User;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.DaoManager;
import net.novucs.esd.test.util.TestDummyDataUtil;
import net.novucs.esd.util.ReflectUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;

/**
 * The type Test make payment servlet.
 */
public class TestMakePaymentServlet {

  private static final String LAYOUT = "/layout.jsp";
  private static final String SESSION = "session";
  private static final String FEE = "fee";
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
      boolean withCurrentMembership)
      throws SQLException, ReflectiveOperationException {

    DaoManager dm = createTestDaoManager(true);
    Dao<User> userDao = dm.get(User.class);
    userDao.insert(user);
    Dao<Membership> membershipDao = dm.get(Membership.class);
    Dao<Application> applicationDao = dm.get(Application.class);
    Application application = new Application(
        user.getId()
    );
    //Integer userId, BigDecimal balance, String status, ZonedDateTime startDate
    if (withOldMembership) {
      Membership oldMembership = new Membership(
          user.getId(),
          ZonedDateTime.now().minusMonths(15),
          false
      );
      membershipDao.insert(oldMembership);
    }
    if (withCurrentMembership) {
      application.setStatus(ApplicationStatus.APPROVED);
      Membership newMembership = new Membership(
          user.getId(),
          ZonedDateTime.now().minusMonths(3),
          !withOldMembership
      );
      membershipDao.insert(newMembership);
    }
    applicationDao.insert(application);

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
    User user = TestDummyDataUtil.getDummyUser();

    setServletDaos(servlet,
        user,
        false,
        false);

    userSession.setUser(user);

    // When
    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);

    HttpServletResponse response = mock(HttpServletResponse.class);
    servlet.doGet(request, response);

    // Assert
    verify(request).setAttribute(FEE, Membership.ANNUAL_FEE_POUNDS);
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
    User user = TestDummyDataUtil.getDummyUser();

    setServletDaos(servlet,
        user,
        false,
        false);

    userSession.setUser(user);

    // When
    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);

    HttpServletResponse response = mock(HttpServletResponse.class);
    servlet.doGet(request, response);

    // Assert
    verify(request).setAttribute(FEE, Membership.ANNUAL_FEE_POUNDS);
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
    User user = TestDummyDataUtil.getDummyUser();

    setServletDaos(servlet, user, false, true);

    userSession.setUser(user);

    // When
    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);

    HttpServletResponse response = mock(HttpServletResponse.class);
    servlet.doGet(request, response);

    // Assert
    verify(request, never()).setAttribute(FEE, Membership.ANNUAL_FEE_POUNDS);
  }

  /**
   * Test request gets make payment membership.
   *
   * @throws ServletException             the servlet exception
   * @throws IOException                  the io exception
   * @throws SQLException                 the sql exception
   * @throws ReflectiveOperationException the reflective operation exception
   */
  @Test
  public void testRequestGetsMakePaymentMembership()
      throws ServletException, IOException, SQLException, ReflectiveOperationException {

    MakePaymentServlet servlet = new MakePaymentServlet();
    HttpSession httpSession = mock(HttpSession.class);
    HttpServletRequest request = mock(HttpServletRequest.class);
    User user = TestDummyDataUtil.getDummyUser();

    setServletDaos(servlet, user, true, true);

    userSession.setUser(user);

    // When
    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);

    HttpServletResponse response = mock(HttpServletResponse.class);
    servlet.doGet(request, response);

    // Assert
    verify(request, never()).setAttribute(FEE, Membership.ANNUAL_FEE_POUNDS);
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
    User user = TestDummyDataUtil.getDummyUser();

    setServletDaos(servlet, user, true, true);

    userSession.setUser(user);

    // When
    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);

    HttpServletResponse response = mock(HttpServletResponse.class);
    servlet.doGet(request, response);

    // Assert
    verify(request, never()).setAttribute(FEE, Membership.ANNUAL_FEE_POUNDS);
  }
}