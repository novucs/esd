package net.novucs.esd.test.controller;

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
import net.novucs.esd.controllers.HomepageServlet;
import org.junit.Test;
import org.mockito.stubbing.Answer;

/**
 * The type Test homepage servlet.
 */
public class TestHomepageServlet {

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
    when(request.getRequestDispatcher("/layout.jsp")).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(mock(HttpSession.class));

    HomepageServlet servlet = new HomepageServlet();
    HttpServletResponse response = mock(HttpServletResponse.class);
    servlet.doGet(request, response);

    // Assert
    verify(request).getRequestDispatcher(eq("/layout.jsp"));
  }

  /**
   * Test homepage in servlet info.
   */
  @Test
  public void testHomepageInServletInfo() {
    // Given
    HomepageServlet servlet = new HomepageServlet();

    // When
    String servletInfo = servlet.getServletInfo();

    // Assert
    assertTrue("Homepage servlet info must contain the key word Homepage",
        servletInfo.contains("Homepage"));
  }
}
