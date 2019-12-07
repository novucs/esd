package net.novucs.esd.test.controller;

import static net.novucs.esd.test.util.TestUtil.createTestDaoManager;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
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
import net.novucs.esd.model.Application;
import net.novucs.esd.model.Role;
import net.novucs.esd.model.User;
import net.novucs.esd.model.UserLog;
import net.novucs.esd.model.UserRole;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.DaoManager;
import net.novucs.esd.orm.Where;
import net.novucs.esd.util.DateUtil;
import net.novucs.esd.util.Password;
import net.novucs.esd.util.ReflectUtil;
import org.junit.Test;
import org.mockito.stubbing.Answer;

/**
 * The type Test registration servlet.
 */
public class TestRegistrationServlet {

  private static final String LAYOUT_PAGE = "/layout.jsp";
  private static final String DOB = "2000-01-01";
  private final HttpServletRequest request = mock(HttpServletRequest.class);
  private final RegistrationServlet servlet = new RegistrationServlet();
  private final HttpServletResponse response = mock(HttpServletResponse.class);

  /**
   * Test request gets registration page.
   *
   * @throws ServletException the servlet exception
   * @throws IOException      the io exception
   */
  @Test
  public void testRequestGetsRegistrationPage() throws ServletException, IOException {
    // Given
    when(request.getRequestDispatcher(LAYOUT_PAGE)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(mock(HttpSession.class));
    when(request.getMethod()).thenReturn("GET");

    // When
    servlet.doGet(request, response);

    // Assert
    verify(request).setAttribute("page", "register.jsp");
    verify(request).getRequestDispatcher(LAYOUT_PAGE);
  }

  /**
   * Test request posts registration page success.
   *
   * @throws ServletException             the servlet exception
   * @throws IOException                  the io exception
   * @throws ReflectiveOperationException the reflective operation exception
   * @throws SQLException                 the sql exception
   */
  @Test
  public void testRequestPostsRegistrationPageSuccess()
      throws ServletException, IOException, ReflectiveOperationException, SQLException {
    // Given
    DaoManager dm = createTestDaoManager(true);
    Dao<User> userDao = dm.get(User.class);
    Dao<UserLog> userLogDao = dm.get(UserLog.class);

    RegistrationServlet servlet = new RegistrationServlet();
    ReflectUtil.setFieldValue(servlet, "userDao", userDao);
    ReflectUtil.setFieldValue(servlet, "userLogDao", userLogDao);
    ReflectUtil.setFieldValue(servlet, "userRoleDao", dm.get(UserRole.class));
    ReflectUtil.setFieldValue(servlet, "roleDao", dm.get(Role.class));
    ReflectUtil.setFieldValue(servlet, "applicationDao", dm.get(Application.class));

    String passwordPlaintext = "password";
    Password password = Password.fromPlaintext(passwordPlaintext);
    DateUtil dateUtil = new DateUtil();
    ZonedDateTime dateOfBirth = dateUtil.getDateFromString(DOB);
    User userToCreate = new User(
        "RegistrationServlet Test User 1",
        "test-user1",
        "email@email.com",
        password,
        "House, A Street, A city, County, AB12 C34",
        dateOfBirth,
        "APPLICATION",
        1
    );

    setupRequest(request, passwordPlaintext, userToCreate, DOB);

    // When
    esdWhen();
    when(request.getRemoteAddr()).thenReturn("127.0.0.1");
    servlet.doPost(request, response);

    // Assert
    User userThatWasCreated = userDao.select()
        .where(new Where().eq("name", userToCreate.getName()))
        .first();
    userToCreate.setId(userThatWasCreated.getId());
    userThatWasCreated.setPassword(password);
    verify(request).setAttribute("registerStatus", "success");
    verify(request).setAttribute("page", "register.jsp");
    verify(request).getRequestDispatcher(LAYOUT_PAGE);
    assertEquals("The right user is returned", userToCreate.getName(),
        userThatWasCreated.getName());
  }

  /**
   * Test request posts registration page fails if user exists.
   *
   * @throws ServletException             the servlet exception
   * @throws IOException                  the io exception
   * @throws ReflectiveOperationException the reflective operation exception
   * @throws SQLException                 the sql exception
   */
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
        "test-user5",
        "email@email.com",
        password,
        "House, A Street, A city, County, AB12 C34",
        dateOfBirth,
        "APPLICATION",
        1
    );

    Dao<User> userDao = daoManager.get(User.class);
    userDao.insert(targetUser);
    ReflectUtil.setFieldValue(servlet, "userDao", userDao);
    setupRequest(request, passwordPlaintext, targetUser, DOB);

    // When
    esdWhen();
    servlet.doPost(request, response);

    // Assert
    verify(request).setAttribute("page", "register.jsp");
    verify(request).setAttribute("registerStatus", "fail");
    verify(request).getRequestDispatcher(LAYOUT_PAGE);
  }

  private void setupRequest(HttpServletRequest request, String password, User user,
      String dateOfBirth) {
    when(request.getParameter("full-name")).thenReturn(user.getName());
    when(request.getParameter("email")).thenReturn(user.getEmail());
    when(request.getParameter("password")).thenReturn(password);

    String[] address = user.getAddress().split(", ");
    when(request.getParameter("address-name")).thenReturn(address[0]);
    when(request.getParameter("address-street")).thenReturn(address[1]);
    when(request.getParameter("address-city")).thenReturn(address[2]);
    when(request.getParameter("address-county")).thenReturn(address[3]);
    when(request.getParameter("address-postcode")).thenReturn(address[4]);
    when(request.getParameter("dob")).thenReturn(dateOfBirth);
  }

  private void esdWhen() {
    when(request.getMethod()).thenReturn("POST");
    when(request.getSession(anyBoolean())).thenReturn(mock(HttpSession.class));
    when(request.getRequestDispatcher(any())).thenReturn(mock(RequestDispatcher.class));
  }
}
