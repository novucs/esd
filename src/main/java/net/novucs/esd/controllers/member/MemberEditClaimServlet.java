package net.novucs.esd.controllers.member;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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
    Map<String, String[]> queryParams = parseQueryParams(request);
    int claimId = Integer.parseInt(queryParams.get("claimId")[0]);

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
      } else {
        // Valid edit request

        List<Claim> claims = MemberMakeClaimServlet.getClaims(membership, claimDao);
        // Only interested in value of claims which have been approved or are still pending
        claims.removeIf(claim1 -> claim1.getStatus().equals(ClaimStatus.CANCELLED));
        claims.removeIf(claim1 -> claim1.getStatus().equals(ClaimStatus.REJECTED));

        // Calculate balance
        double total = MemberMakeClaimServlet.getTotal(claims);
        Double claimValue = claim.getAmount().doubleValue();
        total -= claimValue;

        request.setAttribute("membershipClaimValueToDate", total);
        request.setAttribute("maxClaimValue", MAX_CLAIM_VALUE_POUNDS - total);

        request.setAttribute("claimValue", String.format("%.2f", claimValue));
        request.setAttribute("claimId", claim.getId());
        super.forward(request, response, "Manage Claims", "member.claim.edit");
      }

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
      } else {
        boolean deleteClaim = Boolean.parseBoolean(request.getParameter("cancel-claim"));

        if (deleteClaim) {
          // Claim requested for cancellation
          claim.setStatus(ClaimStatus.CANCELLED);
          claimDao.update(claim);

          // Return back to manage claims page
          request.setAttribute("message", "You have successfully cancelled your claim");
          super.forward(request, response, "Manage Claims", "member.manageclaims");
        } else {
          // Claim value requested to be changed
          double claimValue = Double.parseDouble(request.getParameter("claim-value"));
          BigDecimal claimAmount = BigDecimal.valueOf(claimValue);
          claimAmount = claimAmount.setScale(2, BigDecimal.ROUND_UP);
          claim.setAmount(claimAmount);
          claimDao.update(claim);

          // Return to success page
          request.setAttribute("message", "You have successfully updated your claim");
          super.forward(request, response, "Manage Claims", "member.claim.success");
        }
      }
    } catch (SQLException e) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  private Map<String, String[]> parseQueryParams(HttpServletRequest request) {
    return Collections.list(request.getParameterNames())
        .stream()
        .collect(Collectors.toMap(parameterName -> parameterName, request::getParameterValues));
  }

  @Override
  public String getServletInfo() {
    return "MemberEditClaimServlet";
  }
}
