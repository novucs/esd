package net.novucs.esd.test.controller.admin;

import static junit.framework.TestCase.assertTrue;
import static net.novucs.esd.test.util.TestUtil.createTestDaoManager;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.novucs.esd.controllers.admin.AdminViewUserServlet;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Claim;
import net.novucs.esd.model.Membership;
import net.novucs.esd.model.Role;
import net.novucs.esd.model.User;
import net.novucs.esd.model.UserRole;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.DaoManager;
import net.novucs.esd.orm.Where;
import net.novucs.esd.test.util.TestDummyDataUtil;
import net.novucs.esd.util.ReflectUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;

public class TestAdminViewUserServlet {

  /*
   TODO: Refactor this to test for the implementation of AdminViewUserServlet
   */

  private transient Session userSession;

  @Before
  public void initialiseTest() {
    userSession = new Session();
    userSession.setUser(TestDummyDataUtil.getDummyUser());
  }

  @Test
  public void testPageGetsMemberships()
      throws SQLException, ReflectiveOperationException, IOException {
    AdminViewUserServlet servlet = new AdminViewUserServlet();
    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpSession httpSession = mock(HttpSession.class);
    HttpServletRequest request = mock(HttpServletRequest.class);

    DaoManager dm = createTestDaoManager(true);
    User bob = TestDummyDataUtil.getDummyBobUser();
    Dao<User> userDao = dm.get(User.class);
    Dao<Membership> membershipDao = dm.get(Membership.class);
    Dao<UserRole> userRoleDao = dm.get(UserRole.class);
    Dao<Role> roleDao = dm.get(Role.class);

    setDaoFields(servlet, dm, userDao, membershipDao, userRoleDao, roleDao);
    setData(bob, userDao, membershipDao, userRoleDao, roleDao);

    // When
    when(httpSession.getAttribute(eq("session"))).thenReturn(userSession);
    when(request.getParameter("userId")).thenReturn(String.valueOf(bob.getId()));
    when(request.getRequestDispatcher("/layout.jsp")).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);
    servlet.doGet(request, response);

    // Assert
    verify(request).getRequestDispatcher(eq("/layout.jsp"));
    verify(request).setAttribute(eq("user"), eq(bob));
    verify(request).setAttribute(eq("roleText"), anyString());
    verify(request).setAttribute(eq("claims"), anyList());
    verify(request).setAttribute(eq("memberships"), anyList());
  }

  private void setDaoFields(AdminViewUserServlet servlet, DaoManager dm, Dao<User> userDao,
      Dao<Membership> membershipDao, Dao<UserRole> userRoleDao,
      Dao<Role> roleDao) throws ReflectiveOperationException {
    ReflectUtil.setFieldValue(servlet, "userDao", userDao);
    ReflectUtil.setFieldValue(servlet, "userRoleDao", userRoleDao);
    ReflectUtil.setFieldValue(servlet, "roleDao", roleDao);
    ReflectUtil.setFieldValue(servlet, "membershipDao", membershipDao);
    ReflectUtil.setFieldValue(servlet, "claimDao", dm.get(Claim.class));
  }

  private void setData(User user, Dao<User> userDao, Dao<Membership> membershipDao,
      Dao<UserRole> userRoleDao, Dao<Role> roleDao)
      throws SQLException {
    userDao.insert(user);
    Role adminRole = roleDao.select().where(new Where().eq("name", "Administrator")).one();
    userRoleDao.insert(new UserRole(user.getId(), adminRole.getId()));
    membershipDao.insert(new Membership(user.getId(),
        ZonedDateTime.now().minusYears(1), true));
    membershipDao.insert(new Membership(user.getId(),
        ZonedDateTime.now(), false));
  }

  @Test
  public void testServletInfo() {
    // Given
    AdminViewUserServlet servlet = new AdminViewUserServlet();

    // When
    String servletInfo = servlet.getServletInfo();

    // Assert
    assertTrue("getServletInfo must match the class name.",
        servletInfo.equalsIgnoreCase(servlet.getClass().getSimpleName()));
  }
}
