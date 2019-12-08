package net.novucs.esd.test.controller.admin;

import static junit.framework.TestCase.assertTrue;
import static net.novucs.esd.test.util.TestUtil.createTestDaoManager;
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
import net.novucs.esd.controllers.admin.AdminManageClaimsServlet;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Claim;
import net.novucs.esd.model.Membership;
import net.novucs.esd.model.User;
import net.novucs.esd.orm.DaoManager;
import net.novucs.esd.test.util.TestDummyDataUtil;
import net.novucs.esd.util.ReflectUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;

public class TestAdminManageClaimsServlet {

  /*
   TODO: Refactor this to test for the implementation of AdminManageClaimsServlet
   */

  private transient Session userSession;

  @Before
  public void initialiseTest() {
    userSession = new Session();
    userSession.setUser(TestDummyDataUtil.getDummyUser());
  }

  @Test
  public void testRequestGetsMapAttribute()
      throws ServletException, IOException, SQLException, ReflectiveOperationException {
    // Given
    AdminManageClaimsServlet servlet = new AdminManageClaimsServlet();
    DaoManager dm = createTestDaoManager(true);
    ReflectUtil.setFieldValue(servlet, "claimDao", dm.get(Claim.class));
    ReflectUtil.setFieldValue(servlet, "membershipDao", dm.get(Membership.class));
    ReflectUtil.setFieldValue(servlet, "userDao", dm.get(User.class));
    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpSession httpSession = mock(HttpSession.class);
    HttpServletRequest request = mock(HttpServletRequest.class);

    // When
    when(httpSession.getAttribute(eq("session"))).thenReturn(userSession);
    when(request.getRequestDispatcher("/layout.jsp")).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);
    servlet.doGet(request, response);

    // Assert
    verify(request).getRequestDispatcher(eq("/layout.jsp"));
  }

  @Test
  public void testServletInfo() {
    // Given
    AdminManageClaimsServlet servlet = new AdminManageClaimsServlet();

    // When
    String servletInfo = servlet.getServletInfo();

    // Assert
    assertTrue("getServletInfo must match the class name.",
        servletInfo.equalsIgnoreCase(servlet.getClass().getSimpleName()));
  }
}
