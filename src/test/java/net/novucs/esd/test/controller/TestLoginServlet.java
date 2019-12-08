package net.novucs.esd.test.controller;

import static net.novucs.esd.test.util.TestUtil.createTestDaoManager;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
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
import net.novucs.esd.controllers.LoginServlet;
import net.novucs.esd.lifecycle.DatabaseLifecycle;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Role;
import net.novucs.esd.model.User;
import net.novucs.esd.model.UserRole;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.DaoManager;
import net.novucs.esd.orm.Where;
import net.novucs.esd.util.Password;
import net.novucs.esd.util.ReflectUtil;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;

/**
 * The type Test login servlet.
 */
public class TestLoginServlet {

  private static final String LAYOUT_PAGE = "/layout.jsp";

  private static final String SESSION = "session";

  /**
   * Test request gets login page.
   *
   * @throws ServletException the servlet exception
   * @throws IOException      the io exception
   */
  @Test
  public void testRequestGetsLoginPage() throws ServletException, IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getRequestDispatcher(LAYOUT_PAGE)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(mock(HttpSession.class));

    LoginServlet servlet = new LoginServlet();
    HttpServletResponse response = mock(HttpServletResponse.class);
    servlet.doGet(request, response);
    verify(request).setAttribute("page", "/login.jsp");
    verify(request).getRequestDispatcher(LAYOUT_PAGE);
  }

  /**
   * Test login success.
   *
   * @throws SQLException                 the sql exception
   * @throws ReflectiveOperationException the reflective operation exception
   * @throws ServletException             the servlet exception
   * @throws IOException                  the io exception
   */
  @Test
  public void testLoginSuccess()
      throws SQLException, ReflectiveOperationException, ServletException, IOException {
    DaoManager dm = createTestDaoManager(true);
    Dao<User> userDao = dm.get(User.class);

    LoginServlet loginServlet = new LoginServlet();
    ReflectUtil.setFieldValue(loginServlet, "userDao", userDao);
    ReflectUtil.setFieldValue(loginServlet, "userRoleDao", dm.get(UserRole.class));
    ReflectUtil.setFieldValue(loginServlet, "roleDao", dm.get(Role.class));
    //ReflectUtil.setFieldValue(loginServlet, "membershipDao", dm.get(Role.class));
    HttpServletRequest request = mock(HttpServletRequest.class);

    User userToLogin = userDao.select().where(new Where().eq("name", "Member")).first();
    when(request.getParameter("username")).thenReturn(userToLogin.getEmail());
    when(request.getParameter("password")).thenReturn("password1");
    HttpSession session = mock(HttpSession.class);
    when(request.getSession(anyBoolean())).thenReturn(session);
    when(request.getRequestDispatcher(any())).thenReturn(mock(RequestDispatcher.class));
    when(session.getAttribute(eq(SESSION))).thenReturn(null);

    HttpServletResponse response = mock(HttpServletResponse.class);
    loginServlet.doPost(request, response);

    ArgumentCaptor<Session> argument = ArgumentCaptor.forClass(Session.class);
    verify(session, times(1)).setAttribute(eq(SESSION), argument.capture());

    assertEquals("Has user been correctly stored in session.",
        userToLogin.getId(), argument.getAllValues().get(0).getUser().getId());
  }

  /**
   * Test password incorrect.
   *
   * @throws SQLException                 the sql exception
   * @throws ReflectiveOperationException the reflective operation exception
   * @throws ServletException             the servlet exception
   * @throws IOException                  the io exception
   */
  @Test
  public void testPasswordIncorrect()
      throws SQLException, ReflectiveOperationException, ServletException, IOException {
    DaoManager dm = createTestDaoManager();
    dm.init(DatabaseLifecycle.MODEL_CLASSES);
    Dao<User> userDao = dm.get(User.class);

    User userToCreate = new User(
        "LoginTestUser2",
        "test-user2",
        "user@test.com",
        Password.fromPlaintext("pass"),
        "House,A Street,A city,County,AB12 C34",
        ZonedDateTime.now(),
        1
    );
    userDao.insert(userToCreate);

    LoginServlet loginServlet = new LoginServlet();
    ReflectUtil.setFieldValue(loginServlet, "userDao", userDao);
    HttpServletRequest request = mock(HttpServletRequest.class);

    User userToLogin = userDao.select().where(new Where().eq("name", "LoginTestUser2")).first();
    String incorrectPassword = "incorrectPassword";

    when(request.getParameter("username")).thenReturn(userToLogin.getEmail());
    when(request.getParameter("password")).thenReturn(incorrectPassword);

    HttpSession session = mock(HttpSession.class);
    Session esdSession = mock(Session.class);

    when(request.getSession(anyBoolean())).thenReturn(session);
    when(request.getRequestDispatcher(any())).thenReturn(mock(RequestDispatcher.class));
    when(session.getAttribute(SESSION)).thenReturn(esdSession);

    HttpServletResponse response = mock(HttpServletResponse.class);
    loginServlet.doPost(request, response);

    verify(request).getRequestDispatcher(LAYOUT_PAGE);
    verify(esdSession).pushError("Incorrect username or password");
  }

  /**
   * Test username incorrect.
   *
   * @throws SQLException                 the sql exception
   * @throws ReflectiveOperationException the reflective operation exception
   * @throws ServletException             the servlet exception
   * @throws IOException                  the io exception
   */
  @Test
  public void testUsernameIncorrect()
      throws SQLException, ReflectiveOperationException, ServletException, IOException {
    DaoManager dm = createTestDaoManager();
    dm.init(DatabaseLifecycle.MODEL_CLASSES);
    Dao<User> userDao = dm.get(User.class);

    User userToCreate = new User(
        "LoginTestUser3",
        "test-user3",
        "user@test.com",
        Password.fromPlaintext("correctPassword"),
        "House,A Street,A city,County,AB12 C34",
        ZonedDateTime.now(),
        1
    );
    userDao.insert(userToCreate);

    LoginServlet loginServlet = new LoginServlet();
    ReflectUtil.setFieldValue(loginServlet, "userDao", userDao);
    HttpServletRequest request = mock(HttpServletRequest.class);

    String password = "correctPassword";

    when(request.getParameter("username")).thenReturn("InvalidEmail@thisshouldnotwork.com");
    when(request.getParameter("password")).thenReturn(password);

    HttpSession session = mock(HttpSession.class);
    Session esdSession = mock(Session.class);

    when(request.getSession(anyBoolean())).thenReturn(session);
    when(request.getRequestDispatcher(any())).thenReturn(mock(RequestDispatcher.class));
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(session.getAttribute(eq(SESSION))).thenReturn(esdSession);

    loginServlet.doPost(request, response);
    verify(request).getRequestDispatcher(LAYOUT_PAGE);
    verify(esdSession).pushError("Incorrect username or password");
  }
}