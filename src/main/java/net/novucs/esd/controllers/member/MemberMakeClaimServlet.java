package net.novucs.esd.controllers.member;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.controllers.BaseServlet;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Claim;
import net.novucs.esd.model.ClaimStatus;
import net.novucs.esd.model.Membership;
import net.novucs.esd.model.User;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Where;
import net.novucs.esd.util.DateUtil;

/**
 * The type Member make claim servlet.
 */
public class MemberMakeClaimServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;
  private static final int MAX_CLAIM_YEAR = 100;
  private static final float MAX_CLAIM_SINGLE = 75;
  private static final float CLAIM_LIMIT = 2;
  private static final String PAGE = "member.claims";
  private static final String MEMBERSHIP_STATUS = "membershipStatus";
  private static final String STATUS_CREATE = "CREATE";
  private static final String STATUS_SUCCESS = "SUCCESS";
  private static final String STATUS_FAIL = "FAIL";
  private static final String MEMBER_STATUS_NONE = "NONE";
  private static final String MEMBER_STATUS_FULL_CLAIM = "FULL_CLAIM";
  private static final String MEMBER_STATUS_FULL_WAIT = "FULL_WAIT";
  private static final String MEMBER_STATUS_FULL_USED = "FULL_USED";
  private static final String MEMBER_STATUS_SUSPENDED = "SUSPENDED";
  private static final String MEMBER_STATUS_EXPIRED = "EXPIRED";
  private static final DecimalFormat df = new DecimalFormat("#.##");
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
      allUserMemberships = getAllMemberships(session);
      if (allUserMemberships.isEmpty()) {
        // User has never had a membership
        request.setAttribute(MEMBERSHIP_STATUS, MEMBER_STATUS_NONE);
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

  private List<Membership> getAllMemberships(Session session) throws SQLException {
    return membershipDao.select()
        .where(new Where().eq("user_id", session.getUser().getId())).all();
  }

  private List<Claim> getAllClaims(Membership currentMembership) throws SQLException {
    return claimDao.select()
        .where(new Where().eq("membership_id", currentMembership.getId())).all();
  }

  private Membership getCurrentMembership(List<Membership> memberships) {
    ZonedDateTime now = ZonedDateTime.now();
    return memberships.stream()
        .filter(m -> m.getStartDate().isBefore(now) && m.getEndDate().isAfter(now))
        .findFirst().orElse(null);
  }

  private Membership getLastMembership(Session session, List<Membership> memberships)
      throws SQLException {
    Membership lastMembership = memberships.get(0);
    Membership currentMembership = getCurrentMembership(memberships);
    if (currentMembership == null) {
      for (Membership membership : memberships) {
        if (membership.getStartDate().isAfter(lastMembership.getEndDate())) {
          lastMembership = membership;
        }
      }
      Membership renewMembership = new Membership(session.getUser().getId(),
          BigDecimal.ZERO,
          "EXPIRED",
          lastMembership.getEndDate().plusDays(1),
          false
      );
      membershipDao.insert(renewMembership);
      return lastMembership;
    } else {
      return memberships.stream()
          .filter(m -> m.getStartDate().isAfter(currentMembership.getStartDate().minusMonths(13)))
          .findFirst().orElse(null);
    }
  }

  private String evaluateRemainingClaimBalance(List<Claim> claims) {
    BigDecimal maxSingleClaim = BigDecimal.valueOf(MAX_CLAIM_SINGLE);
    BigDecimal balance = BigDecimal.valueOf(MAX_CLAIM_YEAR);
    balance = balance.subtract(claims.get(0).getAmount());
    balance = balance.compareTo(maxSingleClaim) > 0 ? maxSingleClaim : balance;
    return df.format(balance);
  }

  private void membershipExpired(HttpServletRequest request,
      HttpServletResponse response, String expiredDate) throws IOException, ServletException {
    // User has membership but not active
    request.setAttribute("expiredDate", expiredDate);
    request.setAttribute(MEMBERSHIP_STATUS, MEMBER_STATUS_EXPIRED);
    super.forward(request, response, "Membership Expired", PAGE);
  }

  private void membershipSuspended(HttpServletRequest request,
      HttpServletResponse response) throws IOException, ServletException {
    request.setAttribute(MEMBERSHIP_STATUS, MEMBER_STATUS_SUSPENDED);
    super.forward(request, response, "Membership Suspended", PAGE);
  }

  private void membershipNotYetEligible(HttpServletRequest request,
      HttpServletResponse response, String claimFromDate) throws IOException, ServletException {
    request.setAttribute(MEMBERSHIP_STATUS, MEMBER_STATUS_FULL_WAIT);
    request.setAttribute("claimFrom", claimFromDate);
    super.forward(request, response, "Ineligible for new claims", PAGE);
  }

  private void membershipEligibleToMakeClaims(HttpServletRequest request,
      HttpServletResponse response, Membership currentMembership)
      throws IOException, ServletException, SQLException {

    List<Claim> allMembershipClaims = getAllClaims(currentMembership);

    if (allMembershipClaims.size() == CLAIM_LIMIT) {
      // User has no claims remaining.
      request.setAttribute(MEMBERSHIP_STATUS, MEMBER_STATUS_FULL_USED);
      super.forward(request, response, "Claim quota reached", PAGE);

    } else {
      request.getSession().setAttribute("membershipId", currentMembership.getId());
      if (allMembershipClaims.isEmpty()) {
        // User is eligible to make a claim and has the full remaining quota.
        request.setAttribute("remainingClaims", 2);
        request.setAttribute("maxClaimValue", df.format(MAX_CLAIM_SINGLE));
      } else {
        // Member has made a claim previously and balance needs to be evaluated
        request.setAttribute("remainingClaims", 1);
        request.setAttribute("maxClaimValue",
            evaluateRemainingClaimBalance(allMembershipClaims));
      }
      request.setAttribute(MEMBERSHIP_STATUS, MEMBER_STATUS_FULL_CLAIM);
      super.forward(request, response, "New Claim", PAGE);
    }
  }

  private void evaluateEligibility(Session session,
      HttpServletRequest request,
      HttpServletResponse response,
      List<Membership> allUserMemberships
  ) throws SQLException, IOException, ServletException {
    DateUtil dateUtil = new DateUtil();

    // Get last membership first. If no current membership exists a new current membership is
    // created and the expired membership returned as last membership.
    Membership lastMembership = getLastMembership(session, allUserMemberships);

    // Fetch all memberships again as this may have been updated.
    allUserMemberships = getAllMemberships(session);

    // Get currentMembership, this will correct however may need payment if previous had expired
    Membership currentMembership = getCurrentMembership(allUserMemberships);

    if (currentMembership.getBalance().compareTo(BigDecimal.ZERO) == 0) {

      // currentMembership had expired so return expired membership page
      String expiredDate = dateUtil.getFormattedDate(lastMembership.getEndDate());
      membershipExpired(request, response, expiredDate);
    } else {
      if (currentMembership.getStatus().equalsIgnoreCase(MEMBER_STATUS_SUSPENDED)) {
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
