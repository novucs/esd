package net.novucs.esd.test.filter;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.novucs.esd.filter.LoggedInFilter;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Role;
import net.novucs.esd.test.TestDummyDataUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;

public class TestLoggedInFilter {

  private static final String ACCESSIBLE_PAGE = "/app/settings";
  private static final String SESSION_LABEL = "session";
  private static final String LAYOUT_JSP = "/layout.jsp";
  private transient Session userSession;

  @Before
  public void initialiseTest() {
    userSession = new Session();
    userSession.setUser(TestDummyDataUtils.getDummyUser());
  }

  @Test
  public void testLoggedInFilterWithNullUser()
      throws ServletException, IOException {
    // Given
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpSession httpSession = mock(HttpSession.class);
    FilterChain chain = mock(FilterChain.class);

    // Null User
    Session s = userSession;
    s.setUser(null);

    // When
    when(request.getRequestURI()).thenReturn(ACCESSIBLE_PAGE);
    when(request.getSession(anyBoolean())).thenReturn(httpSession);
    when(httpSession.getAttribute(SESSION_LABEL)).thenReturn(userSession);

    // Request
    LoggedInFilter filter = new LoggedInFilter();
    filter.doFilter(request, response, chain);

    // Assert
    verify(response).sendRedirect(anyString());
  }

  @Test
  public void testLoggedInFilterWithUserCorrectRoles()
      throws ServletException, IOException {
    // Given
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpSession httpSession = mock(HttpSession.class);
    FilterChain chain = mock(FilterChain.class);

    // Roles
    List<Role> sessionRoles = Arrays.asList(new Role("User"));
    userSession.setRoles(sessionRoles);

    // When
    when(request.getRequestURI()).thenReturn(ACCESSIBLE_PAGE);
    when(httpSession.getAttribute(eq(SESSION_LABEL))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT_JSP)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);

    // Request
    LoggedInFilter filter = new LoggedInFilter();
    filter.doFilter(request, response, chain);

    // Assert
    verify(chain, times(1)).doFilter(request, response);
  }

  @Test
  public void testLoggedInFilterExcluded()
      throws ServletException, IOException {
    // Given
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain chain = mock(FilterChain.class);

    // When
    when(request.getRequestURI()).thenReturn("/fakeUrl.css");

    // Request
    LoggedInFilter filter = new LoggedInFilter();
    filter.doFilter(request, response, chain);

    // Assert
    verify(chain, times(1)).doFilter(request, response);
  }
}
