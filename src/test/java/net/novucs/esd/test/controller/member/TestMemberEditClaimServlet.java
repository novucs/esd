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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.novucs.esd.controllers.member.MemberEditClaimServlet;
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
 * The type Test member edit claim servlet.
 */
public class TestMemberEditClaimServlet {

  private static final String LAYOUT = "/layout.jsp";
  private static final String SESSION = "session";
  private static final String CLAIM_ID = "claimId";
  private transient Session userSession;
  private Dao<Claim> claimDao;

  /**
   * Initialise test.
   */
  @Before
  public void initialiseTest() {
    userSession = new Session();
  }

  private void setServletDaos(MemberEditClaimServlet servlet,
      User user,
      boolean approvedClaim,
      boolean falseClaim,
      boolean pendingClaim,
      boolean cancelledClaim,
      double claimValue)
      throws SQLException, ReflectiveOperationException {
    DaoManager dm = createTestDaoManager(true);
    dm = createTestDaoManager(true);
    Dao<User> userDao = dm.get(User.class);
    claimDao = dm.get(Claim.class);
    Dao<Membership> membershipDao = dm.get(Membership.class);

    userDao.insert(user);
    Membership membership =
        new Membership(user.getId(), ZonedDateTime.now().minusMonths(6), false);
    membershipDao.insert(membership);

    if (approvedClaim) {
      Claim claim = new Claim(membership.getId(), BigDecimal.valueOf(claimValue),
          ZonedDateTime.now().minusDays(1), ClaimStatus.APPROVED);
      claimDao.insert(claim);
    }
    if (falseClaim) {
      Claim claim = new Claim(2, BigDecimal.valueOf(claimValue),
          ZonedDateTime.now().minusDays(1), ClaimStatus.PENDING);
      claimDao.insert(claim);
    }
    if (pendingClaim) {
      Claim claim = new Claim(membership.getId(), BigDecimal.valueOf(claimValue),
          ZonedDateTime.now().minusDays(1), ClaimStatus.PENDING);
      claimDao.insert(claim);
    }
    if (cancelledClaim) {
      Claim claim = new Claim(membership.getId(), BigDecimal.valueOf(claimValue),
          ZonedDateTime.now().minusDays(1), ClaimStatus.CANCELLED);
      claimDao.insert(claim);
    }
    ReflectUtil.setFieldValue(servlet, "claimDao", claimDao);
    ReflectUtil.setFieldValue(servlet, "membershipDao", membershipDao);
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

    MemberEditClaimServlet servlet = new MemberEditClaimServlet();
    HttpSession httpSession = mock(HttpSession.class);
    HttpServletRequest request = mock(HttpServletRequest.class);
    User user = TestDummyDataUtil.getDummyUser();

    setServletDaos(servlet,
        user,
        true,
        true,
        false,
        false,
        50.0);

    userSession.setUser(user);
    String[] claimId = {claimDao.select().first().getId().toString()};
    List<String> paramNames = new ArrayList<>();
    paramNames.add(CLAIM_ID);

    // When
    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);
    when(request.getParameterNames()).thenReturn(Collections.enumeration(paramNames));
    when(request.getParameterValues(CLAIM_ID)).thenReturn(claimId);

    HttpServletResponse response = mock(HttpServletResponse.class);
    servlet.doGet(request, response);

    // Assert
    verify(request).setAttribute("page", String.format("%s.jsp", "member.claim.edit"));
  }

  /**
   * Test request no membership.
   *
   * @throws ServletException             the servlet exception
   * @throws IOException                  the io exception
   * @throws ReflectiveOperationException the reflective operation exception
   * @throws SQLException                 the sql exception
   */
  @Test
  public void testRequestNoMembership()
      throws ServletException, IOException, ReflectiveOperationException, SQLException {

    MemberEditClaimServlet servlet = new MemberEditClaimServlet();
    HttpSession httpSession = mock(HttpSession.class);
    HttpServletRequest request = mock(HttpServletRequest.class);
    User user = TestDummyDataUtil.getDummyUser();

    setServletDaos(servlet,
        user,
        false,
        true,
        false,
        false,
        50.0);

    userSession.setUser(user);
    String[] claimId = {"1"};
    List<String> paramNames = new ArrayList<>();
    paramNames.add(CLAIM_ID);

    // When
    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);
    when(request.getParameterNames()).thenReturn(Collections.enumeration(paramNames));
    when(request.getParameterValues(CLAIM_ID)).thenReturn(claimId);
    HttpServletResponse response = mock(HttpServletResponse.class);
    servlet.doGet(request, response);

    // Assert
    verify(request).setAttribute("error", "Membership not found");

  }
}
