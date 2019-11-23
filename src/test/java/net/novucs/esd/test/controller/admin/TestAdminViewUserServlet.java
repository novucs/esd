package net.novucs.esd.test.controller.admin;

import static junit.framework.TestCase.assertTrue;
import static net.novucs.esd.test.util.TestUtils.createTestDaoManager;
import static org.mockito.Matchers.anyBoolean;
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
import net.novucs.esd.controllers.admin.AdminViewUserServlet;
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
  public void testRequestGetsMapAttribute()
      throws IOException, SQLException, ReflectiveOperationException {
    // Given
    AdminViewUserServlet servlet = new AdminViewUserServlet();
    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpSession httpSession = mock(HttpSession.class);
    HttpServletRequest request = mock(HttpServletRequest.class);

    DaoManager dm = createTestDaoManager();
    dm.init(DatabaseLifecycle.MODEL_CLASSES);
    setDaos(servlet, dm);

    // When
    when(httpSession.getAttribute(eq("session"))).thenReturn(userSession);
    when(request.getParameter("userId")).thenReturn("1");
    when(request.getRequestDispatcher("/layout.jsp")).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);
    servlet.doGet(request, response);

    // Assert
    verify(request).getRequestDispatcher(eq("/layout.jsp"));
  }

  private void setDaos(AdminViewUserServlet servlet,DaoManager dm)
      throws ReflectiveOperationException, SQLException {
    Dao<User> userDao =  dm.get(User.class);
    ReflectUtil.setFieldValue(servlet, "userDao", userDao);
    ReflectUtil.setFieldValue(servlet, "userRoleDao", dm.get(UserRole.class));
    ReflectUtil.setFieldValue(servlet, "roleDao", dm.get(Role.class));
    ReflectUtil.setFieldValue(servlet, "membershipDao", dm.get(Membership.class));
    ReflectUtil.setFieldValue(servlet, "claimDao", dm.get(Claim.class));
    userDao.insert(TestDummyDataUtils.getDummyBobUser());
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
