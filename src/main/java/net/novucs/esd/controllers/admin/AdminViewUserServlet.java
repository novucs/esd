package net.novucs.esd.controllers.admin;

import java.io.IOException;
import java.sql.SQLException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.controllers.BaseServlet;
import net.novucs.esd.model.User;
import net.novucs.esd.orm.Dao;

public class AdminViewUserServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;

  @Inject
  private Dao<User> userDao;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String userId  = request.getParameter("userId");

    try{
      User user = userDao.selectById(Integer.parseInt(userId));
      request.setAttribute("user", user);
      super.forward(request, response, "View User", "admin.viewuser");
    } catch (SQLException e){
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    super.forward(request, response, "View User", "admin.viewuser");
  }

  @Override
  public String getServletInfo() {
    return "AdminViewUserServlet";
  }
}
