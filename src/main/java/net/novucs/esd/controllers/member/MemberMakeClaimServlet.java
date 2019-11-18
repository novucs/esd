package net.novucs.esd.controllers.member;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.Iterator;
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

  private static final String PAGE = "member.claims";
  private static final String STATUS_CREATE = "CREATE";
  private static final String STATUS_SUCCESS = "SUCCESS";
  private static final String STATUS_FAIL = "FAIL";
  private static final String MEMBER_STATUS_NONE = "NONE";
  private static final String MEMBER_STATUS_FULL_CLAIM = "FULL_CLAIM";
  private static final String MEMBER_STATUS_FULL_WAIT = "FULL_WAIT";
  private static final String MEMBER_STATUS_FULL_USED = "FULL_USED";
  private static final String MEMBER_STATUS_SUSPENDED = "SUSPENDED";
  private static final String MEMBER_STATUS_EXPIRED = "EXPIRED";

  @Inject
  private Dao<User> userDao;

  @Inject
  private Dao<Membership> membershipDao;

  @Inject
  private Dao<Claim> claimDao;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    request.setAttribute("claimStatus", STATUS_CREATE);
    DateUtil dateUtil = new DateUtil();
    Session session = Session.fromRequest(request);
    List<Membership> allUserMemberships;

    try {
      allUserMemberships = membershipDao.select()
          .where(new Where().eq("user_id", session.getUser().getId())).all();

      if (allUserMemberships.size() == 0) {
        // User has never had a membership

        request.setAttribute("membershipStatus", MEMBER_STATUS_NONE);
        super.forward(request, response, "Make A Claim", PAGE);
      } else {
        Membership currentMembership = getCurrentMembership(allUserMemberships);
        if (currentMembership == null) {
          // User has membership but not active

          Membership lastMembership = getLastMembership(allUserMemberships);
          request.setAttribute("membershipStatus", MEMBER_STATUS_EXPIRED);
          request.setAttribute("expiredDate", dateUtil
              .getFormattedDate(lastMembership.getEndDate()));
          super.forward(request, response, "Membership Expired", PAGE);

        } else {
          // Membership is suspended
          if (currentMembership.getStatus().equalsIgnoreCase(MEMBER_STATUS_SUSPENDED)) {
            request.setAttribute("membershipStatus", MEMBER_STATUS_SUSPENDED);
            super.forward(request, response, "Membership Suspended", PAGE);
          } else if (currentMembership.getClaimFromDate().isAfter(ZonedDateTime.now())) {
            // Membership not eligible to claim yet.

            request.setAttribute("membershipStatus", MEMBER_STATUS_FULL_WAIT);
            request.setAttribute("claimFrom", dateUtil
                .getFormattedDate(currentMembership.getClaimFromDate()));
            super.forward(request, response,
                "Ineligible for new claims", PAGE);
          } else {
            List<Claim> allMembershipClaims = claimDao.select()
                .where(new Where().eq("membership_id", currentMembership.getId())).all();

            // User has no claims remaining.
            if (allMembershipClaims.size() == 2) {
              request.setAttribute("membershipStatus", MEMBER_STATUS_FULL_USED);
              super.forward(request, response, "Claim quota reached",
                  PAGE);
            } else {
              // User is eligible to make a claim and has the full remaining quota.

              request.getSession().setAttribute("membershipId", currentMembership.getId());
              if (allMembershipClaims.size() == 0) {
                request.setAttribute("membershipStatus", MEMBER_STATUS_FULL_CLAIM);
                request.setAttribute("remainingClaims", 2);
                request.setAttribute("maxClaimValue", MAX_CLAIM_SINGLE);
                super.forward(request, response, "New Claim",
                    PAGE);
              } else {
                // User is eligible to make a claim and max claim value needs to be calculated.

                // Calculate how much is left from year quota.
                BigDecimal remainingBalance = BigDecimal.valueOf(MAX_CLAIM_YEAR);
                remainingBalance = remainingBalance
                    .subtract(allMembershipClaims.get(0).getAmount());

                // If remaining quota is more than maximum single claim value, use max value.
                remainingBalance =
                    remainingBalance.compareTo(BigDecimal.valueOf(MAX_CLAIM_SINGLE)) < 0
                        ? BigDecimal.valueOf(MAX_CLAIM_SINGLE) : remainingBalance;

                request.setAttribute("membershipStatus", MEMBER_STATUS_FULL_CLAIM);
                request.setAttribute("remainingClaims", 1);
                request.setAttribute("maxClaimValue", remainingBalance);
                super.forward(request, response, "New Claim", PAGE);
              }
            }
          }
        }
      }
    } catch (SQLException e) {
      Logger.getLogger(MemberMakeClaimServlet.class.getName())
          .log(Level.SEVERE, null, e);
      super.forward(request, response, "Database error", PAGE);
      e.printStackTrace();
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    try {
      Claim claim = new Claim((int) request.getSession().getAttribute("membershipId"));
      claim.setAmount(new BigDecimal(request.getParameter("claim-value")));
      claimDao.insert(claim);
    } catch (SQLException e) {
      Logger.getLogger(MemberMakeClaimServlet.class.getName())
          .log(Level.SEVERE, null, e);
      request.setAttribute("claimStatus", STATUS_FAIL);
      super.forward(request, response, "Database error", PAGE);
      e.printStackTrace();
    }
    request.setAttribute("claimStatus", STATUS_SUCCESS);
    super.forward(request, response, "Claim successfully submitted", PAGE);
  }

  private Membership getCurrentMembership(List<Membership> memberships) {
    ZonedDateTime now = ZonedDateTime.now();
    for (Membership currentMembership : memberships) {
      if (currentMembership.getStartDate().isBefore(now) && currentMembership.getEndDate()
          .isAfter(now)) {
        return currentMembership;
      }
    }
    return null;
  }

  private Membership getLastMembership(List<Membership> memberships) {
    Membership membership = memberships.get(0);
    for (Iterator<Membership> m = memberships.iterator(); m.hasNext(); ) {
      if (m.next().getEndDate().isAfter(membership.getEndDate())) {
        membership = m.next();
      }
    }
    return membership;
  }

  @Override
  public String getServletInfo() {
    return "MemberMakeClaimServlet";
  }
}
