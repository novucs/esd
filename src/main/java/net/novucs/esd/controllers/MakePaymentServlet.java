package net.novucs.esd.controllers;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Application;
import net.novucs.esd.model.ApplicationStatus;
import net.novucs.esd.model.Membership;
import net.novucs.esd.model.Payment;
import net.novucs.esd.model.User;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.util.MembershipUtil;

/**
 * The type Make payment servlet.
 */
public class MakePaymentServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;
  private static final String STRIPE_TEST_SECRET_KEY = "sk_test_kMQ6gPhFRqsyWex4O8FxMU4200Poyj5KwH";

  @Inject
  private Dao<Membership> membershipDao;

  @Inject
  private Dao<Application> applicationDao;

  @Inject
  private Dao<Payment> paymentDao;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    Session session = Session.fromRequest(request);

    try {
      User user = session.getUser();
      Application application = MembershipUtil.getApplication(user, applicationDao);

      if (application == null) {
        // All users must have an application associated with their account.
        sendError(request, response, "Your account has no related application");
        return;
      }

      switch (application.getStatus()) {
        case DENIED:
          response.sendRedirect(".");
          return;
        case OPEN:
          forwardMakePayment(request, response);
          return;
        case PAID:
          super.forward(
              request, response, "Membership Payment", "user.makepayment.notnecessary");
          return;
        case APPROVED:
          if (MembershipUtil.hasActiveMembership(user, membershipDao)) {
            response.sendRedirect("dashboard");
            return;
          }
          forwardMakePayment(request, response);
          return;
        default:
          return;
      }
    } catch (SQLException e) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  private void forwardMakePayment(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    request.setAttribute("fee", Membership.ANNUAL_FEE_POUNDS);
    super.forward(request, response, "Membership Payment", "user.makepayment.pay");
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    Session session = Session.fromRequest(request);
    User user = session.getUser();
    String token = request.getParameter("stripeToken");
    String reference = request.getParameter("reference");

    try {
      if (MembershipUtil.hasActiveMembership(user, membershipDao)) {
        // Users with an active membership cannot make a payment.
        sendError(request, response, "You already have a membership");
        return;
      }

      Application application = MembershipUtil.getApplication(user, applicationDao);
      if (application == null) {
        sendError(request, response, "Your account has no related application");
        return;
      }

      String stripeId = null;
      if (token != null) {
        Charge charge = executeStripePayment(request, response, token);
        if (charge == null) {
          return;
        }
        stripeId = charge.getId();
      }

      paymentDao.insert(new Payment(
          user.getId(),
          BigDecimal.valueOf(Membership.ANNUAL_FEE_POUNDS),
          stripeId,
          reference,
          ZonedDateTime.now(),
          stripeId == null ? "PENDING" : "VERIFIED"
      ));

      application.setStatus(ApplicationStatus.PAID);
      applicationDao.update(application);
      membershipDao.insert(new Membership(
          user.getId(),
          MembershipUtil.hasPreviousMemberships(user, membershipDao)
      ));

      DecimalFormat df = new DecimalFormat("#.##");
      request.setAttribute("charge", df.format(Membership.ANNUAL_FEE_POUNDS));
      super.forward(request, response, "Payment Success", "user.makepayment.success");

    } catch (SQLException e) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  public void sendError(HttpServletRequest request, HttpServletResponse response, String message)
      throws IOException {
    request.setAttribute("error", message);
    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
  }

  private Charge executeStripePayment(
      HttpServletRequest request, HttpServletResponse response, String token)
      throws IOException, ServletException {
    Stripe.apiKey = STRIPE_TEST_SECRET_KEY;
    Map<String, Object> params = new HashMap<>();
    params.put("amount", Membership.ANNUAL_FEE_POUNDS * 100);
    params.put("currency", "gbp");
    params.put("description", "Example charge");
    params.put("source", token);

    try {
      return Charge.create(params);
    } catch (StripeException e) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Failed to process payment", e);
      super.forward(request, response, "Payment Error", "user.makepayment.fail");
      return null;
    }
  }

  @Override
  public String getServletInfo() {
    return "MakePaymentServlet";
  }
}
