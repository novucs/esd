package net.novucs.esd.test.controller.admin;

import static junit.framework.TestCase.assertTrue;
import static net.novucs.esd.test.util.TestUtils.createTestDaoManager;
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
import net.novucs.esd.controllers.admin.AdminDashboardServlet;
import net.novucs.esd.lifecycle.DatabaseLifecycle;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Application;
import net.novucs.esd.model.Claim;
import net.novucs.esd.model.Membership;
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

public class TestAdminDashboardServlet {

  private transient Session userSession;

  private static final String LAYOUT = "/layout.jsp";

  @Before
  public void initialiseTest() {
    userSession = new Session();
    userSession.setUser(TestDummyDataUtils.getDummyUser());
  }

  @Test
  public void testRequestHasSession()
      throws ServletException, IOException, SQLException, ReflectiveOperationException {
    // Given
    AdminDashboardServlet servlet = new AdminDashboardServlet();
    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpSession httpSession = mock(HttpSession.class);
    HttpServletRequest request = mock(HttpServletRequest.class);
    setServletDaos(servlet);
    // When
    when(httpSession.getAttribute(eq("session"))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);
    servlet.doGet(request, response);

    // Assert
    verify(request).getRequestDispatcher(eq(LAYOUT));
  }

  private void setServletDaos(AdminDashboardServlet servlet)
      throws SQLException, ReflectiveOperationException {
    DaoManager dm = createTestDaoManager();
    dm.init(DatabaseLifecycle.MODEL_CLASSES);
    Dao<User> userDao = dm.get(User.class);
    Dao<Role> roleDao = dm.get(Role.class);
    Dao<UserRole> userRoleDao = dm.get(UserRole.class);
    Dao<Application> applicationDao = dm.get(Application.class);
    Dao<Claim> claimDao = dm.get(Claim.class);
    ReflectUtil.setFieldValue(servlet, "userDao", userDao);
    ReflectUtil.setFieldValue(servlet, "userRoleDao", userRoleDao);
    ReflectUtil.setFieldValue(servlet, "roleDao", roleDao);
    ReflectUtil.setFieldValue(servlet, "applicationDao", applicationDao);
    ReflectUtil.setFieldValue(servlet, "claimDao", claimDao);
  }

  @Test
  public void requiredAttributesSet()
      throws ServletException, IOException, SQLException, ReflectiveOperationException {
    AdminDashboardServlet servlet = new AdminDashboardServlet();
    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpSession httpSession = mock(HttpSession.class);
    HttpServletRequest request = mock(HttpServletRequest.class);
    setServletDaos(servlet);
    createRequiredAttributeData();

    when(httpSession.getAttribute(eq("session"))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);
    servlet.doGet(request, response);

    verify(request).setAttribute(eq("outstandingMemberApplications"), eq(1));
    verify(request).setAttribute(eq("currentMembers"), eq(1));
    verify(request).setAttribute(eq("outstandingBalances"), eq(1));
    // Assert
    verify(request).getRequestDispatcher(eq(LAYOUT));
  }

  private void createRequiredAttributeData()
      throws SQLException {
    DaoManager dm = createTestDaoManager();
    dm.init(DatabaseLifecycle.MODEL_CLASSES);
    Dao<User> userDao = dm.get(User.class);
    Dao<Role> roleDao = dm.get(Role.class);
    Dao<UserRole> userRoleDao = dm.get(UserRole.class);
    Dao<Application> applicationDao = dm.get(Application.class);
    Dao<Claim> claimDao = dm.get(Claim.class);
    Dao<Membership> membershipDao = dm.get(Membership.class);
    User bob = TestDummyDataUtils.getDummyBobUser();
    userDao.insert(bob);
    Role member = new Role("Member");
    roleDao.insert(member);
    userRoleDao.insert(new UserRole(bob.getId(), member.getId()));
    applicationDao.insert(new Application(bob.getId(), BigDecimal.ZERO));
    Membership m = new Membership(bob.getId(), BigDecimal.ZERO, "active", ZonedDateTime.now(),
        true);
    membershipDao.insert(m);
    claimDao.insert(new Claim(m.getId(), new BigDecimal(20), ZonedDateTime.now()));
  }

  @Test
  public void testServletInfo() {
    // Given
    AdminDashboardServlet servlet = new AdminDashboardServlet();

    // When
    String servletInfo = servlet.getServletInfo();

    // Assert
    assertTrue("getServletInfo must match the class name.",
        servletInfo.equalsIgnoreCase(servlet.getClass().getSimpleName()));
  }
}
