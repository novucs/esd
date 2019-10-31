package net.novucs.esd.test;

import static com.sun.tools.doclint.Entity.times;
import static junit.framework.TestCase.assertTrue;
import static net.novucs.esd.test.ReflectUtil.setFieldValue;
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
import net.novucs.esd.MemberServlet;
import org.junit.Test;
import org.mockito.stubbing.Answer;

public class TestMemberServlet {

  @Test
  public void testRequestGetsMapAttribute()
      throws ServletException, IOException, ReflectiveOperationException {
    // Given
    MemberServlet servlet = new MemberServlet();
    setFieldValue(servlet, "appName", "dummyApp");
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    // When
    when(request.getRequestDispatcher("/layout.jsp")).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    servlet.doGet(request, response);

    // Assert
    verify(request, times(3)).setAttribute(any(String.class), any(Map.class));
  }

  @Test
  public void testHomepageInServletInfo() {
    // Given
    MemberServlet memberServlet = new MemberServlet();

    // When
    String servletInfo = servlet.getServletInfo();

    // Assert
    assertTrue("Member servlet info must contain the key word member",
        servletInfo.contains("members"));
  }
}
