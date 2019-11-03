package net.novucs.esd.test.controller;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static net.novucs.esd.util.ReflectUtil.setFieldValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.novucs.esd.controllers.RegistrationServlet;
import net.novucs.esd.orm.Dao;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.stubbing.Answer;


public class TestRegistrationServlet {

  @Test
  public void testRequestGetsRegistrationPage()
      throws ServletException, IOException, ReflectiveOperationException {
    // Given
    RegistrationServlet registrationServlet = new RegistrationServlet();

    setFieldValue(registrationServlet, "appName", "dummyApp");

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpSession session = mock(HttpSession.class);
    when(request.getRequestDispatcher("/layout.jsp")).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession()).thenReturn(session);
    when(request.getMethod()).thenReturn("GET");

    registrationServlet.doGet(request, response);
    verify(request, times(2)).getMethod();
    verify(request).setAttribute("page", "register.jsp");
    verify(request).getRequestDispatcher("/layout.jsp");
  }

  @Test
  public void testRequestPostsRegistrationPageSuccess()
      throws ServletException, IOException, ReflectiveOperationException, SQLException {
    // Given
    RegistrationServlet registrationServlet = mock(RegistrationServlet.class);

    Dao mockUserDao = mock(Dao.class);
    when(mockUserDao.select()).thenReturn(null);
    Whitebox.setInternalState(registrationServlet, "userDao", mockUserDao);

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    HttpSession session = mock(HttpSession.class);
    when(request.getSession()).thenReturn(session);
    when(request.getMethod()).thenReturn("POST");
//    when(request.getRequestDispatcher("/layout.jsp")).thenAnswer(
//        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession()).thenReturn(session);

    when(request.getParameter("full-name")).thenReturn("Name Name");
    when(request.getParameter("username")).thenReturn("email@email.com");
    when(request.getParameter("password")).thenReturn("Password1Lol");
    when(request.getParameter("address-name")).thenReturn("House");
    when(request.getParameter("address-street")).thenReturn("A Street");
    when(request.getParameter("address-city")).thenReturn("A city");
    when(request.getParameter("address-county")).thenReturn("County");
    when(request.getParameter("address-postcode")).thenReturn("AB12 C34");

//    doAnswer((invocation) -> {
//      registrationServlet.processRequest(request, response);
//      return null;
//    }).when(registrationServlet).doPost(request, response);

    registrationServlet.doPost(request, response);
    verify(request).getParameter("full-name");
    verify(request).getParameter("username");
    verify(request).getParameter("password");
    verify(request).getParameter("address-name");
    verify(request).getParameter("address-street");
    verify(request).getParameter("address-city");
    verify(request).getParameter("address-county");
    verify(request).getParameter("address-postcode");
    verify(request).setAttribute("page", "registersuccess.jsp");
    verify(request).getRequestDispatcher("/layout.jsp");
  }
}
