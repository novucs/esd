package net.novucs.esd.controllers.member;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.constants.ClaimUtils;
import net.novucs.esd.controllers.BaseServlet;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Claim;
import net.novucs.esd.model.ClaimStatus;
import net.novucs.esd.model.Membership;
import net.novucs.esd.model.User;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.util.DateUtil;
import net.novucs.esd.util.MembershipUtils;

/**
 * The type Member make claim servlet.
 */
public class MemberMakeClaimServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;
  private static final String PAGE = "member.claims";
  private static final String MEMBERSHIP_STATUS = "membershipStatus";
  private static final String STATUS_CREATE = "CREATE";
  private static final String STATUS_SUCCESS = "SUCCESS";
  private static final String STATUS_FAIL = "FAIL";
  private static final MembershipUtils membershipUtils = new MembershipUtils();

  @Inject
  private Dao<Membership> membershipDao;

  @Inject
  private Dao<Claim> claimDao;

  @Inject
  private Dao<User> userDao; // NOPMD

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    Session session = Session.fromRequest(request);
    List<Membership> allUserMemberships;

    request.setAttribute("claimStatus", STATUS_CREATE);
    try {
      allUserMemberships = membershipUtils.getAllMemberships(session, membershipDao);
      if (allUserMemberships.isEmpty()) {
        // User has never had a membership
        request.setAttribute(MEMBERSHIP_STATUS, membershipUtils.STATUS_NONE);
        super.forward(request, response, "Make A Claim", PAGE);
      } else {
        // User has had a membership and may be eligible
        evaluateEligibility(session, request, response, allUserMemberships);
      }
    } catch (SQLException e) {
      Logger.getLogger(MemberMakeClaimServlet.class.getName()).log(Level.SEVERE, null, e);
      super.forward(request, response, "Database error", PAGE);
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    try {
      Claim claim = new Claim(
          (int) request.getSession().getAttribute("membershipId"),
          new BigDecimal(request.getParameter("claim-value")),
          ZonedDateTime.now(),
          ClaimStatus.PENDING);
      claimDao.insert(claim);
    } catch (SQLException e) {
      Logger.getLogger(MemberMakeClaimServlet.class.getName()).log(Level.SEVERE, null, e);
      request.setAttribute("claimStatus", STATUS_FAIL);
      super.forward(request, response, "Database error", PAGE);
    }
    request.setAttribute("claimStatus", STATUS_SUCCESS);
    super.forward(request, response, "Claim successfully submitted", PAGE);
  }

  private void membershipExpired(HttpServletRequest request,
      HttpServletResponse response, String expiredDate) throws IOException, ServletException {
    // User has membership but not active
    request.setAttribute("expiredDate", expiredDate);
    request.setAttribute(MEMBERSHIP_STATUS, membershipUtils.STATUS_EXPIRED);
    super.forward(request, response, "Membership Expired", PAGE);
  }

  private void membershipSuspended(HttpServletRequest request,
      HttpServletResponse response) throws IOException, ServletException {
    request.setAttribute(MEMBERSHIP_STATUS, membershipUtils.STATUS_SUSPENDED);
    super.forward(request, response, "Membership Suspended", PAGE);
  }

  private void membershipNotYetEligible(HttpServletRequest request,
      HttpServletResponse response, String claimFromDate) throws IOException, ServletException {
    request.setAttribute(MEMBERSHIP_STATUS, membershipUtils.STATUS_FULL_WAIT);
    request.setAttribute("claimFrom", claimFromDate);
    super.forward(request, response, "Ineligible for new claims", PAGE);
  }

  private void membershipEligibleToMakeClaims(HttpServletRequest request,
      HttpServletResponse response, Membership currentMembership)
      throws IOException, ServletException, SQLException {

    List<Claim> allMembershipClaims = membershipUtils
        .getAllClaims(claimDao, currentMembership);
    request.getSession().setAttribute("membershipId", currentMembership.getId());
    request.setAttribute("maxClaimValue",
        ClaimUtils.MAX_CLAIM_YEAR);
    request.setAttribute("remainingClaims",
        (int) ClaimUtils.CLAIM_LIMIT - allMembershipClaims.size());
    request.setAttribute("membershipClaimValueToDate",
        membershipUtils.evaluateMembershipClaimsBalance(allMembershipClaims));
    request.setAttribute(MEMBERSHIP_STATUS, membershipUtils.STATUS_FULL_CLAIM);
    super.forward(request, response, "New Claim", PAGE);
  }

  private void evaluateEligibility(Session session,
      HttpServletRequest request,
      HttpServletResponse response,
      List<Membership> allUserMemberships
  ) throws SQLException, IOException, ServletException {
    DateUtil dateUtil = new DateUtil();

    // Get last membership first. If no current membership exists a new current membership is
    // created and the expired membership returned as last membership.
    Membership lastMembership = membershipUtils
        .getLastMembership(session, membershipDao, allUserMemberships);

    // If membership was just created it will be expired
    Membership currentMembership = membershipUtils.getCurrentMembership(session, membershipDao);

    if (membershipUtils.STATUS_EXPIRED.equalsIgnoreCase(currentMembership.getStatus())) {
      // currentMembership had expired so return expired membership page
      String expiredDate = dateUtil.getFormattedDate(lastMembership.getEndDate());
      membershipExpired(request, response, expiredDate);
    } else {
      if (membershipUtils.STATUS_SUSPENDED.equalsIgnoreCase(currentMembership.getStatus())) {
        // Membership is suspended
        membershipSuspended(request, response);
      } else if (currentMembership.getClaimFromDate().isAfter(ZonedDateTime.now())) {
        // Membership is not yet 6 months old so is not eligible to make a claim yet
        String claimFromDate = dateUtil.getFormattedDate(currentMembership.getClaimFromDate());
        membershipNotYetEligible(request, response, claimFromDate);
      } else {
        // Member is eligible to make a claim dependant on how many claims they have already made.
        membershipEligibleToMakeClaims(request, response, currentMembership);
      }
    }
  }

  @Override
  public String getServletInfo() {
    return "MemberMakeClaimServlet";
  }
}
