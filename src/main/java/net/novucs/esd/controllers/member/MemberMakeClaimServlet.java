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
import net.novucs.esd.controllers.BaseServlet;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Claim;
import net.novucs.esd.model.ClaimStatus;
import net.novucs.esd.model.Membership;
import net.novucs.esd.model.User;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Where;
import net.novucs.esd.util.ClaimUtil;
import net.novucs.esd.util.DateUtil;

/**
 * The type Member make claim servlet.
 */
public class MemberMakeClaimServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;
  private static final String TITLE = "Make A Claim";

  @Inject
  private Dao<Membership> membershipDao;

  @Inject
  private Dao<Claim> claimDao;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    Session session = Session.fromRequest(request);
    User user = session.getUser();

    try {
      List<Membership> memberships = membershipDao.select()
          .where(new Where().eq("user_id", user.getId()))
          .all();

      if (memberships.isEmpty()) {
        super.forward(request, response, TITLE, "member.claim.nomembership");
        return;
      }

      Membership membership = memberships.stream()
          .filter(m -> !m.isExpired()).findFirst().orElse(null);

      if (membership == null) {
        super.forward(request, response, TITLE, "member.claim.expired");
        return;
      }

      if (membership.isSuspended()) {
        super.forward(request, response, TITLE, "member.claim.suspended");
        return;
      }

      if (!membership.isAbleToClaim()) {

        request.setAttribute("claimFrom",
            DateUtil.getFormattedDate(membership.getClaimFromDate()));
        super.forward(request, response, TITLE, "member.claim.wait");
        return;
      }

      List<Claim> claims = ClaimUtil.getNonRejectedClaims(claimDao, membership);
      double total = ClaimUtil.getTotal(claims);
      request.setAttribute("membershipClaimValueToDate", total);
      request.setAttribute("maxClaimValue", Claim.MAX_VALUE_POUNDS);
      request.setAttribute("remainingClaims", Math.max(0, Claim.CONCURRENT_LIMIT - claims.size()));
      super.forward(request, response, TITLE, "member.claim.create");
    } catch (SQLException e) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    try {
      Session session = Session.fromRequest(request);
      User user = session.getUser();
      Membership membership = membershipDao.select()
          .where(new Where().eq("user_id", user.getId()))
          .all()
          .stream()
          .filter(Membership::isAbleToClaim)
          .findFirst()
          .orElse(null);

      if (membership == null) {
        request.setAttribute("error", "Unable to make a claim, no valid membership");
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return;
      }

      BigDecimal claimAmount = new BigDecimal(request.getParameter("claim-value"));
      if ((Claim.MAX_VALUE_POUNDS) < claimAmount.doubleValue()) {
        request.setAttribute("error", "You cannot make a claim of this amount");
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return;
      }

      claimDao.insert(new Claim(
          membership.getId(),
          claimAmount,
          ZonedDateTime.now(),
          ClaimStatus.PENDING,
          request.getParameter("claim-rationale")
      ));
      super.forward(
          request, response, "Claim successfully submitted", "member.claim.success");
    } catch (SQLException e) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public String getServletInfo() {
    return "MemberMakeClaimServlet";
  }
}
