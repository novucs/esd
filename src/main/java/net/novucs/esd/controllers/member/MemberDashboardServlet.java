package net.novucs.esd.controllers.member;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.controllers.BaseServlet;
import net.novucs.esd.model.Claim;
import net.novucs.esd.model.Payment;
import net.novucs.esd.model.User;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Where;

public class MemberDashboardServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;

  @Inject
  Dao<Claim> claimDao;

  @Inject
  Dao<Payment> paymentDao;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    User user = super.getSession(request).getUser();

    // Get metrics
    Long payments = null;
    Long claims = null;
    try {
      // Get total payments
      payments = paymentDao
          .select()
          .where(new Where().eq("user_id", user.getId()))
          .count("*");

      // Get total claims
      claims = 0L;
    } catch (SQLException e) {
      Logger.getLogger(this.getClass().getName()).log(Level.WARNING, null, e);
    }

    request.setAttribute("userOutstandingClaims", claims != null ? claims : 0);
    request.setAttribute("userAccountStatus", user.getStatus());
    request.setAttribute("userTotalPayments", payments != null ? payments : 0);
    super.forward(request, response, "Dashboard", "member.dashboard");
  }

  @Override
  public String getServletInfo() {
    return "MemberDashboardServlet";
  }
}
