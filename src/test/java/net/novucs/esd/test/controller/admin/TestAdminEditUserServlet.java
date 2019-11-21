package net.novucs.esd.test.controller.admin;

import static junit.framework.TestCase.assertTrue;
import static net.novucs.esd.test.util.TestUtils.createTestDaoManager;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.notNull;
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
import net.novucs.esd.controllers.admin.AdminEditUserServlet;
import net.novucs.esd.lifecycle.DatabaseLifecycle;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Role;
import net.novucs.esd.model.User;
import net.novucs.esd.model.UserRole;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.DaoManager;
import net.novucs.esd.test.TestDummyDataUtils;
import net.novucs.esd.util.ReflectUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;

public class TestAdminEditUserServlet {

  private static final String SESSION_LABEL = "session";
  private static final String USER_ID_LABEL = "userId";
  private static final String LAYOUT_JSP_LABEL = "/layout.jsp";

  private transient Session userSession;
  private transient User dummyUser;
  private transient Role userRole;
  private transient Role adminRole;

  @Before
  public void initialiseTest() {
    userSession = new Session();
    userSession.setUser(TestDummyDataUtils.getDummyAdminUser());
    dummyUser = TestDummyDataUtils.getDummyUser();
    userRole = new Role("User");
    adminRole = new Role("Administrator");
  }

  private void setServletDaos(AdminEditUserServlet servlet)
      throws ReflectiveOperationException, SQLException {
    DaoManager dm = createTestDaoManager();
    dm.init(DatabaseLifecycle.MODEL_CLASSES);
    Dao<User> userDao = dm.get(User.class);
    Dao<UserRole> userRoleDao = dm.get(UserRole.class);
    Dao<Role> roleDao = dm.get(Role.class);

    // Insert our Dummy User with Roles
    roleDao.insert(userRole);
    roleDao.insert(adminRole);
    userDao.insert(dummyUser);
    userRoleDao.insert(new UserRole(dummyUser.getId(), userRole.getId()));
    userRoleDao.insert(new UserRole(dummyUser.getId(), adminRole.getId()));

    // Reflect DAO
    ReflectUtil.setFieldValue(servlet, "userDao", userDao);
    ReflectUtil.setFieldValue(servlet, "userRoleDao", userRoleDao);
    ReflectUtil.setFieldValue(servlet, "roleDao", roleDao);
  }

  @Test
  public void testUpdateInvalidUser()
      throws IOException, ServletException, SQLException, ReflectiveOperationException {
    // Given
    AdminEditUserServlet servlet = new AdminEditUserServlet();
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
    servlet.doPost(request, response);

    // Verify
    verify(request).setAttribute(eq("error"), anyString());
    verify(response).sendError(anyInt());
  }

  @Test
  public void testUpdateUserDetails()
      throws IOException, ServletException, SQLException, ReflectiveOperationException {
    // Given
    AdminEditUserServlet servlet = new AdminEditUserServlet();
    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);
    setServletDaos(servlet);
    String[] roles = { userRole.getId().toString(), adminRole.getId().toString() };

    // When
    when(request.getSession(anyBoolean())).thenReturn(session);
    when(session.getAttribute(eq(SESSION_LABEL))).thenReturn(userSession);
    when(request.getParameter(eq(USER_ID_LABEL))).thenReturn(
        dummyUser.getId().toString());
    when(request.getParameter(eq("name"))).thenReturn(
        "Test User");
    when(request.getParameter(eq("email"))).thenReturn(
        "test@esd.net");
    when(request.getParameter(eq("date_of_birth"))).thenReturn(
        "1970-01-01");
    when(request.getParameter(eq("password1"))).thenReturn("");
    when(request.getParameter(eq("password2"))).thenReturn("");
    when(request.getParameterValues(eq("roles"))).thenReturn(roles);
    when(request.getRequestDispatcher(LAYOUT_JSP_LABEL)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    servlet.doPost(request, response);

    // Verify
    verify(request).setAttribute(eq("editUser"), notNull());
    verify(request).setAttribute(eq("updated"), eq(true));
    verify(request).setAttribute(eq("availableRoles"), any());
    verify(request).setAttribute(eq("editUserRoles"), any());
    verify(request).setAttribute(eq("errors"), anyList());
  }

