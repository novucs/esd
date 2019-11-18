package net.novucs.esd.test.controller.member;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.novucs.esd.controllers.member.MemberDashboardServlet;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.test.TestDummyDataUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;

public class TestMemberDashboardServlet {

  /*
   TODO: Refactor this to test for the implementation of MemberDashboardServlet
   */

  private transient Session userSession;

  @Before
  public void initialiseTest() {
    userSession = new Session();
    userSession.setUser(TestDummyDataUtils.getDummyUser());
  }

  @Test
  public void testRequestGetsMapAttribute()
      throws ServletException, IOException {
    // Given
    MemberDashboardServlet servlet = new MemberDashboardServlet();
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
    MemberDashboardServlet servlet = new MemberDashboardServlet();

    // When
    String servletInfo = servlet.getServletInfo();

    // Assert
    assertTrue("getServletInfo must match the class name.",
        servletInfo.equalsIgnoreCase(servlet.getClass().getSimpleName()));
  }
}
