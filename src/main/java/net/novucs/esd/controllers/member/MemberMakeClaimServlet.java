package net.novucs.esd.controllers.member;

import java.io.IOException;
import java.lang.reflect.Member;
import java.sql.SQLException;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.controllers.BaseServlet;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Claim;
import net.novucs.esd.model.Membership;
import net.novucs.esd.model.Role;
import net.novucs.esd.model.User;
import net.novucs.esd.model.UserLog;
import net.novucs.esd.model.UserRole;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Where;
import net.novucs.esd.util.DateUtil;

/**
 * The type Member make claim servlet.
 */
public class MemberMakeClaimServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;
  private static final int MAX_CLAIM_YEAR = 100;
  private static final int MAX_CLAIM_SINGLE = 75;
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
    DateUtil dateUtil = new DateUtil();
    Session session = Session.fromRequest(request);
    List<Membership> allUserMemberships;


    request.setAttribute("membershipStatus", MEMBER_STATUS_FULL_CLAIM);
    request.setAttribute("remainingClaims", 2);
    request.setAttribute("maxClaimValue", MAX_CLAIM_SINGLE);
    super.forward(request, response, "New Claim",
        "member.claims");



    try {
      allUserMemberships = membershipDao.select()
          .where(new Where().eq("user_id", session.getUser().getId()))
          .all();

      // User does not have any memberships
      if (allUserMemberships.size() == 0) {
        request.setAttribute("membershipStatus", MEMBER_STATUS_NONE);
        super.forward(request, response, "Make A Claim", "member.claims");
      } else {
        Membership currentMembership = getCurrentMembership(allUserMemberships);

        // User has membership but not active
        if (currentMembership == null) {
          Membership lastMembership = getLastMembership(allUserMemberships);
          request.setAttribute("membershipStatus", MEMBER_STATUS_EXPIRED);
          request.setAttribute("expiredDate", dateUtil
              .getFormattedDate(lastMembership.getEndDate()));
          super.forward(request, response, "Membership Expired", "member.claims");

        } else {
          // Membership is suspended
          if (currentMembership.getStatus().equalsIgnoreCase(MEMBER_STATUS_SUSPENDED)) {
            request.setAttribute("membershipStatus", MEMBER_STATUS_SUSPENDED);
            super.forward(request, response, "Membership Suspended", "member.claims");
          }

          // Membership not eligible to claim yet.
          else if (currentMembership.getClaimFromDate().isAfter(ZonedDateTime.now())) {
            request.setAttribute("membershipStatus", MEMBER_STATUS_FULL_WAIT);
            request.setAttribute("claimFrom", dateUtil
                .getFormattedDate(currentMembership.getClaimFromDate()));
            super.forward(request, response,
                "Ineligible for new claims", "member.claims");
          } else {
            List<Claim> allMembershipClaims = claimDao.select()
                .where(new Where().eq("membership_id", currentMembership.getId())).all();

            // User has no claims remaining.
            if (allMembershipClaims.size() == 2) {
              request.setAttribute("membershipStatus", MEMBER_STATUS_FULL_USED);
              super.forward(request, response, "Claim quota reached",
                  "member.claims");


            }
            // User is eligible to make a claim and has the full remaining quota.
            else if (allMembershipClaims.size() == 0) {
              request.setAttribute("membershipStatus", MEMBER_STATUS_FULL_CLAIM);
              request.setAttribute("remainingClaims", 2);
              request.setAttribute("maxClaimValue", MAX_CLAIM_SINGLE);
              super.forward(request, response, "New Claim",
                  "member.claims");
            }
            // User is eligible to make a claim and max claim value needs to be calculated.
            else {
              float remainingBalance = MAX_CLAIM_YEAR;
              remainingBalance -= allMembershipClaims.get(0).getPounds();
              remainingBalance -= (float) allMembershipClaims.get(0).getPence() / 100;
              remainingBalance =
                  remainingBalance > MAX_CLAIM_SINGLE ? MAX_CLAIM_SINGLE : remainingBalance;
              request.setAttribute("membershipStatus", MEMBER_STATUS_FULL_CLAIM);
              request.setAttribute("remainingClaims", 1);
              request.setAttribute("maxClaimValue", remainingBalance);
              super.forward(request, response, "New Claim",
                  "member.claims");
            }


          }
        }

      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private Membership getCurrentMembership(List<Membership> memberships) {
    ZonedDateTime now = ZonedDateTime.now();
    for (Iterator<Membership> m = memberships.iterator(); m.hasNext(); ) {
      if (m.next().getStartDate().isBefore(now) && m.next().getEndDate().isAfter(now)) {
        return m.next();
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
