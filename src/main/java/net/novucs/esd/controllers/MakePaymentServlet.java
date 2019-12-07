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
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.constants.ApplicationUtils;
import net.novucs.esd.constants.MembershipUtils;
import net.novucs.esd.constants.StripeUtils;
import net.novucs.esd.controllers.member.MemberMakeClaimServlet;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Application;
import net.novucs.esd.model.Membership;
import net.novucs.esd.model.Payment;
import net.novucs.esd.model.User;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Where;

/**
 * The type Make payment servlet.
 */
public class MakePaymentServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;
  public static final String USER_ID = "user_id";
  public static final String PAGE = "user.payments";
  public static final String PAY_CONTEXT = "payContext";
  public static final String PAY_SUCCESS = "PAY_SUCCESS";
  public static final String PAY_FAIL = "PAY_FAIL";
  public static final String PAY_APPLICATION = "PAY_APPLICATION";
  public static final String PAY_MEMBERSHIP = "PAY_MEMBERSHIP";

  @Inject
  private Dao<Membership> membershipDao;

  @Inject
  private Dao<Application> applicationDao;

  @Inject
  private Dao<Payment> paymentDao;

  @Inject
  private Dao<User> userDao; // NOPMD

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    DecimalFormat df = new DecimalFormat("#.##");
    Session session = Session.fromRequest(request);
    Application application;

    try {
      application = applicationDao.select().where(new Where().eq(USER_ID,
          session.getUser().getId())).first();

      // Check user's application first
      if (application != null
          && ApplicationUtils.STATUS_OPEN.equalsIgnoreCase(application.getStatus())) {

        // If application is OPEN, then user is not yet a member.
        request.setAttribute(PAY_CONTEXT, PAY_APPLICATION);

        // The amount they owe is the membership fee minus what they've paid so far.
        request.setAttribute("amountOwed",
            df.format(BigDecimal.valueOf(MembershipUtils.ANNUAL_FEE)
                .subtract(application.getBalance())));

        super.forward(request, response, "Application Payment", PAGE);
      } else {
        // User application is closed so they are a member
        Membership currentMembership = getCurrentMembership(session);

        // Set context to membership repayment
        request.setAttribute(PAY_CONTEXT, PAY_MEMBERSHIP);

        // The amount they owe is the membership fee minus what they've paid so far.
        assert currentMembership != null;
        request.setAttribute("amountOwed",
            df.format(BigDecimal.valueOf(MembershipUtils.ANNUAL_FEE)
                .subtract(currentMembership.getBalance())));
        super.forward(request, response, "Membership Payments", PAGE);
      }

    } catch (SQLException e) {
      Logger.getLogger(MemberMakeClaimServlet.class.getName()).log(Level.SEVERE, e.getMessage(), e);
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    // Get Stripe token with card details and the amount to charge.
    String token = request.getParameter("stripeToken");
    String amount = request.getParameter("amount");

    Session session = Session.fromRequest(request);
    String reference = request.getParameter("reference");
    float balance = Float.parseFloat(amount) / 100f;
    Application application;

    Stripe.apiKey = StripeUtils.TEST_SECRET_KEY;
    // Build params for Stripe charge request
    Map<String, Object> params = new HashMap<>();
    params.put("amount", amount);
    params.put("currency", "gbp");
    params.put("description", "Example charge");
    params.put("source", token);
    // and create charge.
    Charge charge = null;
    try {
      charge = Charge.create(params);
    } catch (StripeException e) {
      Logger.getLogger(MemberMakeClaimServlet.class.getName()).log(Level.SEVERE, e.getMessage(), e);
      request.setAttribute(PAY_CONTEXT, PAY_FAIL);
      super.forward(request, response, "Error processing payment", PAGE);
    }

    try {
      // Store a record of the transaction as a new Payment.
      assert charge != null;
      Payment payment = new Payment(session.getUser().getId(),
          BigDecimal.valueOf(balance),
          charge.getId(), reference);
      paymentDao.insert(payment);

      if (PAY_APPLICATION.equalsIgnoreCase(reference)) {
        // Payment was for an application

        // Get the application and update the balance to reflect the payment.
        application = applicationDao.select()
            .where(new Where().eq(USER_ID,
                session.getUser().getId())).first();

        application.setBalance(BigDecimal.valueOf(balance));
        applicationDao.update(application);

        // Return confirmation that the payment was successful
        request.setAttribute("charge", Long.parseLong(amount) / 100);
        request.setAttribute(PAY_CONTEXT, PAY_SUCCESS);
        super.forward(request, response, "Payment successfully received", PAGE);
      } else if (PAY_MEMBERSHIP.equalsIgnoreCase(reference)) {
        // Payment was for a/multiple membership(s)

        Membership currentMembership = getCurrentMembership(session);
        currentMembership.setBalance(BigDecimal.valueOf(balance));
        membershipDao.update(currentMembership);

        // Finally return confirmation of the payment along with the amount charged.
        request.setAttribute("charge", Long.parseLong(amount) / 100);
        request.setAttribute(PAY_CONTEXT, PAY_SUCCESS);
        super.forward(request, response, "Payment successfully received", PAGE);
      }

    } catch (SQLException e) {
      Logger.getLogger(MemberMakeClaimServlet.class.getName()).log(Level.SEVERE, e.getMessage(), e);
    }

  }

  private Membership getCurrentMembership(Session session)
      throws SQLException {
    ZonedDateTime now = ZonedDateTime.now();
    List<Membership> memberships = membershipDao.select()
        .where(new Where().eq("user_id", session.getUser().getId())).all();
    return memberships.stream()
        .filter(m -> m.getStartDate().isBefore(now) && m.getEndDate().isAfter(now))
        .findFirst().orElse(null);
  }

  @Override
  public String getServletInfo() {
    return "MakePaymentServlet";
  }
}