  @Test
  public void testUpdateUserPassword()
      throws SQLException, ReflectiveOperationException, IOException, ServletException {
    // Given
    AdminEditUserServlet servlet = new AdminEditUserServlet();
    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);
    setServletDaos(servlet);
    String[] roles = { userRole.getId().toString(), adminRole.getId().toString() };

    // When
    when(request.getSession(anyBoolean())).thenReturn(session);
    when(session.getAttribute(eq(SESSION_LABEL))).thenReturn(userSession);
    when(request.getParameter(eq(USER_ID_LABEL))).thenReturn(
        dummyUser.getId().toString());
    when(request.getParameter(eq("name"))).thenReturn(
        "Test User");
    when(request.getParameter(eq("email"))).thenReturn(
        "test@esd.net");
    when(request.getParameter(eq("date_of_birth"))).thenReturn(
        "1970-01-01");
    when(request.getParameter(eq("password1"))).thenReturn("enterprise");
    when(request.getParameter(eq("password2"))).thenReturn("enterprise");
    when(request.getParameterValues(eq("roles"))).thenReturn(roles);
    when(request.getRequestDispatcher(LAYOUT_JSP_LABEL)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    servlet.doPost(request, response);

    // Verify
    verify(request).setAttribute(eq("editUser"), notNull());
    verify(request).setAttribute(eq("updated"), eq(true));
    verify(request).setAttribute(eq("availableRoles"), any());
    verify(request).setAttribute(eq("editUserRoles"), any());
    verify(request).setAttribute(eq("notice"), anyString());
    verify(request).setAttribute(eq("errors"), anyList());
  }

  @Test
  public void testUpdateUserIncorrectRepeatedPassword()
      throws SQLException, ReflectiveOperationException, IOException, ServletException {
    // Given
    AdminEditUserServlet servlet = new AdminEditUserServlet();
    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);
    setServletDaos(servlet);
    String[] roles = { userRole.getId().toString(), adminRole.getId().toString() };

    // When
    when(request.getSession(anyBoolean())).thenReturn(session);
    when(session.getAttribute(eq(SESSION_LABEL))).thenReturn(userSession);
    when(request.getParameter(eq(USER_ID_LABEL))).thenReturn(
        dummyUser.getId().toString());
    when(request.getParameter(eq("name"))).thenReturn(
        "Test User");
    when(request.getParameter(eq("email"))).thenReturn(
        "test@esd.net");
    when(request.getParameter(eq("date_of_birth"))).thenReturn(
        "1970-01-01");
    when(request.getParameter(eq("password1"))).thenReturn("enterprise");
    when(request.getParameter(eq("password2"))).thenReturn("depression");
    when(request.getParameterValues(eq("roles"))).thenReturn(roles);
    when(request.getRequestDispatcher(LAYOUT_JSP_LABEL)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    servlet.doPost(request, response);

    // Verify
    verify(request).setAttribute(eq("editUser"), notNull());
    verify(request).setAttribute(eq("updated"), eq(true));
    verify(request).setAttribute(eq("availableRoles"), any());
    verify(request).setAttribute(eq("editUserRoles"), any());
    verify(request).setAttribute(eq("errors"), anyList());
  }

  @Test
  public void testServletInfo() {
    // Given
    AdminEditUserServlet servlet = new AdminEditUserServlet();

    // When
    String servletInfo = servlet.getServletInfo();

    // Assert
    assertTrue("getServletInfo must match the class name.",
        servletInfo.equalsIgnoreCase(servlet.getClass().getSimpleName()));
  }
}
