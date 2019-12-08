package net.novucs.esd.controllers.member;

import java.io.IOException;
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
import net.novucs.esd.model.Membership;
import net.novucs.esd.model.Payment;
import net.novucs.esd.model.User;
import net.novucs.esd.model.Claim;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Where;

public class MemberDashboardServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;

  @Inject
  private Dao<Claim> claimDao;

  @Inject
  private Dao<Membership> membershipDao;

  @Inject
  private Dao<Payment> paymentDao;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    Session session = super.getSession(request);
    User user = session.getUser();

    // Get metrics
    long payments = 0L;
    long claims = 0L;
    try {
      // Get total payments
      payments = paymentDao
          .select()
          .where(new Where().eq("user_id", user.getId()))
          .count("*");

      // Get total claims
      List<Membership> memberships = membershipDao.select()
          .where(new Where().eq("user_id", user.getId())).all();
      if (memberships.size() > 0) {
        Where w = new Where();
        for (Membership m : memberships) {
          w = w.eq("membership_id", m.getId()).or();
        }
        w.getClauses().remove(w.getClauses().size() - 1);
        claims = claimDao.select().where(w).count("*");
      }
    } catch (SQLException e) {
      Logger.getLogger(this.getClass().getName()).log(Level.WARNING, null, e);
    }

    request.setAttribute("userTotalClaims", claims);
    request.setAttribute("membershipValid",
        session.getRoleNames().contains("Member") ? "You're a Member!" : "No Membership");
    request.setAttribute("userTotalPayments", payments);
    super.forward(request, response, "Dashboard", "member.dashboard");
  }

  @Override
  public String getServletInfo() {
    return "MemberDashboardServlet";
  }
}
