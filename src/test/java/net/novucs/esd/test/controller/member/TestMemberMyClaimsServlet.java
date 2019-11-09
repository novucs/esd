package net.novucs.esd.test.controller.member;

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
import net.novucs.esd.controllers.member.MemberMyClaimsServlet;
import org.junit.Test;
import org.mockito.stubbing.Answer;

public class TestMemberMyClaimsServlet {

  /*
   TODO: Refactor this to test for the implementation of MemberMyClaimsServlet
   */

  @Test
  public void testRequestGetsMapAttribute()
      throws ServletException, IOException {
    // Given
    MemberMyClaimsServlet servlet = new MemberMyClaimsServlet();
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    // When
    when(request.getRequestDispatcher("/layout.jsp")).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(mock(HttpSession.class));
    servlet.doGet(request, response);

    // Assert
    verify(request, times(0)).setAttribute(any(String.class), any(Map.class));
  }

  @Test
  public void testServletInfo() {
    // Given
    MemberMyClaimsServlet servlet = new MemberMyClaimsServlet();

    // When
    String servletInfo = servlet.getServletInfo();

    // Assert
    assertTrue("MemberMyClaimsServlet must have 'Member My Claims' in its info",
        servletInfo.contains("Member My Claims"));
  }
}
