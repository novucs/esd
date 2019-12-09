package net.novucs.esd.controllers.admin;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.controllers.BaseServlet;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Action;
import net.novucs.esd.model.Claim;
import net.novucs.esd.model.Membership;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.util.ClaimUtil;

public class AdminReportingServlet extends BaseServlet {

  private static final long serialVersionUID = 1337133713371337L;

  private static final String DATE_FORMAT = "dd-MM-yy";
  private static final String FROM_LABEL = "from";

  @Inject
  private Dao<Claim> claimDao;

  @Inject
  private Dao<Membership> membershipDao;

  @Inject
  private Dao<Action> actionDao;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    Session session = Session.fromRequest(request);

    if (session.getFilter("showReport") != null) {
      request.setAttribute("showReport", true);

      LocalDate to = (LocalDate) session.getFilter("to");
      request.setAttribute("to", to.format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
      request.setAttribute("toFormatted",
          to.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)));

      LocalDate from = (LocalDate) session.getFilter(FROM_LABEL);
      request.setAttribute(FROM_LABEL, from.format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
      request.setAttribute("fromFormatted", ((LocalDate) session.getFilter(FROM_LABEL))
          .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)));

      double claimSum = (double) session.getFilter("claimSum");
      double membershipSum = (double) session.getFilter("membershipSum");
      double actionSum = (double) session.getFilter("actionSum");

      request.setAttribute("claims", session.getFilter("claims"));
      request.setAttribute("claimSum", claimSum);
      request.setAttribute("actionSum", actionSum);
      request.setAttribute("membershipSum", membershipSum);
      request.setAttribute("turnover", membershipSum + actionSum - claimSum);
      session.clearFilters();
    }

    super.forward(request, response, "Reporting", "admin.reporting");
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    try {
      LocalDate from = LocalDate.parse(request.getParameter(FROM_LABEL),
          DateTimeFormatter.ofPattern(DATE_FORMAT));
      LocalDate to = LocalDate.parse(request.getParameter("to"),
          DateTimeFormatter.ofPattern(DATE_FORMAT));
      // Here we are going to get all of the totals and show them as a report.

      List<Claim> claimsMade = claimDao.select().all().stream().filter(
          (r) -> r.getClaimDate().toLocalDate().isAfter(from.minusDays(1))
              && r.getClaimDate().toLocalDate().isBefore(to.plusDays(1)))
          .collect(Collectors.toList());

      double claimSum = ClaimUtil.sumAllClaims(claimDao, from, to);
      double membershipSum = membershipDao.select().all().stream().filter(
          r -> r.getStartDate().toLocalDate().isAfter(from.minusDays(1))
              && r.isActive()
              && r.getStartDate().toLocalDate().isBefore(to.plusDays(1))
      ).count() * Membership.ANNUAL_FEE_POUNDS;


      List<Action> actions = actionDao.select().all();
      actions = actions.stream().filter((a) ->
          a.getDateCreated()
              .toLocalDate()
              .isBefore(to.plusDays(1))
         && a.getDateCreated()
          .toLocalDate()
          .isAfter(from.minusDays(1))).collect(Collectors.toList());

      double actionSum = 0.0;
      if (actions != null && !actions.isEmpty()) {
        for (Action loopAction: actions) {
          actionSum += loopAction.getBalance().doubleValue();
        }
      }

      double turnover = membershipSum + actionSum - claimSum;
      Session session = Session.fromRequest(request);
      session.setFilter("showReport", true);
      session.setFilter("to", to);
      session.setFilter(FROM_LABEL, from);
      session.setFilter("claimSum", claimSum);
      session.setFilter("claims", claimsMade);
      session.setFilter("actionSum", actionSum);
      session.setFilter("membershipSum", membershipSum);
      session.setFilter("turnover", turnover);
    } catch (SQLException e) {
      Logger.getLogger(getClass().getName()).log(Level.WARNING, e.getMessage(), e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return;
    }

    response.sendRedirect("reporting");
  }
}
