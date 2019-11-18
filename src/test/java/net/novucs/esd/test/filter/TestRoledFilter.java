package net.novucs.esd.test.filter;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.novucs.esd.filter.BaseFilter;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Role;
import net.novucs.esd.test.TestDummyDataUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;

public class TestRoledFilter {

  private static final String ROLE_NAME = "member";
  private static final String DUMMY_PATH = "/app/makepayment";
  private static final String SESSION_LABEL = "session";
  private static final String LAYOUT_JSP = "/layout.jsp";
  private transient Session userSession;

  @Before
  public void initialiseTest() {
    userSession = new Session();
    userSession.setUser(TestDummyDataUtils.getDummyUser());
  }

  @Test
  public void testFilterWithNoHttpSession()
      throws ServletException, IOException {
    // Given
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain chain = mock(FilterChain.class);

    // When
    when(request.getRequestURI()).thenReturn(DUMMY_PATH);
    when(request.getSession(anyBoolean())).thenReturn(null);

    // Request
    BaseFilter filter = new DummyFilter();
    filter.filterByRole(ROLE_NAME, request, response, chain);

    // Assert
    verify(response, times(1)).sendRedirect(anyString());
  }

  @Test
  public void testFilterWithNoSession()
      throws ServletException, IOException {
    // Given
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpSession httpSession = mock(HttpSession.class);
    FilterChain chain = mock(FilterChain.class);

    // When
    when(request.getRequestURI()).thenReturn(DUMMY_PATH);
    when(httpSession.getAttribute(eq(SESSION_LABEL))).thenReturn(null);

    // Request
    BaseFilter filter = new DummyFilter();
    filter.filterByRole(ROLE_NAME, request, response, chain);

    // Assert
    verify(response, times(1)).sendRedirect(anyString());
  }

  @Test
  public void testFilterWithUserNullRoles()
      throws ServletException, IOException {
    // Given
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpSession httpSession = mock(HttpSession.class);
    FilterChain chain = mock(FilterChain.class);

    // Roles
    userSession.setRoles(null);

    // When
    when(request.getRequestURI()).thenReturn(DUMMY_PATH);
    when(httpSession.getAttribute(eq(SESSION_LABEL))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT_JSP)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);

    // Request
    BaseFilter filter = new DummyFilter();
    filter.filterByRole(ROLE_NAME, request, response, chain);

    // Assert
    verify(response, times(1)).sendRedirect(anyString());
  }

  @Test
  public void testFilterWithUserNoRoles()
      throws ServletException, IOException {
    // Given
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpSession httpSession = mock(HttpSession.class);
    FilterChain chain = mock(FilterChain.class);

    // Roles
    List<Role> sessionRoles = new ArrayList<>();
    userSession.setRoles(sessionRoles);

    // When
    when(request.getRequestURI()).thenReturn(DUMMY_PATH);
    when(httpSession.getAttribute(eq(SESSION_LABEL))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT_JSP)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);

    // Request
    BaseFilter filter = new DummyFilter();
    filter.filterByRole(ROLE_NAME, request, response, chain);

    // Assert
    verify(response, times(1)).sendRedirect(anyString());
  }

  @Test
  public void testFilterWithUserCorrectRoles()
      throws ServletException, IOException {
    // Given
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpSession httpSession = mock(HttpSession.class);
    FilterChain chain = mock(FilterChain.class);

    // Roles
    List<Role> sessionRoles = Arrays.asList(new Role(ROLE_NAME));
    userSession.setRoles(sessionRoles);

    // When
    when(request.getRequestURI()).thenReturn(DUMMY_PATH);
    when(httpSession.getAttribute(eq(SESSION_LABEL))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT_JSP)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);

    // Request
    BaseFilter filter = new DummyFilter();
    filter.filterByRole(ROLE_NAME, request, response, chain);

    // Assert
    verify(chain, times(1)).doFilter(request, response);
  }

  @Test
  public void testFilterWithUserIncorrectRoles()
      throws ServletException, IOException {
    // Given
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpSession httpSession = mock(HttpSession.class);
    FilterChain chain = mock(FilterChain.class);

    // Roles
    List<Role> sessionRoles = Collections.singletonList(new Role("INCORRECT_ROLE"));
    userSession.setRoles(sessionRoles);

    // When
    when(request.getRequestURI()).thenReturn(DUMMY_PATH);
    when(httpSession.getAttribute(eq(SESSION_LABEL))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT_JSP)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);

    // Request
    BaseFilter filter = new DummyFilter();
    filter.filterByRole(ROLE_NAME, request, response, chain);

    // Assert
    verify(response, times(1)).sendError(anyInt());
  }

  @Test
  public void testFilterExcluded()
      throws ServletException, IOException {
    // Given
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain chain = mock(FilterChain.class);

    // When
    when(request.getRequestURI()).thenReturn("/fakeUrl.css");

    // Request
    BaseFilter filter = new DummyFilter();
    filter.filterByRole(ROLE_NAME, request, response, chain);

    // Assert
    verify(chain, times(1)).doFilter(request, response);
  }

  private static class DummyFilter extends BaseFilter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
    }
  }
}
