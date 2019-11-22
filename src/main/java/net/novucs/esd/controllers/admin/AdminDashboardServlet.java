package net.novucs.esd.controllers.admin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.controllers.BaseServlet;
import net.novucs.esd.model.Application;
import net.novucs.esd.model.Claim;
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
      int outstandingApplications = applicationDao.select().all().size();
      int claims = claimDao.select().all().size();
      int members = 0;

      for (User u : numberOfUsers) {
        for (UserRole r : userRoles) {
          if (r.getUserId() == u.getId() && roleId == r.getRoleId()) {
            members++;
          }
        }
      }

      request.setAttribute("outstandingMemberApplications", outstandingApplications);
      request.setAttribute("currentMembers", members);
      request.setAttribute("outstandingBalances", claims);

      // todo: Update when reporting is set in place
      request.setAttribute("monthlyClaimCost", "200");
      request.setAttribute("quarterlyClaimCost", "20000");
      super.forward(request, response, "Dashboard", "admin.dashboard");
    } catch (SQLException e) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public String getServletInfo() {
    return "AdminDashboardServlet";
  }
}
