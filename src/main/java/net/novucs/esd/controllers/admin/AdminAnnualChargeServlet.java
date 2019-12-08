package net.novucs.esd.controllers.admin;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.controllers.BaseServlet;
import net.novucs.esd.model.Role;
import net.novucs.esd.model.User;
import net.novucs.esd.model.UserRole;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Where;
import net.novucs.esd.util.ClaimUtil;

public class AdminAnnualChargeServlet extends BaseServlet {

  private static final long serialVersionUID = 1337133713371338L;

  @Inject
  private Dao<UserRole> userRoleDao;

  @Inject
  private Dao<Role> roleDao;

  protected void doGet(HttpServletRequest request,
      HttpServletResponse response)
      throws ServletException, IOException {
    try{
      LocalDate from = LocalDate.now().minusYears(1);
      LocalDate to = LocalDate.now();

      int claimSum = ClaimUtil.sumAllClaims(from, to);
      Role memberRole = roleDao.select().where(new Where().eq("name", Role.MEMBER)).one();

      long numberOfMembers = userRoleDao.select()
          .where(new Where().eq("role_id", memberRole.getId()))
          .count("*");

      long maxCharge = claimSum / numberOfMembers;
      if(claimSum == 0){
        maxCharge = 10;
      }

      request.setAttribute("claimSum", claimSum);
      request.setAttribute("numberOfMembers", numberOfMembers);
      request.setAttribute("maxCharge", maxCharge);

      super.forward(request, response, "Annual Sum", "admin.annualcharge");
    } catch(SQLException e) {
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
    }
  }

  protected void doPost(HttpServletRequest request,
      HttpServletResponse response)
      throws ServletException, IOException {
      // Here we need to go through all of the members, and charge them based on how many claims
      // have been made.
  }
}
