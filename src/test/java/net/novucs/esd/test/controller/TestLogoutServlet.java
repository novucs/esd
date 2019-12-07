package net.novucs.esd.test.controller;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.any;
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
import net.novucs.esd.controllers.LogoutServlet;
import org.junit.Test;

/**
 * The type Test logout servlet.
 */
public class TestLogoutServlet {

  private final LogoutServlet servlet = new LogoutServlet();
  /**
   * Test request gets map attribute.
   *
   * @throws ServletException the servlet exception
   * @throws IOException      the io exception
   */

  @Test
  public void testRequestGetsMapAttribute()
      throws ServletException, IOException {
    // Given
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    // When
    when(request.getSession(true)).thenReturn(mock(HttpSession.class));
    when(request.getRequestDispatcher(any())).thenReturn(mock(RequestDispatcher.class));
    servlet.doGet(request, response);

    // Assert
    verify(response, times(1)).sendRedirect(eq("login"));
  }

  /**
   * Test logout in servlet info.
   */
  @Test
  public void testLogoutInServletInfo() {
    // When
    String servletInfo = servlet.getServletInfo();

    // Assert
    assertTrue("Logout servlet info must contain the key word Logout",
        servletInfo.contains("Logout"));
  }
}