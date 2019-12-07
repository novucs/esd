package net.novucs.esd.test.controller.admin;

import static junit.framework.TestCase.assertTrue;
import static net.novucs.esd.test.util.TestUtil.createTestDaoManager;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
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
import net.novucs.esd.controllers.admin.AdminManageUsersServlet;
import net.novucs.esd.lifecycle.DatabaseLifecycle;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.User;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.DaoManager;
import net.novucs.esd.test.util.TestDummyDataUtil;
import net.novucs.esd.util.ReflectUtil;
import org.junit.Before;
import org.junit.Test;
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

  private final AdminManageUsersServlet servlet = new AdminManageUsersServlet();
  private final HttpServletResponse response = mock(HttpServletResponse.class);
  private final HttpSession httpSession = mock(HttpSession.class);
  private final HttpServletRequest request = mock(HttpServletRequest.class);


  /**
   * Initialise test user.
   */

  @Before
  public void initialiseTest() {
    userSession = mock(Session.class);
    userSession.setUser(TestDummyDataUtil.getDummyUser());
  }

  /**
   * Test edited user.
   *
   * @throws SQLException                 the sql exception
   * @throws ReflectiveOperationException the reflective operation exception
   * @throws ServletException             the servlet exception
   * @throws IOException                  the io exception
   */
  @Test
  public void testRequestGetsMapAttribute()
      throws ServletException, IOException, ReflectiveOperationException, SQLException {
    // Given
    DaoManager dm = createTestDaoManager();
    dm.init(DatabaseLifecycle.MODEL_CLASSES);
    Dao<User> userDao = dm.get(User.class);
    ReflectUtil.setFieldValue(servlet, USER_DAO, userDao);

    // When
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    esdWhen();
    servlet.doGet(request, response);

    // Assert
    verify(request).getRequestDispatcher(eq(LAYOUT));
  }

  /**
   * Test the pagination.
   *
   * @throws SQLException                 the sql exception
   * @throws ReflectiveOperationException the reflective operation exception
   * @throws ServletException             the servlet exception
   * @throws IOException                  the io exception
   */
  @Test
  public void testPagination()
      throws SQLException, ReflectiveOperationException, IOException, ServletException {
    // Given
    DaoManager dm = createTestDaoManager();
    dm.init(DatabaseLifecycle.MODEL_CLASSES);
    Dao<User> userDao = dm.get(User.class);
    userDao.insert(TestDummyDataUtil.getDummyUser());

    // When
    esdWhen();
    ReflectUtil.setFieldValue(servlet, USER_DAO, userDao);
    int pageSize = 15;
    when(userSession.getFilter(PAGE_SIZE_FILTER)).thenReturn(pageSize);
    when(userSession.getFilter(USER_SEARCH_QUERY)).thenReturn(null);
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    servlet.doGet(request, response);

    // Assert
    verify(request).setAttribute(eq("users"), anyListOf(User.class));
    verify(request).setAttribute(eq("maxPages"), anyInt());
    verify(request).setAttribute(eq("pn"), anyDouble());
    verify(request).setAttribute("ps", pageSize);
    verify(request).getRequestDispatcher(eq("/layout.jsp"));
  }

  /**
   * Test the search query.
   *
   * @throws SQLException                 the sql exception
   * @throws ReflectiveOperationException the reflective operation exception
   * @throws ServletException             the servlet exception
   * @throws IOException                  the io exception
   */
  @Test
  public void testSearchQuery()
      throws SQLException, ReflectiveOperationException, IOException, ServletException {
    // Given
    int pageSize = 15;
    DaoManager dm = createTestDaoManager();
    dm.init(DatabaseLifecycle.MODEL_CLASSES);
    Dao<User> userDao = dm.get(User.class);
    this.addUserData(userDao);
    ReflectUtil.setFieldValue(servlet, USER_DAO, userDao);

    //When
    esdWhen();
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(userSession.getFilter(PAGE_SIZE_FILTER)).thenReturn(pageSize);
    when(userSession.getFilter(USER_SEARCH_QUERY)).thenReturn("admin");
    servlet.doGet(request, response);

    // Assert
    verify(request).setAttribute(eq("users"), anyListOf(User.class));
    verify(request).setAttribute(eq("maxPages"), anyInt());
    verify(request).setAttribute(eq("pn"), anyDouble());
    verify(request).setAttribute("ps", pageSize);
    verify(request).getRequestDispatcher(eq(LAYOUT));
  }


  /**
   * Test the set search query.
   *
   * @throws SQLException                 the sql exception
   * @throws ReflectiveOperationException the reflective operation exception
   * @throws ServletException             the servlet exception
   * @throws IOException                  the io exception
   */
  @Test
  public void testSetSearchQuery()
      throws SQLException, ReflectiveOperationException, IOException, ServletException {
    // Given
    DaoManager dm = createTestDaoManager();
    dm.init(DatabaseLifecycle.MODEL_CLASSES);
    Dao<User> userDao = dm.get(User.class);
    ReflectUtil.setFieldValue(servlet, USER_DAO, userDao);
    String queryTest = "queryTest";


    // When
    esdWhen();
    when(request.getParameter("search-users-query")).thenReturn(queryTest);
    servlet.doPost(request, response);

    // Assert
    verify(userSession).setFilter(USER_SEARCH_QUERY, queryTest);
  }

  /**
   * Test the set search query.
   *
   * @throws SQLException                 the sql exception
   * @throws ReflectiveOperationException the reflective operation exception
   * @throws ServletException             the servlet exception
   * @throws IOException                  the io exception
   */
  @Test
  public void testSetPageFilter()
      throws SQLException, ReflectiveOperationException, IOException, ServletException {
    // Given
    DaoManager dm = createTestDaoManager();
    dm.init(DatabaseLifecycle.MODEL_CLASSES);
    Dao<User> userDao = dm.get(User.class);
    ReflectUtil.setFieldValue(servlet, USER_DAO, userDao);
    String pageSize = "30";

    // When
    esdWhen();
    when(request.getParameter("page-size")).thenReturn(pageSize);
    when(request.getParameter(PAGE_SIZE_FILTER)).thenReturn(pageSize);
    servlet.doPost(request, response);

    // Assert
    verify(userSession).setFilter(PAGE_SIZE_FILTER, Integer.parseInt(pageSize));
  }

  /**
   * Test the servlets information.
   */
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

  /**
   * Getting test dummy users from the databases.
   *
   * @throws SQLException      the sql exception
   */

  private void addUserData(Dao<User> userDao) throws SQLException {
    userDao.insert(TestDummyDataUtil.getDummyBobUser());
    userDao.insert(TestDummyDataUtil.getDummyAdminUser());
  }

  private void esdWhen() {
    when(request.getSession(anyBoolean())).thenReturn(httpSession);
    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);

  }
}
