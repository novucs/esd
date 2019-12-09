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
import net.novucs.esd.controllers.UserProfileServlet;
import net.novucs.esd.lifecycle.DatabaseLifecycle;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Action;
import net.novucs.esd.model.Role;
import net.novucs.esd.model.User;
import net.novucs.esd.model.UserAction;
import net.novucs.esd.model.UserRole;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.DaoManager;
import net.novucs.esd.test.util.TestDummyDataUtil;
import net.novucs.esd.util.ReflectUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;

public class TestUserProfileServlet {

  private static final String SESSION_LABEL = "session";
  private static final String USER_ID_LABEL = "userId";
  private static final String ERRORS_LABEL = "errors";
  private static final String LAYOUT_JSP_LABEL = "/layout.jsp";

  private transient Session userSession;
  private transient Role userRole;

  @Before
  public void initialiseTest() {
    userSession = new Session();
    userSession.setUser(TestDummyDataUtil.getDummyAdminUser());
    userRole = new Role("User");
  }

  private void setServletDaos(UserProfileServlet servlet)
      throws ReflectiveOperationException, SQLException {
    DaoManager dm = createTestDaoManager();
    dm.init(DatabaseLifecycle.MODEL_CLASSES);
    Dao<User> userDao = dm.get(User.class);
    Dao<UserRole> userRoleDao = dm.get(UserRole.class);
    Dao<Role> roleDao = dm.get(Role.class);

    // Insert our Dummy User with Roles
    roleDao.insert(userRole);
    userDao.insert(userSession.getUser());
    userRoleDao.insert(
        new UserRole(userSession.getUser().getId(), userRole.getId())
    );

    // Reflect DAO
    ReflectUtil.setFieldValue(servlet, "userDao", userDao);
    ReflectUtil.setFieldValue(servlet, "actionDao", dm.get(Action.class));
    ReflectUtil.setFieldValue(servlet, "userActionDao", dm.get(UserAction.class));
  }

  @Test
  public void testUpdateInvalidUser()
      throws IOException, ServletException, SQLException, ReflectiveOperationException {
    // Given
    UserProfileServlet servlet = new UserProfileServlet();
    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);
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

  @Test
  public void testUpdateUserDetails()
      throws IOException, ServletException, SQLException, ReflectiveOperationException {
    // Given
    UserProfileServlet servlet = new UserProfileServlet();
    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);
    setServletDaos(servlet);

    // When
    when(request.getSession(anyBoolean())).thenReturn(session);
    when(session.getAttribute(eq(SESSION_LABEL))).thenReturn(userSession);
    when(request.getParameter(eq("fullname"))).thenReturn("Test User");
    when(request.getParameter(eq("email"))).thenReturn(
        "test@esd.net");
    when(request.getParameter(eq("address"))).thenReturn(
        "1 ESD Lane");
    when(request.getParameter(eq("date_of_birth"))).thenReturn(
        "1970-01-01");
    when(request.getParameter(eq("current_password"))).thenReturn("");
    when(request.getParameter(eq("new_password"))).thenReturn("");
    when(request.getRequestDispatcher(LAYOUT_JSP_LABEL)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    servlet.doPost(request, response);

    // Verify
    verify(request).setAttribute(eq("updated"), eq(true));
  }

  @Test
  public void testUpdateUserPassword()
      throws SQLException, ReflectiveOperationException, IOException, ServletException {
    // Given
    UserProfileServlet servlet = new UserProfileServlet();
    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);
    setServletDaos(servlet);
    String[] roles = {userRole.getId().toString()};

    // When
    when(request.getSession(anyBoolean())).thenReturn(session);
    when(session.getAttribute(eq(SESSION_LABEL))).thenReturn(userSession);
    when(request.getParameter(eq("fullname"))).thenReturn(
        "Test User");
    when(request.getParameter(eq("email"))).thenReturn(
        "test@esd.net");
    when(request.getParameter(eq("address"))).thenReturn(
        "1 ESD Lane");
    when(request.getParameter(eq("date_of_birth"))).thenReturn(
        "1970-01-01");
    when(request.getParameter(eq("current_password"))).thenReturn("bob");
    when(request.getParameter(eq("new_password"))).thenReturn("enterprise");
    when(request.getParameterValues(eq("roles"))).thenReturn(roles);
    when(request.getRequestDispatcher(LAYOUT_JSP_LABEL)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    servlet.doPost(request, response);

    // Verify
    verify(request).setAttribute(eq("updated"), eq(true));
  }

  @Test
  public void testUpdateUserIncorrectRepeatedPassword()
      throws SQLException, ReflectiveOperationException, IOException, ServletException {
    // Given
    UserProfileServlet servlet = new UserProfileServlet();
    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);
    setServletDaos(servlet);

    // When
    when(request.getSession(anyBoolean())).thenReturn(session);
    when(session.getAttribute(eq(SESSION_LABEL))).thenReturn(userSession);
    when(request.getParameter(eq("fullname"))).thenReturn(
        "Test User");
    when(request.getParameter(eq("email"))).thenReturn(
        "test@esd.net");
    when(request.getParameter(eq("address"))).thenReturn(
        "1 ESD Lane");
    when(request.getParameter(eq("date_of_birth"))).thenReturn(
        "1970-01-01");
    when(request.getParameter(eq("current_password"))).thenReturn("enterprise");
    when(request.getParameter(eq("new_password"))).thenReturn("depression");
    when(request.getRequestDispatcher(LAYOUT_JSP_LABEL)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    servlet.doPost(request, response);

    // Verify
    verify(request).setAttribute(eq("updated"), eq(true));
  }

  @Test
  public void testGetInvalidEditUser()
      throws IOException, ServletException, SQLException, ReflectiveOperationException {
    // Given
    UserProfileServlet servlet = new UserProfileServlet();
    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);
    setServletDaos(servlet);

    // When
    when(request.getSession(anyBoolean())).thenReturn(session);
    when(session.getAttribute(eq(SESSION_LABEL))).thenReturn(userSession);
    when(request.getParameter(eq(USER_ID_LABEL))).thenReturn("696969");
    when(request.getRequestDispatcher(LAYOUT_JSP_LABEL)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    servlet.doGet(request, response);

    // Verify
    verify(request).setAttribute(eq(ERRORS_LABEL), anyList());
  }

  @Test
  public void testGetEditUser()
      throws IOException, ServletException, SQLException, ReflectiveOperationException {
    // Given
    UserProfileServlet servlet = new UserProfileServlet();
    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);
    setServletDaos(servlet);

    // When
    when(request.getSession(anyBoolean())).thenReturn(session);
    when(session.getAttribute(eq(SESSION_LABEL))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT_JSP_LABEL)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    servlet.doGet(request, response);

    // Verify
    verify(request).setAttribute(eq(ERRORS_LABEL), anyList());
  }

  @Test
  public void testServletInfo() {
    // Given
    UserProfileServlet servlet = new UserProfileServlet();

    // When
    String servletInfo = servlet.getServletInfo();

    // Assert
    assertTrue("getServletInfo must match the class name.",
        servletInfo.equalsIgnoreCase(servlet.getClass().getSimpleName()));
  }
}
