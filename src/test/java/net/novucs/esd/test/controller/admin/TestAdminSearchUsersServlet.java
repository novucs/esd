package net.novucs.esd.test.controller.admin;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.novucs.esd.ExampleServlet;
import net.novucs.esd.controllers.admin.AdminSearchUsersServlet;
import org.junit.Test;
import org.mockito.stubbing.Answer;

public class TestAdminSearchUsersServlet {

  /*
   TODO: Refactor this to test for the implementation of AdminSearchUsersServlet
   */

  @Test
  public void testRequestGetsMapAttribute()
      throws ServletException, IOException, ReflectiveOperationException {
    // Given
    AdminSearchUsersServlet servlet = new AdminSearchUsersServlet();
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    // When
    when(request.getRequestDispatcher("/layout.jsp")).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(mock(HttpSession.class));
    servlet.doGet(request, response);

    // Assert
    verify(request, times(3)).setAttribute(any(String.class), any(Map.class));
  }

  @Test
  public void testServletInfo() {
    // Given
    ExampleServlet servlet = new ExampleServlet();

    // When
    String servletInfo = servlet.getServletInfo();

    // Assert
    assertTrue("AdminSearchUsersServlet must have 'Admin Search Users' in its info",
        servletInfo.contains("Admin Search Users"));
  }
}
