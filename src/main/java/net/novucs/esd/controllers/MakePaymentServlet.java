package net.novucs.esd.controllers;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
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
import net.novucs.esd.constants.StripeUtils;
import net.novucs.esd.controllers.member.MemberMakeClaimServlet;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Application;
import net.novucs.esd.model.Membership;
import net.novucs.esd.model.Payment;
import net.novucs.esd.model.User;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.util.MembershipUtils;

/**
 * The type Make payment servlet.
 */
public class MakePaymentServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;
  public static final String USER_ID = "user_id";
  public static final String PAGE = "user.payments";
  public static final String PAY_CONTEXT = "payContext";
  public static final String PAY_SUCCESS = "PAY_SUCCESS";
  public static final String PAY_RECEIVED = "PAY_RECEIVED";
  public static final String PAY_FAIL = "PAY_FAIL";
  public static final String PAY_APPLICATION = "PAY_APPLICATION";
  public static final String PAY_MEMBERSHIP = "PAY_MEMBERSHIP";
  public static final MembershipUtils membershipUtils = new MembershipUtils();
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
    Session session = Session.fromRequest(request);
    Application application;

    try {
      application = membershipUtils.getApplication(session, applicationDao);

      // Check user's application first
      if (application != null
          && ApplicationUtils.STATUS_APPROVED.equalsIgnoreCase(application.getStatus())) {

        // If application is OPEN, then user is not yet a member.
        request.setAttribute(PAY_CONTEXT, PAY_APPLICATION);
        // The amount they owe is the membership fee minus what they've paid so far.
        request.setAttribute("amountOwed", membershipUtils.getAnnualFee());
        super.forward(request, response, "Application Payment", PAGE);


      } else {
        // Get last membership checks membership status and creates a new expired membership
        List<Membership> memberships = membershipUtils.getAllMemberships(session, membershipDao);
        Membership lastMembership = membershipUtils
            .getLastMembership(session, membershipDao, memberships);

        if (lastMembership == null) {
          // If application is OPEN, then user is not yet a member.
          request.setAttribute(PAY_CONTEXT, PAY_MEMBERSHIP);
          request.setAttribute("amountOwed", membershipUtils.df.format(0));
          super.forward(request, response, "Application Payment", PAGE);
        } else {
          // Get currentMembership, this will correct however may need payment if previous had expired
          Membership currentMembership = membershipUtils
              .getCurrentMembership(session, membershipDao);

          if (membershipUtils.STATUS_EXPIRED.equalsIgnoreCase(currentMembership.getStatus())) {
            // Set context to membership repayment
            request.setAttribute(PAY_CONTEXT, PAY_MEMBERSHIP);

            // The amount they owe is the membership fee minus what they've paid so far.
            request.setAttribute("amountOwed", membershipUtils.getAnnualFee());
            super.forward(request, response, "Membership Payment", PAGE);
          } else {
            request.setAttribute(PAY_CONTEXT, PAY_MEMBERSHIP);
            request.setAttribute("amountOwed", 0);
            super.forward(request, response, "Membership Payment", PAGE);
          }
        }
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
    Session session = Session.fromRequest(request);
    String reference = request.getParameter("reference");
    Payment payment;
    try {
      if (token != null) {
        String amount = request.getParameter("amount");
        float balance = Float.parseFloat(amount) / 100f;
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
          Logger.getLogger(MemberMakeClaimServlet.class.getName())
              .log(Level.SEVERE, e.getMessage(), e);
          request.setAttribute(PAY_CONTEXT, PAY_FAIL);
          super.forward(request, response, "Error processing payment", PAGE);
        }

        payment = new Payment(session.getUser().getId(),
            BigDecimal.valueOf(balance),
            charge != null ? charge.getId() : null, reference);

        Application application = membershipUtils.getApplication(session, applicationDao);
        paymentDao.insert(payment);
        application.setStatus(ApplicationUtils.STATUS_PAID);
        applicationDao.update(application);

        Membership membership = membershipUtils.getCurrentMembership(session, membershipDao);
        if (membership == null) {
          membership = new Membership(session.getUser().getId(),
              membershipUtils.STATUS_ACTIVE,
              ZonedDateTime.now(),
              true);
          membershipDao.insert(membership);
        }

        // Return confirmation that the payment was successful
        request.setAttribute("charge", Long.parseLong(amount) / 100);
        request.setAttribute(PAY_CONTEXT, PAY_SUCCESS);
        super.forward(request, response, "Payment successfully received", PAGE);
      } else {
        payment = new Payment(session.getUser().getId(),
            BigDecimal.valueOf(membershipUtils.ANNUAL_FEE), "OFFLINE", reference);
        Application application = membershipUtils.getApplication(session, applicationDao);
        paymentDao.insert(payment);
        application.setStatus(ApplicationUtils.STATUS_PAID);
        applicationDao.update(application);

        Membership membership = membershipUtils.getCurrentMembership(session, membershipDao);
        if (membership == null) {
          membership = new Membership(session.getUser().getId(),
              membershipUtils.STATUS_ACTIVE,
              ZonedDateTime.now(),
              true);
          membershipDao.insert(membership);
        }
        // Return confirmation that the payment was received
        request.setAttribute(PAY_CONTEXT, PAY_RECEIVED);
        super.forward(request, response, "Payment successfully recorded", PAGE);
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
