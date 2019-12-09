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
import net.novucs.esd.model.Action;
import net.novucs.esd.model.Application;
import net.novucs.esd.model.ApplicationStatus;
import net.novucs.esd.model.Membership;
import net.novucs.esd.model.Payment;
import net.novucs.esd.model.User;
import net.novucs.esd.model.UserAction;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Where;
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

  @Inject
  private Dao<Action> actionDao;

  @Inject
  private Dao<UserAction> userActionDao;

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

      String actionId = request.getParameter("actionId");

      if (actionId != null) {
        // Procees action payment
        forwardMakeActionPayment(request, response, Integer.parseInt(actionId));
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

  private void forwardMakeActionPayment(HttpServletRequest request, HttpServletResponse response,
      int actionId)
      throws IOException, ServletException, SQLException {

    Action action = actionDao.selectById(actionId);
    double fee = Double.parseDouble(action.getPounds().toString()
        + "." + action.getPence().toString());
    request.setAttribute("fee", fee);
    request.setAttribute("actionId", actionId);
    super.forward(request, response, "Membership Payment", "user.makepayment.pay");
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    Session session = Session.fromRequest(request);
    User user = session.getUser();
    String token = request.getParameter("stripeToken");
    String reference = request.getParameter("reference");
    String actionId = request.getParameter("actionId");
    String offlineActionId = request.getParameter("offlineActionId");
    DecimalFormat df = new DecimalFormat("#.##");

    try {
      if (actionId == null || offlineActionId == null) {
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

        String stripeId = getStripeChargeId(token, request, response);

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

        request.setAttribute("charge", df.format(Membership.ANNUAL_FEE_POUNDS));
      } else {
        actionId = actionId == null ? offlineActionId : actionId;
        double fee = processActionPayment(actionId, token, request, response, reference, user);
        request.setAttribute("charge", df.format(fee));
      }

      super.forward(request, response, "Payment Success", "user.makepayment.success");
    } catch (SQLException e) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  private double processActionPayment(String actionId, String token,
      HttpServletRequest request, HttpServletResponse response, String reference, User user)
      throws SQLException, IOException, ServletException {

    String stripeId = getStripeChargeId(token, request, response);
    Action action = actionDao.selectById(Integer.parseInt(actionId));
    double fee = Double.parseDouble(action.getPounds().toString()
        + "." + action.getPence().toString());
    paymentDao.insert(new Payment(
        user.getId(),
        BigDecimal.valueOf(fee),
        stripeId,
        reference,
        ZonedDateTime.now(),
        stripeId == null ? "PENDING" : "VERIFIED"
    ));

    int userId = Session.fromRequest(request).getUser().getId();
    UserAction toDelete = userActionDao.select().where(
        new Where()
            .eq("user_id", userId)
            .and()
            .eq("action_id", actionId))
        .first();

    userActionDao.delete(toDelete);
    if (userActionDao.select()
        .where(new Where().eq("action_id", actionId)).count("*") == 0) {
      actionDao.delete(action);
    }

    return fee;
  }

  public String getStripeChargeId(String token,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (token != null) {
      Charge charge = executeStripePayment(request, response, token);
      if (charge == null) {
        return null;
      }
      return charge.getId();
    }
    return null;
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


