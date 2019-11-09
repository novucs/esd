package net.novucs.esd.test.controller;

import static junit.framework.TestCase.assertTrue;
import static net.novucs.esd.util.ReflectUtil.setFieldValue;
import static org.mockito.Matchers.any;
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
import net.novucs.esd.ExampleServlet;
import net.novucs.esd.controllers.HomepageServlet;
import org.junit.Test;
import org.mockito.stubbing.Answer;

public class TestHomepageServlet {

  @Test
  public void testRequestGetsMapAttribute()
      throws ServletException, IOException, ReflectiveOperationException {
    // Given
    HomepageServlet servlet = new HomepageServlet();
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    // When
    when(request.getRequestDispatcher("/homepage.jsp")).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    servlet.doGet(request, response);

  }

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
