package net.novucs.esd.test.controller;

import static junit.framework.TestCase.assertTrue;
import static net.novucs.esd.test.util.TestUtil.createTestDaoManager;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.novucs.esd.controllers.UserSettingsServlet;
import net.novucs.esd.lifecycle.DatabaseLifecycle;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.User;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.DaoManager;
import net.novucs.esd.test.util.TestDummyDataUtil;
import net.novucs.esd.util.ReflectUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;

/**
 * The type Test User Settings servlet.
 */

public class TestUserSettingsServlet {

  private static final String SESSION_LABEL = "session";

  private static final String USER_ID_LABEL = "userId";

  private static final String ERRORS_LABEL = "errors";

  private static final String LAYOUT_JSP_LABEL = "/layout.jsp";

  private transient Session userSession;

  private final UserSettingsServlet servlet = new UserSettingsServlet();

  private final HttpServletResponse response = mock(HttpServletResponse.class);

  private final HttpServletRequest request = mock(HttpServletRequest.class);

  private final HttpSession session = mock(HttpSession.class);


  @Before
  public void initialiseTest() {
    userSession = new Session();
    userSession.setUser(TestDummyDataUtil.getDummyAdminUser());
  }
  /**
   * sets the Database servlet.
   *
   * @throws ReflectiveOperationException the reflective operator exception
   * @throws SQLException the sql exception
   */

  private void setServletDaos(UserSettingsServlet servlet)
      throws ReflectiveOperationException, SQLException {

    // make sure that getting users is possible
    DaoManager dm = createTestDaoManager();
    dm.init(DatabaseLifecycle.MODEL_CLASSES);
    Dao<User> userDao = dm.get(User.class);

    // Insert our Dummy User with Roles
    userDao.insert(userSession.getUser());

    // Reflect DAO
    ReflectUtil.setFieldValue(servlet, "userDao", userDao);
  }

  /**
   * Test updating invalid users.
   *
   * @throws SQLException                 the sql exception
   * @throws ReflectiveOperationException the reflective operation exception
   * @throws ServletException             the servlet exception
   * @throws IOException                  the io exception
   */
  @Test
  public void testUpdateInvalidUser()
      throws IOException, ServletException, SQLException, ReflectiveOperationException {
    // Given
    setServletDaos(servlet);

    // Empty Session
    Session emptySession = new Session();
    emptySession.setUser(null);

    // When
    when(request.getSession(anyBoolean())).thenReturn(session);
    when(session.getAttribute(SESSION_LABEL)).thenReturn(emptySession);
    when(request.getRequestDispatcher(LAYOUT_JSP_LABEL)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    servlet.doPost(request, response);

    // Verify
    verify(request).setAttribute(eq("error"), anyString());
    verify(response).sendError(anyInt());
  }

  /**
   * Test Updating the user's details.
   *
   * @throws SQLException                 the sql exception
   * @throws ReflectiveOperationException the reflective operation exception
   */
  @Test
  public void testUpdateUserDetails()
      throws SQLException, ReflectiveOperationException, IOException, ServletException {
    // Given
    setServletDaos(servlet);

    // When
    esdWhen();
    userDetails();
    when(request.getParameter(eq("current_password"))).thenReturn("");
    when(request.getParameter(eq("new_password"))).thenReturn("");
    servlet.doPost(request, response);

    // Verify
    verify(request).setAttribute(eq("updated"), eq(true));
  }

  /**
   * Test updating the user's password.
   *
   * @throws SQLException                 the sql exception
   * @throws ReflectiveOperationException the reflective operation exception
   * @throws ServletException             the servlet exception
   * @throws IOException                  the io exception
   */
  @Test
  public void testUpdateUserPassword()
      throws SQLException, ReflectiveOperationException, IOException, ServletException {
    // Given
    setServletDaos(servlet);

    // When
    esdWhen();
    userDetails();
    when(request.getParameter(eq("current_password"))).thenReturn("bob");
    when(request.getParameter(eq("new_password"))).thenReturn("enterprise");
    servlet.doPost(request, response);

    // Verify
    verify(request).setAttribute(eq("updated"), eq(true));
  }

  /**
   * Test an incorrect password update.
   *
   * @throws SQLException                 the sql exception
   * @throws ReflectiveOperationException the reflective operation exception
   * @throws ServletException             the servlet exception
   * @throws IOException                  the io exception
   */
  @Test
  public void testUpdateUserIncorrectRepeatedPassword()
      throws SQLException, ReflectiveOperationException, IOException, ServletException {
    // Given
    setServletDaos(servlet);

    // When
    esdWhen();
    userDetails();
    when(request.getParameter(eq("current_password"))).thenReturn("enterprise");
    when(request.getParameter(eq("new_password"))).thenReturn("depression");
    servlet.doPost(request, response);

    // Verify
    verify(request).setAttribute(eq("updated"), eq(true));
  }

  /**
   * Test getting an invalid edited user.
   *
   * @throws SQLException                 the sql exception
   * @throws ReflectiveOperationException the reflective operation exception
   * @throws ServletException             the servlet exception
   * @throws IOException                  the io exception
   */
  @Test
  public void testGetInvalidEditUser()
      throws IOException, ServletException, SQLException, ReflectiveOperationException {
    // Given
    setServletDaos(servlet);

    // When
    esdWhen();
    when(request.getParameter(eq(USER_ID_LABEL))).thenReturn("696969");
    servlet.doGet(request, response);

    // Verify
    verify(request).setAttribute(eq(ERRORS_LABEL), anyList());
  }

  /**
   * Test getting an edited user.
   *
   * @throws SQLException                 the sql exception
   * @throws ReflectiveOperationException the reflective operation exception
   * @throws ServletException             the servlet exception
   * @throws IOException                  the io exception
   */
  @Test
  public void testGetEditUser()
      throws IOException, ServletException, SQLException, ReflectiveOperationException {
    // Given
    setServletDaos(servlet);

    // When
    esdWhen();
    servlet.doGet(request, response);

    // Verify
    verify(request).setAttribute(eq(ERRORS_LABEL), anyList());
  }

  /**
   * Test the servlet information.
   */
  @Test
  public void testServletInfo() {
    // Given
    UserSettingsServlet servlet = new UserSettingsServlet();

    // When
    String servletInfo = servlet.getServletInfo();

    // Assert
    assertTrue("getServletInfo must match the class name.",
        servletInfo.equalsIgnoreCase(servlet.getClass().getSimpleName()));
  }

  private void esdWhen() {
    when(request.getSession(anyBoolean())).thenReturn(session);
    when(session.getAttribute(eq(SESSION_LABEL))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT_JSP_LABEL)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
  }

  private void userDetails() {
    when(request.getParameter(eq("fullname"))).thenReturn(
        "Test User");
    when(request.getParameter(eq("email"))).thenReturn(
        "test@esd.net");
    when(request.getParameter(eq("address"))).thenReturn(
        "1 ESD Lane");
    when(request.getParameter(eq("date_of_birth"))).thenReturn(
        "1970-01-01");
  }
}
