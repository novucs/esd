package net.novucs.esd.test.controller;

import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static net.novucs.esd.util.ReflectUtil.setFieldValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
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
import net.novucs.esd.controllers.LogoutServlet;
import org.junit.Test;
import org.mockito.stubbing.Answer;

public class TestLogoutServlet {

  @Test
  public void testRequestGetsMapAttribute()
      throws ServletException, IOException, ReflectiveOperationException {
    // Given
    LogoutServlet servlet = new LogoutServlet();
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    // When
    when(request.getSession(true)).thenReturn(mock(HttpSession.class));
    when(request.getRequestDispatcher(any())).thenReturn(mock(RequestDispatcher.class));
    servlet.doGet(request, response);

    // Assert
    verify(response, times(1)).sendRedirect(eq("login"));
  }

  @Test
  public void testLogoutInServletInfo() {
    // Given
    LogoutServlet servlet = new LogoutServlet();

    // When
    String servletInfo = servlet.getServletInfo();

    // Assert
    assertTrue("Logout servlet info must contain the key word Logout",
        servletInfo.contains("Logout"));
  }
}