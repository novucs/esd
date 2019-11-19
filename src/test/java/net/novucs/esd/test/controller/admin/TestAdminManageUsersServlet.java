package net.novucs.esd.test.controller.admin;

import static junit.framework.TestCase.assertTrue;
import static net.novucs.esd.test.util.TestUtils.createTestDaoManager;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
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
import net.novucs.esd.controllers.admin.AdminManageUsersServlet;
import net.novucs.esd.lifecycle.DatabaseLifecycle;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.User;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.DaoManager;
import net.novucs.esd.test.TestDummyDataUtils;
import net.novucs.esd.util.DateUtil;
import net.novucs.esd.util.Password;
import net.novucs.esd.util.ReflectUtil;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

public class TestAdminManageUsersServlet {

  /*
   TODO: Refactor this to test for the implementation of AdminManageUsersServlet
   */

  private transient Session userSession;

  private static final String PAGE_SIZE_FILTER = "userPageSizeFilter";

  private static final String USER_SEARCH_QUERY = "userSearchQuery";

  private static final String SESSION = "session";

  private static final String LAYOUT = "/layout.jsp";

  private static final String USER_DAO = "userDao";

  @Before
  public void initialiseTest() {
    userSession = mock(Session.class);
    userSession.setUser(TestDummyDataUtils.getDummyUser());
  }

  @Test
  public void testRequestGetsMapAttribute()
      throws ServletException, IOException, ReflectiveOperationException, SQLException {
    // Given

    AdminManageUsersServlet servlet = new AdminManageUsersServlet();
    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpSession httpSession = mock(HttpSession.class);
    HttpServletRequest request = mock(HttpServletRequest.class);
    DaoManager dm = createTestDaoManager();
    dm.init(DatabaseLifecycle.MODEL_CLASSES);
    Dao<User> userDao = dm.get(User.class);
    ReflectUtil.setFieldValue(servlet, USER_DAO, userDao);
    // When
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);
    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);
    servlet.doGet(request, response);

    // Assert
    verify(request).getRequestDispatcher(eq(LAYOUT));
  }

  @Test
  public void testPagination()
      throws SQLException, ReflectiveOperationException, IOException, ServletException {
    AdminManageUsersServlet servlet = new AdminManageUsersServlet();
    HttpSession httpSession = mock(HttpSession.class);
    HttpServletRequest request = mock(HttpServletRequest.class);
    DaoManager dm = createTestDaoManager();
    dm.init(DatabaseLifecycle.MODEL_CLASSES);
    Dao<User> userDao = dm.get(User.class);
    userDao.insert(TestDummyDataUtils.getDummyUser());
    when(request.getSession(anyBoolean())).thenReturn(httpSession);
    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);
    ReflectUtil.setFieldValue(servlet, USER_DAO, userDao);
    int pageSize = 15;
    when(userSession.getFilter(PAGE_SIZE_FILTER)).thenReturn(pageSize);
    when(userSession.getFilter(USER_SEARCH_QUERY)).thenReturn(null);
    // When
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    HttpServletResponse response = mock(HttpServletResponse.class);
    servlet.doGet(request, response);
    verify(request).setAttribute(eq("users"), anyListOf(User.class));
    verify(request).setAttribute("maxPages", 1);
    verify(request).setAttribute("pn", 1.0);
    verify(request).setAttribute("ps", pageSize);
    verify(request).getRequestDispatcher(eq("/layout.jsp"));
  }

  @Test
  public void testSearchQuery()
      throws SQLException, ReflectiveOperationException, IOException, ServletException {

    AdminManageUsersServlet servlet = new AdminManageUsersServlet();
    HttpSession httpSession = mock(HttpSession.class);
    HttpServletRequest request = mock(HttpServletRequest.class);
    DaoManager dm = createTestDaoManager();
    dm.init(DatabaseLifecycle.MODEL_CLASSES);
    Dao<User> userDao = dm.get(User.class);
    this.addUserData(userDao);
    ReflectUtil.setFieldValue(servlet, USER_DAO, userDao);
    when(request.getSession(anyBoolean())).thenReturn(httpSession);
    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);
    int pageSize = 15;
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(userSession.getFilter(PAGE_SIZE_FILTER)).thenReturn(pageSize);
    when(userSession.getFilter(USER_SEARCH_QUERY)).thenReturn("admin");
    HttpServletResponse response = mock(HttpServletResponse.class);
    servlet.doGet(request, response);
    verify(request).setAttribute(eq("users"), anyListOf(User.class));
    verify(request).setAttribute("maxPages", 1);
    verify(request).setAttribute("pn", 1.0);
    verify(request).setAttribute("ps", pageSize);
    verify(request).getRequestDispatcher(eq(LAYOUT));
  }

  @Test
  public void testSetSearchQuery()
      throws SQLException, ReflectiveOperationException, IOException, ServletException {

    AdminManageUsersServlet servlet = new AdminManageUsersServlet();
    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpSession httpSession = mock(HttpSession.class, Mockito.RETURNS_DEFAULTS);
    HttpServletRequest request = mock(HttpServletRequest.class);
    DaoManager dm = createTestDaoManager();
    dm.init(DatabaseLifecycle.MODEL_CLASSES);
    Dao<User> userDao = dm.get(User.class);
    ReflectUtil.setFieldValue(servlet, USER_DAO, userDao);
    String queryTest = "queryTest";
    when(request.getSession(anyBoolean())).thenReturn(httpSession);
    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);
    when(request.getParameter("search-users-query")).thenReturn(queryTest);
    servlet.doPost(request, response);
    verify(userSession).setFilter(USER_SEARCH_QUERY, queryTest);
  }

  @Test
  public void testSetPageFilter()
      throws SQLException, ReflectiveOperationException, IOException, ServletException {

    AdminManageUsersServlet servlet = new AdminManageUsersServlet();
    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpSession httpSession = mock(HttpSession.class);
    HttpServletRequest request = mock(HttpServletRequest.class);

    DaoManager dm = createTestDaoManager();
    dm.init(DatabaseLifecycle.MODEL_CLASSES);

    Dao<User> userDao = dm.get(User.class);
    ReflectUtil.setFieldValue(servlet, USER_DAO, userDao);
    String pageSize = "30";
    when(request.getSession(anyBoolean())).thenReturn(httpSession);
    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);
    when(request.getParameter("page-size")).thenReturn(pageSize);
    servlet.doPost(request, response);
    when(request.getParameter(PAGE_SIZE_FILTER)).thenReturn(pageSize);
    verify(userSession).setFilter(PAGE_SIZE_FILTER, Integer.parseInt(pageSize));
  }


  @Test
  public void testServletInfo() {
    // Given
    AdminManageUsersServlet servlet = new AdminManageUsersServlet();

    // When
    String servletInfo = servlet.getServletInfo();

    // Assert
    assertTrue("getServletInfo must match the class name.",
        servletInfo.equalsIgnoreCase(servlet.getClass().getSimpleName()));
  }

  private void addUserData(Dao<User> userDao) throws SQLException {
    DateUtil dateUtil = new DateUtil();
    ZonedDateTime dateOfBirth = dateUtil.getDateFromString("2000-01-01");

    userDao.insert(new User(
        "bob",
        "bob@bob.net",
        Password.fromPlaintext("bob"),
        "bob lane",
        dateOfBirth,
        "great",
        1
    ));

    userDao.insert(new User(
        "admin",
        "admin@admin.net",
        Password.fromPlaintext("bob"),
        "admin lane",
        dateOfBirth,
        "depressed",
        1
    ));
  }
}
