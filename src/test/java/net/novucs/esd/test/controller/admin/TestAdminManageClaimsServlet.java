package net.novucs.esd.test.controller.admin;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.novucs.esd.controllers.admin.AdminManageClaimsServlet;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.User;
import net.novucs.esd.util.DateUtil;
import net.novucs.esd.util.Password;
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
    userSession.setUser(new User(
        "testuser",
        "testuser@example.com",
        Password.fromPlaintext("test_pass"),
        "Line 1,Line 2,City,County,Postcode",
        new DateUtil().getDateFromString("2000-01-01"),
        "APPLICATION"
    ));
  }

  @Test
  public void testRequestGetsMapAttribute()
      throws ServletException, IOException {
    // Given
    AdminManageClaimsServlet servlet = new AdminManageClaimsServlet();
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
  public void testRequestHasNoSession()
      throws ServletException, IOException {
    // Given
    AdminManageClaimsServlet servlet = new AdminManageClaimsServlet();
    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpSession httpSession = mock(HttpSession.class);
    HttpServletRequest request = mock(HttpServletRequest.class);

    // When
    when(request.getSession(anyBoolean())).thenReturn(httpSession);
    when(httpSession.getAttribute(eq("session"))).thenReturn(null);
    servlet.doGet(request, response);

    // Assert
    verify(response, times(1)).sendRedirect("../login");
  }

  @Test
  public void testServletInfo() {
    // Given
    AdminManageClaimsServlet servlet = new AdminManageClaimsServlet();

    // When
    String servletInfo = servlet.getServletInfo();

    // Assert
    assertTrue("AdminManageClaimsServlet must have 'Admin Manage Claims' in its info",
        servletInfo.contains("Admin Manage Claims"));
  }
}
