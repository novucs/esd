package net.novucs.esd.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.User;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.util.DateUtil;
import net.novucs.esd.util.Password;

public class UserSettingsServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;

  @Inject
  private Dao<User> userDao;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    super.forward(request, response, "Account Settings", "user.settings");
  }

  @Override
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
    Session session = Session.fromRequest(request);
    User user = session.getUser();
    if (user == null) {
      request.setAttribute("error", "Invalid User ID specified.");
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    // Update User
    user.setName(request.getParameter("fullname"));
    user.setEmail(request.getParameter("email"));
    user.setAddress(request.getParameter("address"));
    user.setDateOfBirth(new DateUtil()
        .getDateFromString(request.getParameter("date_of_birth")));

    // Update Password
    Password password = user.getPassword();
    String currentPassword = request.getParameter("current_password");
    String newPassword = request.getParameter("new_password");

    if (!currentPassword.isEmpty()
        && !newPassword.isEmpty()
        && !currentPassword.equals(newPassword)
        && password.authenticate(request.getParameter("current_password"))) {
      user.setNeedsPasswordChange(0);
      user.setPassword(Password.fromPlaintext(newPassword));
    }

    // Save User
    try {
      userDao.update(user);
      request.setAttribute("updated", true);
      response.sendRedirect("settings");
    } catch (SQLException e) {
      Logger.getLogger(UserSettingsServlet.class.getName())
          .log(Level.SEVERE, "Unable to update account settings.", e);
      request.setAttribute("error", "There was an error updating your account settings.");
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return;
    }
  }

  @Override
  public String getServletInfo() {
    return "UserSettingsServlet";
  }
}
