package net.novucs.esd.test.controller;

import static net.novucs.esd.test.util.TestUtils.createTestDaoManager;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Stack;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.novucs.esd.controllers.LoginServlet;
import net.novucs.esd.lifecycle.DatabaseLifecycle;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.User;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.DaoManager;
import net.novucs.esd.orm.Where;
import net.novucs.esd.util.Password;
import net.novucs.esd.util.ReflectUtil;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;

public class TestLoginServlet {

  private static final String LAYOUT_PAGE = "/layout.jsp";

  @Test
  public void testRequestGetsLoginPage() throws ServletException, IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getRequestDispatcher(LAYOUT_PAGE)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession()).thenReturn(mock(HttpSession.class));

    LoginServlet servlet = new LoginServlet();
    HttpServletResponse response = mock(HttpServletResponse.class);
    servlet.doGet(request, response);
    verify(request).setAttribute("page", "/login.jsp");
    verify(request).getRequestDispatcher(LAYOUT_PAGE);
  }

  @Test
  public void testLoginSuccess()
      throws SQLException, ReflectiveOperationException, ServletException, IOException {
    DaoManager dm = createTestDaoManager();
    dm.init(DatabaseLifecycle.MODEL_CLASSES);
    Dao<User> userDao = dm.get(User.class);

    User userToCreate = new User(
        "LoginTestUser1",
        "user@test.com",
        Password.fromPlaintext("testPassword"),
        "House,A Street,A city,County,AB12 C34",
        "APPLICATION"
    );

    userDao.insert(userToCreate);

    LoginServlet loginServlet = new LoginServlet();
    ReflectUtil.setFieldValue(loginServlet, "userDao", userDao);
    HttpServletRequest request = mock(HttpServletRequest.class);

    User userToLogin = userDao.select().where(new Where().eq("name", "LoginTestUser1")).first();
    String password = "testPassword";

    when(request.getParameter("username")).thenReturn(userToLogin.getEmail());
    when(request.getParameter("password")).thenReturn(password);
    HttpSession session = mock(HttpSession.class);
    when(request.getSession()).thenReturn(session);
    when(request.getRequestDispatcher(any())).thenReturn(mock(RequestDispatcher.class));
    when(session.getAttribute(any())).thenReturn(null);

    HttpServletResponse response = mock(HttpServletResponse.class);
    loginServlet.doPost(request, response);

    ArgumentCaptor<Session> argument = ArgumentCaptor.forClass(Session.class);
    verify(session).setAttribute(eq("session"), argument.capture());

    verify(response).sendRedirect("homepage");
    assertEquals("Has user been correctly stored in session.",
        userToLogin.getId(), argument.getValue().getUser().getId());
  }

  @Test
  public void testPasswordIncorrect()
      throws SQLException, ReflectiveOperationException, ServletException, IOException {
    DaoManager dm = createTestDaoManager();
    dm.init(DatabaseLifecycle.MODEL_CLASSES);
    Dao<User> userDao = dm.get(User.class);

    User userToCreate = new User(
        "LoginTestUser2",
        "user@test.com",
        Password.fromPlaintext("pwd"),
        "House,A Street,A city,County,AB12 C34",
        "APPLICATION"
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
    when(request.getSession()).thenReturn(session);
    when(request.getRequestDispatcher(any())).thenReturn(mock(RequestDispatcher.class));
    when(session.getAttribute(any())).thenReturn(null);

    HttpServletResponse response = mock(HttpServletResponse.class);
    loginServlet.doPost(request, response);

    verify(request).getRequestDispatcher(LAYOUT_PAGE);
    verify(request).setAttribute(eq("errors"), any(Stack.class));
  }

  @Test
  public void testUsernameIncorrect()
      throws SQLException, ReflectiveOperationException, ServletException, IOException {
    DaoManager dm = createTestDaoManager();
    dm.init(DatabaseLifecycle.MODEL_CLASSES);
    Dao<User> userDao = dm.get(User.class);

    User userToCreate = new User(
        "LoginTestUser3",
        "user@test.com",
        Password.fromPlaintext("correctPassword"),
        "House,A Street,A city,County,AB12 C34",
        "APPLICATION"
    );
    userDao.insert(userToCreate);

    LoginServlet loginServlet = new LoginServlet();
    ReflectUtil.setFieldValue(loginServlet, "userDao", userDao);
    HttpServletRequest request = mock(HttpServletRequest.class);

    User userToLogin = userDao.select().where(new Where().eq("name", "LoginTestUser3")).first();
    String password = "correctPassword";

    when(request.getParameter("username")).thenReturn(userToLogin.getEmail() + "invalid");
    when(request.getParameter("password")).thenReturn(password);

    HttpSession session = mock(HttpSession.class);
    when(request.getSession()).thenReturn(session);
    when(request.getRequestDispatcher(any())).thenReturn(mock(RequestDispatcher.class));
    when(session.getAttribute(any())).thenReturn(null);

    HttpServletResponse response = mock(HttpServletResponse.class);
    loginServlet.doPost(request, response);

    verify(request).getRequestDispatcher(LAYOUT_PAGE);
    verify(request).setAttribute(eq("errors"), any(Stack.class));
  }
}