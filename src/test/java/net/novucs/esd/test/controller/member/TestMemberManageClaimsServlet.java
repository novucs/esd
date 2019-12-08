package net.novucs.esd.test.controller.member;

import static net.novucs.esd.test.util.TestUtil.createTestDaoManager;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.novucs.esd.controllers.member.MemberManageClaimsServlet;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Claim;
import net.novucs.esd.model.ClaimStatus;
import net.novucs.esd.model.Membership;
import net.novucs.esd.model.User;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.DaoManager;
import net.novucs.esd.test.util.TestDummyDataUtil;
import net.novucs.esd.util.ReflectUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;

/**
 * The type Test member manage claims servlet.
 */
public class TestMemberManageClaimsServlet {

  private static final String LAYOUT = "/layout.jsp";
  private static final String SESSION = "session";
  private static final String RATIONALE = "RATIONALE";
  private transient Session userSession;
  private Dao<Claim> claimDao;
  private Dao<Membership> membershipDao;

  /**
   * Initialise test.
   */
  @Before
  public void initialiseTest() {
    userSession = new Session();
  }

  private void setServletDaos(MemberManageClaimsServlet servlet,
      User user,
      boolean approvedClaim,
      boolean rejectedClaim,
      boolean pendingClaim,
      boolean cancelledClaim,
      double claimValue)
      throws SQLException, ReflectiveOperationException {
    DaoManager dm = createTestDaoManager(true);
    dm = createTestDaoManager(true);
    Dao<User> userDao = dm.get(User.class);
    claimDao = dm.get(Claim.class);
    membershipDao = dm.get(Membership.class);

    userDao.insert(user);
    Membership membership =
        new Membership(user.getId(), ZonedDateTime.now().minusMonths(6), false);
    membershipDao.insert(membership);

    if (approvedClaim) {
      Claim claim = new Claim(membership.getId(), BigDecimal.valueOf(claimValue),
          ZonedDateTime.now().minusDays(1), ClaimStatus.APPROVED, RATIONALE);
      claimDao.insert(claim);
    }
    if (rejectedClaim) {
      Claim claim = new Claim(membership.getId(), BigDecimal.valueOf(claimValue),
          ZonedDateTime.now().minusDays(1), ClaimStatus.REJECTED, RATIONALE);
      claimDao.insert(claim);
    }
    if (pendingClaim) {
      Claim claim = new Claim(membership.getId(), BigDecimal.valueOf(claimValue),
          ZonedDateTime.now().minusDays(1), ClaimStatus.PENDING, RATIONALE);
      claimDao.insert(claim);
    }
    if (cancelledClaim) {
      Claim claim = new Claim(membership.getId(), BigDecimal.valueOf(claimValue),
          ZonedDateTime.now().minusDays(1), ClaimStatus.CANCELLED, RATIONALE);
      claimDao.insert(claim);
    }
    ReflectUtil.setFieldValue(servlet, "claimDao", claimDao);
  }

  /**
   * Test request returns correct page.
   *
   * @throws ServletException             the servlet exception
   * @throws IOException                  the io exception
   * @throws ReflectiveOperationException the reflective operation exception
   * @throws SQLException                 the sql exception
   */
  @Test
  public void testRequestReturnsCorrectPage()
      throws ServletException, IOException, ReflectiveOperationException, SQLException {

    MemberManageClaimsServlet servlet = new MemberManageClaimsServlet();
    HttpSession httpSession = mock(HttpSession.class);
    HttpServletRequest request = mock(HttpServletRequest.class);
    User user = TestDummyDataUtil.getDummyUser();

    setServletDaos(servlet,
        user,
        true,
        false,
        false,
        false,
        50.0);

    userSession.setUser(user);

    // When
    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);

    HttpServletResponse response = mock(HttpServletResponse.class);
    servlet.doGet(request, response);

    // Assert
    verify(request).setAttribute("page", String.format("%s.jsp", "member.manageclaims"));

  }

  /**
   * Test request returns correct number of claims.
   *
   * @throws ServletException             the servlet exception
   * @throws IOException                  the io exception
   * @throws ReflectiveOperationException the reflective operation exception
   * @throws SQLException                 the sql exception
   */
  @Test
  public void testRequestReturnsCorrectNumberOfClaims()
      throws ServletException, IOException, ReflectiveOperationException, SQLException {

    MemberManageClaimsServlet servlet = new MemberManageClaimsServlet();
    HttpSession httpSession = mock(HttpSession.class);
    HttpServletRequest request = mock(HttpServletRequest.class);
    User user = TestDummyDataUtil.getDummyUser();

    setServletDaos(servlet,
        user,
        true,
        false,
        false,
        false,
        50.0);

    userSession.setUser(user);

    // When
    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);

    HttpServletResponse response = mock(HttpServletResponse.class);
    servlet.doGet(request, response);

    List<Claim> claims = claimDao.select().all();
    // Assert
    verify(request).setAttribute("claims", claims);
  }

  /**
   * Test request returns correct pagination.
   *
   * @throws ServletException             the servlet exception
   * @throws IOException                  the io exception
   * @throws ReflectiveOperationException the reflective operation exception
   * @throws SQLException                 the sql exception
   */
  @Test
  public void testRequestReturnsCorrectPagination()
      throws ServletException, IOException, ReflectiveOperationException, SQLException {

    MemberManageClaimsServlet servlet = new MemberManageClaimsServlet();
    HttpSession httpSession = mock(HttpSession.class);
    HttpServletRequest request = mock(HttpServletRequest.class);
    User user = TestDummyDataUtil.getDummyUser();

    setServletDaos(servlet,
        user,
        true,
        false,
        false,
        false,
        50.0);

    userSession.setUser(user);

    // When
    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);

    HttpServletResponse response = mock(HttpServletResponse.class);

    Integer membershipId = membershipDao.select().first().getId();

    for (int i = 0; i < 20; i++) {
      claimDao.insert(new Claim(
          membershipId,
          BigDecimal.TEN,
          ZonedDateTime.now().minusDays(i),
          ClaimStatus.PENDING,
          RATIONALE
      ));
    }
    ReflectUtil.setFieldValue(servlet, "claimDao", claimDao);

    servlet.doGet(request, response);

    // Assert
    verify(request).setAttribute("claims", claimDao.select().all().subList(0, 15));
  }
}