package net.novucs.esd.test.controller;

import static net.novucs.esd.test.util.TestUtil.createTestDaoManager;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.novucs.esd.controllers.ManagePaymentsServlet;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Payment;
import net.novucs.esd.model.User;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.DaoManager;
import net.novucs.esd.test.util.TestDummyDataUtil;
import net.novucs.esd.util.ReflectUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;

/**
 * The type Test member manage claims servlet.
 */
public class TestManagePaymentsServlet {

  private static final String LAYOUT = "/layout.jsp";
  private static final String SESSION = "session";
  private static final String VERIFIED = "VERIFIED";
  private static final String PENDING = "PENDING";
  private transient Session userSession;

  @SuppressWarnings("PMD.SingularField")
  private Dao<Payment> paymentDao;

  /**
   * Initialise test.
   */
  @Before
  public void initialiseTest() {
    userSession = new Session();
  }

  private void setServletDaos(ManagePaymentsServlet servlet,
      User user)
      throws SQLException, ReflectiveOperationException {
    DaoManager dm = createTestDaoManager(true);
    dm = createTestDaoManager(true);
    Dao<User> userDao = dm.get(User.class);
    paymentDao = dm.get(Payment.class);
    userDao.insert(user);

    paymentDao.insert(new Payment(
        user.getId(),
        BigDecimal.TEN,
        "mockStripeId",
        "Card Payment",
        ZonedDateTime.now(),
        VERIFIED
    ));
    paymentDao.insert(new Payment(
        user.getId(),
        BigDecimal.TEN,
        null,
        "Bank Reference",
        ZonedDateTime.now(),
        PENDING
    ));
    ReflectUtil.setFieldValue(servlet, "paymentDao", paymentDao);
  }

  /**
   * Test request returns correct page.
   *
   * @throws ServletException             the servlet exception
   * @throws IOException                  the io exception
   * @throws ReflectiveOperationException the reflective operation exception
   * @throws SQLException                 the sql exception
   */
  @Test
  public void testRequestReturnsCorrectPage()
      throws ServletException, IOException, ReflectiveOperationException, SQLException {

    ManagePaymentsServlet servlet = new ManagePaymentsServlet();
    HttpSession httpSession = mock(HttpSession.class);
    HttpServletRequest request = mock(HttpServletRequest.class);
    User user = TestDummyDataUtil.getDummyUser();

    setServletDaos(servlet, user);

    userSession.setUser(user);

    // When
    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);

    HttpServletResponse response = mock(HttpServletResponse.class);
    servlet.doGet(request, response);

    // Assert
    verify(request).setAttribute("page", String.format("%s.jsp", "user.payments.manage"));

  }

  // TODO: Fix this test.

  //  /**
  //   * Test request returns correct number of claims.
  //   *
  //   * @throws ServletException             the servlet exception
  //   * @throws IOException                  the io exception
  //   * @throws ReflectiveOperationException the reflective operation exception
  //   * @throws SQLException                 the sql exception
  //   */
  //  @Test
  //  public void testRequestReturnsCorrectNumberOfClaims()
  //      throws ServletException, IOException, ReflectiveOperationException, SQLException {
  //
  //    ManagePaymentsServlet servlet = new ManagePaymentsServlet();
  //    HttpSession httpSession = mock(HttpSession.class);
  //    HttpServletRequest request = mock(HttpServletRequest.class);
  //    User user = TestDummyDataUtil.getDummyUser();
  //
  //    setServletDaos(servlet, user);
  //
  //    userSession.setUser(user);
  //
  //    // When
  //    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);
  //    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
  //        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
  //    when(request.getSession(anyBoolean())).thenReturn(httpSession);
  //
  //    HttpServletResponse response = mock(HttpServletResponse.class);
  //    servlet.doGet(request, response);
  //
  //    List<Payment> payments = paymentDao.select().all();
  //    // Assert
  //    verify(request).setAttribute("payments", payments);
  //  }

  // TODO: Fix this test.

  //  /**
  //   * Test request returns correct pagination.
  //   *
  //   * @throws ServletException             the servlet exception
  //   * @throws IOException                  the io exception
  //   * @throws ReflectiveOperationException the reflective operation exception
  //   * @throws SQLException                 the sql exception
  //   */
  //  @Test
  //  public void testRequestReturnsCorrectPagination()
  //      throws ServletException, IOException, ReflectiveOperationException, SQLException {
  //
  //    ManagePaymentsServlet servlet = new ManagePaymentsServlet();
  //    HttpSession httpSession = mock(HttpSession.class);
  //    HttpServletRequest request = mock(HttpServletRequest.class);
  //    User user = TestDummyDataUtil.getDummyUser();
  //
  //    setServletDaos(servlet, user);
  //
  //    userSession.setUser(user);
  //
  //    // When
  //    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);
  //    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
  //        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
  //    when(request.getSession(anyBoolean())).thenReturn(httpSession);
  //
  //    HttpServletResponse response = mock(HttpServletResponse.class);
  //
  //    servlet.doGet(request, response);
  //    for (int i = 0; i < 20; i++) {
  //      paymentDao.insert(new Payment(
  //          user.getId(),
  //          BigDecimal.TEN,
  //          "mockStripeId",
  //          "Card Payment",
  //          ZonedDateTime.now(),
  //          VERIFIED
  //      ));
  //    }
  //    ReflectUtil.setFieldValue(servlet, "paymentDao", paymentDao);
  //    servlet.doGet(request, response);
  //
  //    // Assert
  //    verify(request).setAttribute("payments", paymentDao.select().all().subList(0, 15));
  //  }
}