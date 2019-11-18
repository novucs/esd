package net.novucs.esd.controllers.admin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.controllers.BaseServlet;
import net.novucs.esd.model.User;
import net.novucs.esd.orm.Dao;

public class AdminManageUsersServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;

  @Inject
  private Dao<User> userDao;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    try {
      String pageNumberParameter = request.getParameter("pn");
      String pageSizeParameter = request.getParameter("ps");
      double pageSize = pageSizeParameter == null ? 15 : Integer.parseInt(pageSizeParameter);
      int pageNumber = pageNumberParameter == null ? 1 : Integer.parseInt(pageNumberParameter);
      List<User> users = userDao.select().offset((int) pageSize * (pageNumber -1)).limit((int)pageSize).all();
      double numberOfUsers = userDao.select().all().size();
      int max = (int) Math.ceil(numberOfUsers / pageSize);
      request.setAttribute("users", users);
      request.setAttribute("maxPages", max);
      request.setAttribute("pn", pageNumber);
      request.setAttribute("ps", pageSize);
      super.forward(request, response, "Manage Users", "admin.manageusers");
    } catch (SQLException e) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public String getServletInfo() {
    return "AdminManageUsersServlet";
  }
}
