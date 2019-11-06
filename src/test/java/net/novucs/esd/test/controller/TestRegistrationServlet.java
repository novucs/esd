package net.novucs.esd.test.controller;

import static net.novucs.esd.test.util.TestUtils.createTestDaoManager;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.novucs.esd.controllers.RegistrationServlet;
import net.novucs.esd.lifecycle.DatabaseLifecycle;
import net.novucs.esd.model.User;
import net.novucs.esd.model.UserLog;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.DaoManager;
import net.novucs.esd.orm.Where;
import net.novucs.esd.util.DateUtil;
import net.novucs.esd.util.Password;
import net.novucs.esd.util.ReflectUtil;
import org.junit.Test;
import org.mockito.stubbing.Answer;

public class TestRegistrationServlet {

  private static final String LAYOUT_PAGE = "/layout.jsp";
  private static final String DOB = "2000-01-01";

  @Test
  public void testRequestGetsRegistrationPage() throws ServletException, IOException {
    // Given
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getRequestDispatcher(LAYOUT_PAGE)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession()).thenReturn(mock(HttpSession.class));
    when(request.getMethod()).thenReturn("GET");

    RegistrationServlet servlet = new RegistrationServlet();
    HttpServletResponse response = mock(HttpServletResponse.class);

    // When
    servlet.doGet(request, response);

    // Assert
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
    Dao<UserLog> userLogDao = daoManager.get(UserLog.class);

    RegistrationServlet servlet = new RegistrationServlet();
    ReflectUtil.setFieldValue(servlet, "userDao", userDao);
    ReflectUtil.setFieldValue(servlet, "userLogDao", userLogDao);

    String passwordPlaintext = "password";
    Password password = Password.fromPlaintext(passwordPlaintext);
    DateUtil dateUtil = new DateUtil();
    ZonedDateTime dateOfBirth = dateUtil.getDateFromString(DOB);
    User userToCreate = new User(
        "RegistrationServlet Test User 1",
        "email@email.com",
        password,
        "House, A Street, A city, County, AB12 C34",
        dateOfBirth,
        "APPLICATION"
    );

    HttpServletRequest request = mock(HttpServletRequest.class);
    setupRequest(request, passwordPlaintext, userToCreate, DOB);
    when(request.getMethod()).thenReturn("POST");
    when(request.getSession()).thenReturn(mock(HttpSession.class));
    when(request.getRequestDispatcher(any())).thenReturn(mock(RequestDispatcher.class));
    when(request.getRemoteAddr()).thenReturn("127.0.0.1");

    HttpServletResponse response = mock(HttpServletResponse.class);

    // When
    servlet.doPost(request, response);

    // Assert
    User userThatWasCreated = userDao.select()
        .where(new Where().eq("name", "RegistrationServlet Test User 1"))
        .first();
    userToCreate.setId(userThatWasCreated.getId());
    userThatWasCreated.setPassword(password);
    verify(request).setAttribute("page", "registersuccess.jsp");
    verify(request).getRequestDispatcher(LAYOUT_PAGE);
    assertEquals("The right user is returned", userToCreate.getName(),
        userThatWasCreated.getName());
  }

  @Test
  public void testRequestPostsRegistrationPageFailsIfUserExists()
      throws ServletException, IOException, ReflectiveOperationException, SQLException {
    // Given
    DaoManager daoManager = createTestDaoManager();
    daoManager.init(DatabaseLifecycle.MODEL_CLASSES);

    String passwordPlaintext = "password";
    Password password = Password.fromPlaintext(passwordPlaintext);

    DateUtil dateUtil = new DateUtil();
    ZonedDateTime dateOfBirth = dateUtil.getDateFromString(DOB);
    User targetUser = new User(
        "RegistrationServlet Test User 2",
        "email@email.com",
        password,
        "House, A Street, A city, County, AB12 C34",
        dateOfBirth,
        "APPLICATION"
    );

    Dao<User> userDao = daoManager.get(User.class);
    userDao.insert(targetUser);

    RegistrationServlet servlet = new RegistrationServlet();
    ReflectUtil.setFieldValue(servlet, "userDao", userDao);

    HttpServletRequest request = mock(HttpServletRequest.class);
    setupRequest(request, passwordPlaintext, targetUser, DOB);
    when(request.getMethod()).thenReturn("POST");
    when(request.getSession()).thenReturn(mock(HttpSession.class));
    when(request.getRequestDispatcher(any())).thenReturn(mock(RequestDispatcher.class));

    HttpServletResponse response = mock(HttpServletResponse.class);

    // When
    servlet.doPost(request, response);

    // Assert
    verify(request).setAttribute("page", "registerfail.jsp");
    verify(request).getRequestDispatcher(LAYOUT_PAGE);
  }

  private void setupRequest(HttpServletRequest request, String password, User user,
      String dateOfBirth) {
    when(request.getParameter("full-name")).thenReturn(user.getName());
    when(request.getParameter("username")).thenReturn(user.getEmail());
    when(request.getParameter("password")).thenReturn(password);

    String[] address = user.getAddress().split(", ");
    when(request.getParameter("address-name")).thenReturn(address[0]);
    when(request.getParameter("address-street")).thenReturn(address[1]);
    when(request.getParameter("address-city")).thenReturn(address[2]);
    when(request.getParameter("address-county")).thenReturn(address[3]);
    when(request.getParameter("address-postcode")).thenReturn(address[4]);
    when(request.getParameter("dob")).thenReturn(dateOfBirth);

  }
}
