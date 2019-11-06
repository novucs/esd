package net.novucs.esd.test.controller;

import static net.novucs.esd.test.util.TestUtils.createTestDaoManager;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.novucs.esd.controllers.RegistrationServlet;
import net.novucs.esd.lifecycle.DatabaseLifecycle;
import net.novucs.esd.model.User;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.DaoManager;
import net.novucs.esd.orm.Where;
import net.novucs.esd.util.Password;
import net.novucs.esd.util.ReflectUtil;
import org.junit.Test;
import org.mockito.stubbing.Answer;

public class TestRegistrationServlet {

  private static final String LAYOUT_PAGE = "/layout.jsp";

  @Test
  public void testRequestGetsRegistrationPage() throws ServletException, IOException {
    // Given
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);
    when(request.getRequestDispatcher(LAYOUT_PAGE)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession()).thenReturn(session);
    when(request.getMethod()).thenReturn("GET");

    RegistrationServlet servlet = new RegistrationServlet();
    HttpServletResponse response = mock(HttpServletResponse.class);
    servlet.doGet(request, response);
    verify(request).setAttribute("page", "register.jsp");
    verify(request).getRequestDispatcher(LAYOUT_PAGE);
  }

  @Test
  public void testRequestPostsRegistrationPageSuccess()
      throws ServletException, IOException, ReflectiveOperationException, SQLException {
    // Given
    DaoManager daoManager = createTestDaoManager();
    daoManager.init(DatabaseLifecycle.MODEL_CLASSES);
    Dao<User> userDao = daoManager.get(User.class);

    RegistrationServlet servlet = new RegistrationServlet();
    ReflectUtil.setFieldValue(servlet, "userDao", userDao);
    HttpServletRequest request = mock(HttpServletRequest.class);

    HttpSession session = mock(HttpSession.class);
    when(request.getSession()).thenReturn(session);
    when(request.getMethod()).thenReturn("POST");
    when(request.getSession()).thenReturn(session);

    String passwordPlaintext = "password";
    Password password = Password.fromPlaintext(passwordPlaintext);
    User userToCreate = new User(
        "RegistrationServlet Test User 1",
        "email@email.com",
        password,
        "House,A Street,A city,County,AB12 C34",
        "APPLICATION"
    );

    when(request.getParameter("full-name")).thenReturn(userToCreate.getName());
    when(request.getParameter("username")).thenReturn(userToCreate.getEmail());
    when(request.getParameter("password")).thenReturn(passwordPlaintext);
    when(request.getParameter("address-name")).thenReturn("House");
    when(request.getParameter("address-street")).thenReturn("A Street");
    when(request.getParameter("address-city")).thenReturn("A city");
    when(request.getParameter("address-county")).thenReturn("County");
    when(request.getParameter("address-postcode")).thenReturn("AB12 C34");
    when(request.getRequestDispatcher(any())).thenReturn(mock(RequestDispatcher.class));

    HttpServletResponse response = mock(HttpServletResponse.class);
    servlet.doPost(request, response);

    User userThatWasCreated = userDao.select()
        .where(new Where().eq("name", "RegistrationServlet Test User 1"))
        .first();
    userToCreate.setId(userThatWasCreated.getId());
    userThatWasCreated.setPassword(password);
    verify(request).setAttribute("page", "registersuccess.jsp");
    verify(request).getRequestDispatcher(LAYOUT_PAGE);
    assertEquals("The right user is returned", userToCreate.getId(), userThatWasCreated.getId());
  }

  @Test
  public void testRequestPostsRegistrationPageFail()
      throws ServletException, IOException, ReflectiveOperationException, SQLException {
    // Given
    DaoManager daoManager = createTestDaoManager();
    daoManager.init(DatabaseLifecycle.MODEL_CLASSES);
    String password = "pass123";
    User targetUser = new User(
        "Name",
        "email@email.com",
        Password.fromPlaintext(password),
        "House, A Street, A city, County, AB12 C34",
        "APPLICATION"
    );
    Dao<User> userDao = daoManager.get(User.class);
    userDao.insert(targetUser);

    RegistrationServlet servlet = new RegistrationServlet();
    ReflectUtil.setFieldValue(servlet, "userDao", userDao);
    HttpServletRequest request = mock(HttpServletRequest.class);

    HttpSession session = mock(HttpSession.class);
    when(request.getSession()).thenReturn(session);
    when(request.getMethod()).thenReturn("POST");
    when(request.getSession()).thenReturn(session);

    when(request.getParameter("full-name")).thenReturn(targetUser.getName());
    when(request.getParameter("username")).thenReturn(targetUser.getEmail());
    when(request.getParameter("password")).thenReturn(password);
    when(request.getParameter("address-name")).thenReturn("House");
    when(request.getParameter("address-street")).thenReturn("A Street");
    when(request.getParameter("address-city")).thenReturn("A city");
    when(request.getParameter("address-county")).thenReturn("County");
    when(request.getParameter("address-postcode")).thenReturn("AB12 C34");
    when(request.getRequestDispatcher(any())).thenReturn(mock(RequestDispatcher.class));

    HttpServletResponse response = mock(HttpServletResponse.class);
    servlet.doPost(request, response);

    verify(request).setAttribute("page", "registerfail.jsp");
    verify(request).getRequestDispatcher(LAYOUT_PAGE);
  }
}
