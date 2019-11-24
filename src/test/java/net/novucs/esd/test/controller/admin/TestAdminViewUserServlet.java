package net.novucs.esd.test.controller.admin;

import static junit.framework.TestCase.assertTrue;
import static net.novucs.esd.test.util.TestUtils.createTestDaoManager;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.novucs.esd.controllers.admin.AdminViewUserServlet;
import net.novucs.esd.lifecycle.DatabaseLifecycle;
import net.novucs.esd.lifecycle.Session;
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

public class TestAdminViewUserServlet {

  /*
   TODO: Refactor this to test for the implementation of AdminViewUserServlet
   */

  private transient Session userSession;

  @Before
  public void initialiseTest() {
    userSession = new Session();
    userSession.setUser(TestDummyDataUtils.getDummyUser());
  }

  @Test
  public void testPageGetsMemberships()
      throws SQLException, ReflectiveOperationException, IOException {
    AdminViewUserServlet servlet = new AdminViewUserServlet();
    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpSession httpSession = mock(HttpSession.class);
    HttpServletRequest request = mock(HttpServletRequest.class);

    DaoManager dm = createTestDaoManager();
    dm.init(DatabaseLifecycle.MODEL_CLASSES);
    User bob = TestDummyDataUtils.getDummyBobUser();
    Dao<User> userDao = dm.get(User.class);
    Dao<Membership> membershipDao = dm.get(Membership.class);
    setDaoFields(servlet, dm, userDao, membershipDao);
    setData(bob, userDao, membershipDao);

    // When
    when(httpSession.getAttribute(eq("session"))).thenReturn(userSession);
    when(request.getParameter("userId")).thenReturn("1");
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
      Dao<Membership> membershipDao)  throws ReflectiveOperationException {
    ReflectUtil.setFieldValue(servlet, "userDao", userDao);
    ReflectUtil.setFieldValue(servlet, "userRoleDao", dm.get(UserRole.class));
    ReflectUtil.setFieldValue(servlet, "roleDao", dm.get(Role.class));
    ReflectUtil.setFieldValue(servlet, "membershipDao", membershipDao);
    ReflectUtil.setFieldValue(servlet, "claimDao", dm.get(Claim.class));
  }

  private void setData(User user, Dao<User> userDao, Dao<Membership> membershipDao)
      throws SQLException {
    userDao.insert(user);
    membershipDao.insert(new Membership(user.getId(), BigDecimal.ZERO,
        "STATUS", ZonedDateTime.now(), true));
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
