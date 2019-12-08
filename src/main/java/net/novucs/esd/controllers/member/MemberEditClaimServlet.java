package net.novucs.esd.controllers.member;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
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
 * The type Member edit claim servlet.
 */
public class MemberEditClaimServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;

  private static final Integer MAX_CLAIM_VALUE_POUNDS = 100;

  @Inject
  private Dao<Claim> claimDao;

  @Inject
  private Dao<Membership> membershipDao;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    Session session = Session.fromRequest(request);
    User user = session.getUser();
    int claimId = Integer.parseInt(request.getParameter("claimId"));

    try {
      Claim claim = claimDao.select()
          .where(new Where().eq("id", claimId))
          .first();

      Membership membership = membershipDao.select()
          .where(new Where().eq("id", claim.getMembershipId()))
          .first();

      if (membership == null || !membership.getUserId().equals(user.getId())) {
        // Ensures the membership the claim is attached to belongs to the session user.
        request.setAttribute("error", "Membership not found");
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return;
      }

      // Valid edit request

      List<Claim> claims = getClaims(membership);

      // Calculate balance
      double total = ClaimUtil.getTotal(claims);
      Double claimValue = claim.getAmount().doubleValue();
      total -= claimValue;

      request.setAttribute("membershipClaimValueToDate", total);
      request.setAttribute("maxClaimValue", MAX_CLAIM_VALUE_POUNDS - total);

      request.setAttribute("claimValue", String.format("%.2f", claimValue));
      request.setAttribute("claimId", claim.getId());
      super.forward(request, response, "Manage Claims", "member.claim.edit");


    } catch (SQLException e) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    Session session = Session.fromRequest(request);
    User user = session.getUser();
    int claimId = Integer.parseInt(request.getParameter("claimId"));
    try {
      Claim claim = claimDao.select()
          .where(new Where().eq("id", claimId))
          .first();

      Membership membership = membershipDao.select()
          .where(new Where().eq("id", claim.getMembershipId()))
          .first();

      if (membership == null || !membership.getUserId().equals(user.getId())) {
        // Ensures the membership the claim is attached to belongs to the session user.
        request.setAttribute("error", "Membership not found");
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return;
      }
      boolean deleteClaim = Boolean.parseBoolean(request.getParameter("cancel-claim"));
      if (deleteClaim) {
        // Claim requested for cancellation
        claim.setStatus(ClaimStatus.CANCELLED);
        claimDao.update(claim);

        // Return back to manage claims page
        request.setAttribute("message", "You have successfully cancelled your claim");
        super.forward(request, response, "Manage Claims", "member.manageclaims");
        return;
      }
      // Claim value requested to be changed
      double claimValue = Double.parseDouble(request.getParameter("claim-value"));
      BigDecimal claimAmount = BigDecimal.valueOf(claimValue);
      claimAmount = claimAmount.setScale(2, BigDecimal.ROUND_UP);
      claim.setAmount(claimAmount);
      claimDao.update(claim);

      // Return to success page
      request.setAttribute("message", "You have successfully updated your claim");
      super.forward(request, response, "Manage Claims", "member.claim.success");


    } catch (SQLException e) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Gets claims.
   *
   * @param membership the membership
   * @return the claims
   * @throws SQLException the sql exception
   */
  private List<Claim> getClaims(Membership membership) throws SQLException {
    return claimDao.select()
        .where(new Where().eq("membership_id", membership.getId())
            .and().eq("status", ClaimStatus.PENDING.name())
            .or().eq("status", ClaimStatus.APPROVED.name()))
        .all();
  }

  @Override
  public String getServletInfo() {
    return "MemberEditClaimServlet";
  }
}
