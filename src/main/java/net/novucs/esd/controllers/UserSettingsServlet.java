package net.novucs.esd.controllers;

import static javax.servlet.jsp.PageContext.PAGE;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Role;
import net.novucs.esd.model.User;
import net.novucs.esd.model.UserLog;
import net.novucs.esd.model.UserRole;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Where;
import net.novucs.esd.util.DateUtil;
import net.novucs.esd.util.Password;

public class UserSettingsServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;

  private Dao<User> userDao;

  private Dao<Role> roleDao;

  private Dao<UserRole> userRoleDao;

  private Dao<UserLog> userLogDao;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    super.forward(request, response, "Account Settings", "user.settings");
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    try {
      updateAccount(request, response);
    } catch (SQLException | NumberFormatException ex) {
      Logger.getLogger(getClass().getName()).log(Level.WARNING, "Error processing request", ex);
    }
  }

  private void updateAccount(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException, SQLException {
    User user = userDao.selectById(Integer.parseInt(request.getParameter("userId")));
    if (user == null) {
      request.setAttribute("error", "Invalid User ID specified.");
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    user.setName(request.getParameter("full_name"));
    user.setEmail(request.getParameter("new_email"));
    user.setDateOfBirth(new DateUtil()
        .getDateFromString(request.getParameter("date_of_birth")));
    user.setAddress(request.getParameter("address"));

    // Save User
    try {
      userDao.update(user);
      request.setAttribute("updated", true);
    } catch (SQLException e) {
      request.setAttribute("error", "There was an error saving the user. Try again.");
      request.setAttribute("updated", false);
    }

    // Feedback
    request.setAttribute("updated", true);
    super.forward(request, response, "Edit " + user.getName(), "user.settings.success");
  }

  @Override
  public String getServletInfo() {
    return "UserSettingsServlet";
  }
}
