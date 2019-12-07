package net.novucs.esd.controllers.admin;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.constants.ApplicationUtils;
import net.novucs.esd.controllers.BaseServlet;
import net.novucs.esd.model.Application;
import net.novucs.esd.model.Claim;
import net.novucs.esd.model.ClaimStatus;
import net.novucs.esd.model.Role;
import net.novucs.esd.model.User;
import net.novucs.esd.model.UserRole;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Where;

public class AdminDashboardServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;

  @Inject
  private Dao<User> userDao;

  @Inject
  private Dao<UserRole> userRoleDao;

  @Inject
  private Dao<Role> roleDao;

  @Inject
  private Dao<Application> applicationDao;

  @Inject
  private Dao<Claim> claimDao;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    try {
      Role role = roleDao.select().where(new Where().eq("name", "Member")).first();
      int roleId = role.getId();
      List<User> numberOfUsers = userDao.select().all();
      List<UserRole> userRoles = userRoleDao.select().all();
      int outstandingApplications = applicationDao.select().where(new Where()
          .eq("status", ApplicationUtils.STATUS_OPEN)).all().size();
      List<Claim> claims = claimDao.select().all();
      int numberOfClaims = claims.size();
      int members = 0;

      for (User u : numberOfUsers) {
        for (UserRole r : userRoles) {
          if (r.getUserId() == u.getId() && roleId == r.getRoleId()) {
            members++;
          }
        }
      }

      LocalDate today = LocalDate.now();
      LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
      LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);
      int monthlyClaimSum = sumClaims(claims, oneMonthAgo, today);
      int quarterlyClaimSum = sumClaims(claims, threeMonthsAgo, today);

      request.setAttribute("outstandingMemberApplications", outstandingApplications);
      request.setAttribute("currentMembers", members);
      request.setAttribute("outstandingBalances", numberOfClaims);

      // todo: Update when reporting is set in place
      request.setAttribute("monthlyClaimCost", String.valueOf(monthlyClaimSum));
      request.setAttribute("quarterlyClaimCost", String.valueOf(quarterlyClaimSum));
      super.forward(request, response, "Dashboard", "admin.dashboard");
    } catch (SQLException e) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  private int sumClaims(List<Claim> claims, LocalDate from, LocalDate to) throws SQLException {
    return claims.stream().filter((r) ->
        r.getClaimDate().toLocalDate().isAfter(from.minusDays(1))
            && r.getClaimDate().toLocalDate().isBefore(to.plusDays(1))
            && r.getStatus().equals(ClaimStatus.APPROVED))
        .map(Claim::getAmount).map(BigDecimal::intValue)
        .mapToInt(Integer::intValue)
        .sum();
  }

  @Override
  public String getServletInfo() {
    return "AdminDashboardServlet";
  }
}
