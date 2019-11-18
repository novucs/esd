package net.novucs.esd.controllers.admin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.controllers.BaseServlet;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.User;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Where;

public class AdminManageUsersServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;

  private final String PAGE_SIZE_FILTER = "userPageSizeFilter";

  private final String USER_SEARCH_QUERY = "userSearchQuery";

  private final String[] pageSizes = { "15", "30", "50" };

  @Inject
  private Dao<User> userDao;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    try {

      String pageNumberParameter = request.getParameter("pn");
      Session session = super.getSession(request);

      Integer pageSize = (Integer) session.getFilter(PAGE_SIZE_FILTER);
      String searchQuery = (String) session.getFilter(USER_SEARCH_QUERY);
      pageSize = pageSize == null ? Integer.parseInt(pageSizes[0]) : pageSize;
      double pageNumber = pageNumberParameter == null ? 1 : Double.parseDouble(pageNumberParameter);

      List<User> users;
      if(searchQuery != null){
        String columns[] = {"name", "email"};
        users = userDao.select().where(new Where().search(searchQuery, columns)).offset(
            (int) (pageSize * (pageNumber -1))).limit(pageSize).all();
      } else {
       users = userDao.select().offset(
            (int) (pageSize * (pageNumber -1))).limit(pageSize).all();
      }

      double numberOfUsers = userDao.select().all().size();
      int max = (int) Math.ceil(numberOfUsers / (double) pageSize);

      request.setAttribute("users", users);
      request.setAttribute("maxPages", max);
      request.setAttribute("pn", pageNumber);
      request.setAttribute("ps", pageSize);
      request.setAttribute("pageSizes", pageSizes);
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
    String ps = request.getParameter("page-size");
    String searchQuery = request.getParameter("search-users-query");
    Session session = super.getSession(request);

    if(ps != null){
      session.setFilter(PAGE_SIZE_FILTER, Integer.parseInt(ps));
    }

    if(searchQuery != null){
      session.setFilter(USER_SEARCH_QUERY, searchQuery);
    }
    response.sendRedirect("users");
  }

  @Override
  public String getServletInfo() {
    return "AdminManageUsersServlet";
  }
}
