package net.novucs.esd.controllers;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
  private static final int MEMBERSHIP_FEE = 10;
  private static final String STRIPE_KEY = "sk_test_kMQ6gPhFRqsyWex4O8FxMU4200Poyj5KwH";
  private static final String USER_ID = "user_id";
  private static final String PAGE = "user.payments";
  private static final String PAY_CONTEXT = "payContext";

  private static final String APPLICATION_STATUS_OPEN = "OPEN";
  private static final String PAY_SUCCESS = "PAY_SUCCESS";
  private static final String PAY_FAIL = "PAY_FAIL";
  private static final String PAY_APPLICATION = "PAY_APPLICATION";
  private static final String PAY_MEMBERSHIP = "PAY_MEMBERSHIP";

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
    List<Membership> allUserMemberships;

    try {
      application = applicationDao.select().where(new Where().eq(USER_ID,
          session.getUser().getId())).first();

      // Check user's application first
      if (APPLICATION_STATUS_OPEN.equalsIgnoreCase(application.getStatus())) {
        // If application is OPEN, then user is not yet a member.
        request.setAttribute(PAY_CONTEXT, PAY_APPLICATION);

        // The amount they owe is the membership fee minus what they've paid so far.
        request.setAttribute("amountOwed", df.format(BigDecimal.valueOf(MEMBERSHIP_FEE)
            .subtract(application.getBalance())));
        super.forward(request, response, "Application Payment", PAGE);
      } else {
        // User application is closed so they are a member

        // Get all memberships and evaluate how much is outstanding in total.
        allUserMemberships = membershipDao.select()
            .where(new Where().eq(USER_ID, session.getUser().getId())).all();
        float balance = 0f;
        for (Membership membership : allUserMemberships
        ) {
          balance += Float.parseFloat(membership.getBalance().toString()) - MEMBERSHIP_FEE;
        }

        // If anything is outstanding, balance will be a negative number so inverse to positive.
        balance *= -1;

        // If balance was 0 it will now be -0. So this brilliant line of code sorts that out.
        balance += 0;
        request.setAttribute("amountOwed", df.format(balance));
        request.setAttribute("memberships", allUserMemberships);
        request.setAttribute(PAY_CONTEXT, PAY_MEMBERSHIP);
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
    List<Membership> allUserMemberships;

    Stripe.apiKey = STRIPE_KEY;
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

        // Get all the user's membership and loop through them, updating the balance where required.
        allUserMemberships = membershipDao.select()
            .where(new Where().eq(USER_ID, session.getUser().getId())).all();
        for (Membership membership : allUserMemberships
        ) {
          if (Float.parseFloat(membership.getBalance().toString()) == 0) {
            membership.setBalance(BigDecimal.valueOf(MEMBERSHIP_FEE));
            membershipDao.update(membership);
            balance -= MEMBERSHIP_FEE;
            if (balance == 0) {
              break;
            }
          }
        }

        // Finally return confirmation of the payment along with the amount charged.
        request.setAttribute("charge", Long.parseLong(amount) / 100);
        request.setAttribute(PAY_CONTEXT, PAY_SUCCESS);
        super.forward(request, response, "Payment successfully received", PAGE);
      }

    } catch (SQLException e) {
      Logger.getLogger(MemberMakeClaimServlet.class.getName()).log(Level.SEVERE, e.getMessage(), e);
    }

  }

  @Override
  public String getServletInfo() {
    return "MakePaymentServlet";
  }
}
