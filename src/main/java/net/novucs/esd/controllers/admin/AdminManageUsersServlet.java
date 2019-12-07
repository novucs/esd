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
import net.novucs.esd.model.User;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.util.PaginationUtil;

public class AdminManageUsersServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;

  private static final String PAGE_SIZE_FILTER = "userPageSizeFilter";

  private static final String USER_SEARCH_QUERY = "userSearchQuery";


  @Inject
  private Dao<User> userDao;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    try {

      Session session = Session.fromRequest(request);
      String searchQuery = (String) session.getFilter(USER_SEARCH_QUERY);
      int pageSize = PaginationUtil.getPageSize(request, PAGE_SIZE_FILTER);
      double pageNumber = PaginationUtil.getPageNumber(request);

      List<User> users;
      if (searchQuery == null) {
        users = PaginationUtil.paginate(userDao, pageSize, pageNumber);
      } else {
        String[] columns = {"name", "username", "email"};
        users = PaginationUtil.paginateWithSearch(userDao, pageSize, pageNumber,
            searchQuery, columns);
      }

      int max = PaginationUtil.getMaxPages(userDao, pageSize);
      PaginationUtil.setRequestAttributes(request, max, pageNumber, pageSize);
      request.setAttribute("users", users);
      session.removeFilter(USER_SEARCH_QUERY);

      super.forward(request, response, "Manage Users", "admin.manageusers");
    } catch (SQLException e) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    // Here we will set the filters in the users session.

    String searchQuery = request.getParameter("search-users-query");
    PaginationUtil.postPaginationWithSearch(request, PAGE_SIZE_FILTER, USER_SEARCH_QUERY,
        searchQuery);

    response.sendRedirect("users");
  }

  @Override
  public String getServletInfo() {
    return "AdminManageUsersServlet";
  }
}
