package net.novucs.esd.controllers.admin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.controllers.BaseServlet;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Payment;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.util.PaginationUtil;

public class AdminManagePaymentsServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;

  private static final String PAGE_SIZE_FILTER = "userPageSizeFilter";

  private static final String PAYMENT_SEARCH_QUERY = "userSearchQuery";

  @Inject
  private Dao<Payment> paymentDao;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    try {
      Session session = Session.fromRequest(request);
      String searchQuery = (String) session.getFilter(PAYMENT_SEARCH_QUERY);
      int pageSize = PaginationUtil.getPageSize(request, PAGE_SIZE_FILTER);
      double pageNumber = PaginationUtil.getPageNumber(request);

      List<Payment> payments;
      if (searchQuery == null) {
        payments = PaginationUtil
            .paginate(paymentDao, pageSize, pageNumber);
      } else {
        String[] columns = {"reference", "approval_status"};
        payments = PaginationUtil.paginateWithSearch(paymentDao, pageSize, pageNumber,
            searchQuery, columns);
      }

      int max = PaginationUtil.getMaxPages(paymentDao, pageSize);
      PaginationUtil.setRequestAttributes(request, max, pageNumber, pageSize);
      request.setAttribute("payments", payments);
      session.removeFilter(PAYMENT_SEARCH_QUERY);

      super.forward(request, response, "Manage Payments", "admin.payments.manage");
    } catch (SQLException e) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    // Here we will set the filters in the users session.
    String searchQuery = request.getParameter("search-payment-query");
    PaginationUtil.postPaginationWithSearch(request, PAGE_SIZE_FILTER, PAYMENT_SEARCH_QUERY,
        searchQuery);

    response.sendRedirect("payments");
  }

  @Override
  public String getServletInfo() {
    return "ManagePaymentsServlet";
  }
}
