package net.novucs.esd.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Action;
import net.novucs.esd.model.User;
import net.novucs.esd.model.UserAction;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Where;
import net.novucs.esd.util.DateUtil;
import net.novucs.esd.util.Password;

public class UserProfileServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;

  @Inject
  private Dao<User> userDao;

  @Inject
  private Dao<Action> actionDao;

  @Inject
  private Dao<UserAction> userActionDao;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    // Check for action

    int userId = Session.fromRequest(request).getUser().getId();
    try {
      List<UserAction> userActions = userActionDao.select().where(
          new Where().eq("user_id", userId))
          .all();

      if(userActions != null && userActions.size() > 0){
        List<Action> actions = new ArrayList<>();
        for(UserAction userAction : userActions){
          actions.add(actionDao.select().where(
              new Where().eq("id", userAction.getActionId())).one());
        }

        request.setAttribute("actions", actions);
      }

      super.forward(request, response, "My Profile", "user.profile");

    } catch (SQLException e) {
      e.printStackTrace();
    }

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
    updateUserPassword(request, user, password, currentPassword, newPassword);

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
      super.forward(request, response, "My Profile", "user.profile");
    } catch (SQLException e) {
      Logger.getLogger(UserProfileServlet.class.getName())
          .log(Level.SEVERE, "Unable to update account settings.", e);
      request.setAttribute("error", "There was an error updating your account settings.");
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return;
    }
  }

  private void updateUserPassword(HttpServletRequest request, User user, Password password,
      String currentPassword, String newPassword) {
    if (!currentPassword.isEmpty()) {
      if (password.authenticate(currentPassword)) {
        if (newPassword.isEmpty()) {
          request.setAttribute(
              "notice",
              "You entered your password, but didn't specify a new password. "
                  + "Your password has not been updated."
          );
        } else {
          user.setNeedsPasswordChange(0);
          user.setPassword(Password.fromPlaintext(newPassword));
        }
      } else {
        request.setAttribute(
            "notice",
            "You entered an incorrect password. Please try again. "
                + "Your password has not been updated."

        );
      }
    }
  }

  @Override
  public String getServletInfo() {
    return "UserProfileServlet";
  }
}
