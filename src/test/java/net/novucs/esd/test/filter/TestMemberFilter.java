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
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.novucs.esd.filter.MemberFilter;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Role;
import net.novucs.esd.model.User;
import net.novucs.esd.util.DateUtil;
import net.novucs.esd.util.Password;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;

public class TestMemberFilter {

  private transient Session userSession;

  @Before
  public void initialiseTest() {
    userSession = new Session();
    userSession.setUser(new User(
        "testuser",
        "testuser@example.com",
        Password.fromPlaintext("test_pass"),
        "Line 1,Line 2,City,County,Postcode",
        new DateUtil().getDateFromString("2000-01-01"),
        "APPLICATION"
    ));
  }

  @Test
  public void testMemberFilterWithNoSession()
      throws ServletException, IOException {
    // Given
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpSession httpSession = mock(HttpSession.class);
    FilterChain chain = mock(FilterChain.class);

    // When
    when(request.getRequestURI()).thenReturn("/makepayment");
    when(httpSession.getAttribute(eq("session"))).thenReturn(null);

    // Request
    MemberFilter filter = new MemberFilter();
    filter.doFilter(request, response, chain);

    // Assert
    verify(response, times(1)).sendRedirect(anyString());
  }

  @Test
  public void testMemberFilterWithUserNullRoles()
      throws ServletException, IOException {
    // Given
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpSession httpSession = mock(HttpSession.class);
    FilterChain chain = mock(FilterChain.class);

    // Roles
    userSession.setRoles(null);

    // When
    when(request.getRequestURI()).thenReturn("/makepayment");
    when(httpSession.getAttribute(eq("session"))).thenReturn(userSession);
    when(request.getRequestDispatcher("/layout.jsp")).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);

    // Request
    MemberFilter filter = new MemberFilter();
    filter.doFilter(request, response, chain);

    // Assert
    verify(response, times(1)).sendRedirect(anyString());
  }

  @Test
  public void testMemberFilterWithUserNoRoles()
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
    when(request.getRequestURI()).thenReturn("/makepayment");
    when(httpSession.getAttribute(eq("session"))).thenReturn(userSession);
    when(request.getRequestDispatcher("/layout.jsp")).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);

    // Request
    MemberFilter filter = new MemberFilter();
    filter.doFilter(request, response, chain);

    // Assert
    verify(response, times(1)).sendRedirect(anyString());
  }

  @Test
  public void testMemberFilterWithUserCorrectRoles()
      throws ServletException, IOException {
    // Given
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpSession httpSession = mock(HttpSession.class);
    FilterChain chain = mock(FilterChain.class);

    // Roles
    List<Role> sessionRoles = Arrays.asList(new Role("Member"));
    userSession.setRoles(sessionRoles);

    // When
    when(request.getRequestURI()).thenReturn("/makepayment");
    when(httpSession.getAttribute(eq("session"))).thenReturn(userSession);
    when(request.getRequestDispatcher("/layout.jsp")).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);

    // Request
    MemberFilter filter = new MemberFilter();
    filter.doFilter(request, response, chain);

    // Assert
    verify(chain, times(1)).doFilter(request, response);
  }

  @Test
  public void testMemberFilterWithUserIncorrectRoles()
      throws ServletException, IOException {
    // Given
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpSession httpSession = mock(HttpSession.class);
    FilterChain chain = mock(FilterChain.class);

    // Roles
    List<Role> sessionRoles = Arrays.asList(new Role("INCORRECT_ROLE"));
    userSession.setRoles(sessionRoles);

    // When
    when(request.getRequestURI()).thenReturn("/makepayment");
    when(httpSession.getAttribute(eq("session"))).thenReturn(userSession);
    when(request.getRequestDispatcher("/layout.jsp")).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);

    // Request
    MemberFilter filter = new MemberFilter();
    filter.doFilter(request, response, chain);

    // Assert
    verify(response, times(1)).sendError(anyInt());
  }

  @Test
  public void testMemberFilterExcluded()
      throws ServletException, IOException {
    // Given
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain chain = mock(FilterChain.class);

    // When
    when(request.getRequestURI()).thenReturn("/fakeUrl.css");

    // Request
    MemberFilter filter = new MemberFilter();
    filter.doFilter(request, response, chain);

    // Assert
    verify(chain, times(1)).doFilter(request, response);
  }
}
