package net.novucs.esd.test;

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
import org.junit.Test;
import org.mockito.stubbing.Answer;

/**
 * The type Test example servlet.
 */
public class TestExampleServlet {

  /**
   * Test request gets map attribute.
   *
   * @throws ServletException             the servlet exception
   * @throws IOException                  the io exception
   * @throws ReflectiveOperationException the reflective operation exception
   */
  @Test
  public void testRequestGetsMapAttribute()
      throws ServletException, IOException, ReflectiveOperationException {
    // Given
    ExampleServlet serverlet = new ExampleServlet();
    setFieldValue(serverlet, "appName", "dummyApp");
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    // When
    when(request.getRequestDispatcher("/layout.jsp")).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    serverlet.doGet(request, response);

    // Assert
    verify(request, times(3)).setAttribute(any(String.class), any(Map.class));
  }

  /**
   * Test example in serverlett info.
   */
  @Test
  public void testExampleInServerlettInfo() {
    // Given
    ExampleServlet serverlet = new ExampleServlet();

    // When
    String serverletInfo = serverlet.getServletInfo();

    // Assert
    assertTrue("Example serverlett info must contain the key word example",
        serverletInfo.contains("example"));
  }
}
