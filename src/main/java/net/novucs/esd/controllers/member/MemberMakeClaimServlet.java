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

/**
 * The type Member make claim servlet.
 */
public class MemberMakeClaimServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;
  private static final String TITLE = "Make A Claim";
  private static final Integer MAX_CLAIM_COUNT = 2;
  private static final Integer MAX_CLAIM_VALUE_POUNDS = 100;

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
        request.setAttribute("claimFrom", membership.getClaimFromDate());
        super.forward(request, response, TITLE, "member.claim.wait");
        return;
      }

      List<Claim> claims = getClaims(membership, claimDao);

      double total = ClaimUtil.getTotal(claims);
      request.setAttribute("membershipClaimValueToDate", String.format("%.2f", total));
      request.setAttribute("maxClaimValue", String.format("%.2f", MAX_CLAIM_VALUE_POUNDS - total));
      request.setAttribute("remainingClaims", Math.max(0, MAX_CLAIM_COUNT - claims.size()));
      super.forward(request, response, TITLE, "member.claim.create");
    } catch (SQLException e) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Gets claims.
   *
   * @param membership the membership
   * @param claimDao   the claim dao
   * @return the claims
   * @throws SQLException the sql exception
   */
  private List<Claim> getClaims(Membership membership, Dao<Claim> claimDao) throws SQLException {
    return claimDao.select()
        .where(new Where().eq("membership_id", membership.getId())
            .and().eq("status", ClaimStatus.PENDING.name())
            .or().eq("status", ClaimStatus.APPROVED.name()))
        .all();
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
      double total = ClaimUtil.getTotal(getClaims(membership, claimDao));

      if ((MAX_CLAIM_VALUE_POUNDS - total) < claimAmount.doubleValue()) {
        request.setAttribute("error", "You cannot make a claim of this amount");
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return;
      }

      claimDao.insert(new Claim(
          membership.getId(),
          claimAmount,
          ZonedDateTime.now(),
          ClaimStatus.PENDING
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
