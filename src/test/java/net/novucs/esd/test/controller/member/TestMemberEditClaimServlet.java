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
      boolean cancelledClaim)
      throws SQLException, ReflectiveOperationException {
    DaoManager dm = createTestDaoManager(true);
    dm = createTestDaoManager(true);
    Dao<User> userDao = dm.get(User.class);
    Dao<Claim> claimDao = dm.get(Claim.class);
    Dao<Membership> membershipDao = dm.get(Membership.class);

    userDao.insert(user);
    Membership membership =
        new Membership(user.getId(), ZonedDateTime.now().minusMonths(6), false);
    membershipDao.insert(membership);

    if (approvedClaim) {
      Claim claim = new Claim(membership.getId(), BigDecimal.valueOf(50.00),
          ZonedDateTime.now().minusDays(1), ClaimStatus.APPROVED);
      claimDao.insert(claim);
    }
    if (cancelledClaim) {
      Claim claim = new Claim(2, BigDecimal.valueOf(50.00),
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
        false);

    userSession.setUser(user);
    // When
    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);
    when(request.getParameter(CLAIM_ID)).thenReturn("1");

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
        true);

    userSession.setUser(user);
    // When
    when(httpSession.getAttribute(eq(SESSION))).thenReturn(userSession);
    when(request.getRequestDispatcher(LAYOUT)).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> mock(RequestDispatcher.class));
    when(request.getSession(anyBoolean())).thenReturn(httpSession);
    when(request.getParameter(CLAIM_ID)).thenReturn("1");
    HttpServletResponse response = mock(HttpServletResponse.class);
    servlet.doGet(request, response);

    // Assert
    verify(request).setAttribute("error", "Membership not found");

  }
}
