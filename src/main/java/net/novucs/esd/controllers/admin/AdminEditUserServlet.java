package net.novucs.esd.controllers.admin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.controllers.BaseServlet;
import net.novucs.esd.model.Role;
import net.novucs.esd.model.User;
import net.novucs.esd.model.UserRole;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Where;
import net.novucs.esd.util.DateUtil;
import net.novucs.esd.util.Password;

public class AdminEditUserServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;

  @Inject
  private Dao<User> userDao;

  @Inject
  private Dao<UserRole> userRoleDao;

  @Inject
  private Dao<Role> roleDao;

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    try {
      updateUser(request, response);
    } catch (SQLException | NumberFormatException ex) {
      Logger.getLogger(getClass().getName()).log(Level.WARNING, "Error processing request", ex);
    }
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    try {
      User user = userDao.selectById(Integer.parseInt(request.getParameter("userId")));
      if (user == null) {
        request.setAttribute("error", "Invalid User ID specified.");
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return;
      }

      addRoleAttributes(request, user);
      super.forward(request, response,
          "Edit " + user.getName() + " (#" + user.getId() + ")",
          "admin.edituser");
    } catch (SQLException ex) {
      Logger.getLogger(AdminEditUserServlet.class.getName())
          .log(Level.WARNING, "Unable to add user role.");
    }
  }

  private void updateUser(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException, SQLException {
    User user = userDao.selectById(Integer.parseInt(request.getParameter("userId")));
    if (user == null) {
      request.setAttribute("error", "Invalid User ID specified.");
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    // Edit User
    user.setName(request.getParameter("name"));
    user.setEmail(request.getParameter("email"));
    user.setDateOfBirth(new DateUtil()
        .getDateFromString(request.getParameter("date_of_birth")));

    // Update Password
    String firstPassword = request.getParameter("password1");
    String repeatPassword = request.getParameter("password2");
    if (!firstPassword.isEmpty() && !repeatPassword.isEmpty()
        && firstPassword.equals(repeatPassword)) {
      request.setAttribute("notice", "The users password has also been updated.");
      user.setPassword(Password.fromPlaintext(firstPassword));
    }

    // Delete and Re-add User Roles
    userRoleDao.delete(userRoleDao.select()
        .where(new Where().eq("user_id", user.getId()))
        .all());
    userRoleDao.insert(Arrays.stream(request.getParameterValues("roles"))
        .map(role -> new UserRole(user.getId(), Integer.parseInt(role)))
        .toArray(UserRole[]::new));

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
    addRoleAttributes(request, user);
    super.forward(request, response, "Edit " + user.getName(), "admin.edituser");
  }

  private void addRoleAttributes(HttpServletRequest request, User user)
      throws SQLException {
    request.setAttribute("availableRoles", roleDao.select().all());
    request.setAttribute("editUserRoles", userRoleDao.select()
        .where(new Where().eq("user_id", user.getId())).all()
        .stream().map(UserRole::getId)
        .collect(Collectors.toList()));
    request.setAttribute("editUser", user);
  }

  @Override
  public String getServletInfo() {
    return "AdminEditUserServlet";
  }
}
