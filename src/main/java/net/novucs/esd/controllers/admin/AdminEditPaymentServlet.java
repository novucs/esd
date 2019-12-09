package net.novucs.esd.controllers.admin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.controllers.BaseServlet;
import net.novucs.esd.model.Payment;
import net.novucs.esd.orm.Dao;

/**
 * The type Member edit claim servlet.
 */
public class AdminEditPaymentServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;

  @Inject
  private Dao<Payment> paymentDao;

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    int claimId = Integer.parseInt(request.getParameter("paymentId"));
    try {
      Payment payment = paymentDao.selectById(claimId);

      boolean verifyPayment = Boolean.parseBoolean(request.getParameter("verify-payment"));
      boolean declinePayment = Boolean.parseBoolean(request.getParameter("decline-payment"));
      boolean pendingPayment = Boolean.parseBoolean(request.getParameter("pending-payment"));
      if (verifyPayment) {
        payment.setApprovalStatus("VERIFIED");
      } else if (declinePayment) {
        payment.setApprovalStatus("DECLINED");
      } else if (pendingPayment) {
        payment.setApprovalStatus("PENDING");
      }
      paymentDao.update(payment);
      response.sendRedirect("payments");
    } catch (SQLException e) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public String getServletInfo() {
    return "MemberEditClaimServlet";
  }
}
