package net.novucs.esd.controllers.admin;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.constants.MembershipUtils;
import net.novucs.esd.controllers.BaseServlet;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Claim;
import net.novucs.esd.model.Membership;
import net.novucs.esd.orm.Dao;

public class AdminReportingServlet extends BaseServlet {


  @Inject
  private Dao<Claim> claimDao;

  @Inject
  private Dao<Membership> membershipDao;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    Session session = Session.fromRequest(request);

    if (session.getFilter("showReport") != null) {
      request.setAttribute("showReport", true);

      LocalDate to = (LocalDate) session.getFilter("to");
      request.setAttribute("to", to.format(DateTimeFormatter.ofPattern("dd-MM-yy")));
      request.setAttribute("toFormatted",
          to.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)));

      LocalDate from = (LocalDate) session.getFilter("from");
      request.setAttribute("from", from.format(DateTimeFormatter.ofPattern("dd-MM-yy")));
      request.setAttribute("fromFormatted", ((LocalDate) session.getFilter("from"))
          .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)));

      int claimSum = (int) session.getFilter("claimSum");
      long membershipSum = (long) session.getFilter("membershipSum");

      request.setAttribute("claims", session.getFilter("claimsMade"));
      request.setAttribute("claimSum", claimSum);
      request.setAttribute("membershipSum", membershipSum);
      request.setAttribute("turnover", membershipSum - claimSum);
      session.clearFilters();
    }

    super.forward(request, response, "Reporting", "admin.reporting");
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    try {
      LocalDate from = LocalDate.parse(request.getParameter("from"),
          DateTimeFormatter.ofPattern("dd-MM-yy"));
      LocalDate to = LocalDate.parse(request.getParameter("to"),
          DateTimeFormatter.ofPattern("dd-MM-yy"));
      // Here we are going to get all of the totals and show them as a report.

      List<Claim> claimsMade = claimDao.select().all().stream().filter(
          r -> r.getClaimDate().toLocalDate().isAfter(from) &&
              r.getClaimDate().toLocalDate().isBefore(to)).collect(Collectors.toList());

      int claimSum = claimsMade.stream().map(Claim::getAmount).map(
          BigDecimal::intValue).mapToInt(Integer::intValue).sum();

      long membershipSum = membershipDao.select().all().stream().filter(
          r -> r.getStartDate().toLocalDate().isAfter(from) && r.getStatus().equals(
              MembershipUtils.STATUS_ACTIVE) && r.getStartDate().toLocalDate().isBefore(to)
      ).count() * MembershipUtils.ANNUAL_FEE;

      Session session = Session.fromRequest(request);
      session.setFilter("showReport", true);
      session.setFilter("to", to);
      session.setFilter("from", from);
      session.setFilter("claimSum", claimSum);
      session.setFilter("claims", claimsMade);
      session.setFilter("membershipSum", membershipSum);
      session.setFilter("turnover", membershipSum - claimSum);
    } catch (SQLException e) {
      e.printStackTrace();
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return;
    }

    response.sendRedirect("reporting");
  }
}