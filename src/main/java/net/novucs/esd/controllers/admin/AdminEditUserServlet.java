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
import net.novucs.esd.util.DateUtil;
import net.novucs.esd.util.Password;

public class AdminEditUserServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;

  @Inject
  private Dao<User> userDao;

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    User user = getUserFromId(request);
    if (user == null) {
      request.setAttribute("error", "Invalid User ID specified.");
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    // Edit User
    user.setName(request.getParameter("name"));
    user.setEmail(request.getParameter("email"));
    user.setDateOfBirth(new DateUtil()
        .getDateFromString(request.getParameter("date_of_birth"))
    );

    // Update Password
    String password1 = request.getParameter("password1");
    String password2 = request.getParameter("password2");
    if ((!password1.isEmpty() && !password2.isEmpty()) && password1.equals(password2)) {
      request.setAttribute("notice", "The users password has also been updated.");
      user.setPassword(Password.fromPlaintext(password1));
    }

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
    request.setAttribute("editUser", user);
    super.forward(request, response, "Edit " + user.getName(), "admin.edituser");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    User user = getUserFromId(request);
    if (user == null) {
      request.setAttribute("error", "Invalid User ID specified.");
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    request.setAttribute("editUser", user);
    super.forward(request, response,
        "Edit " + user.getName() + " (#" + user.getId() + ")",
        "admin.edituser");
  }

  /**
   * Get the User passed via the request parameter.
   *
   * @param request Servlet Request
   * @return User / Null
   */
  private User getUserFromId(HttpServletRequest request) {
    // Check if we have received a User ID to edit
    Integer userId;
    try {
      userId = Integer.parseInt(request.getParameter("userId"));
    } catch (NumberFormatException e) {
      return null;
    }

    // Check if the user exists
    User user;
    try {
      user = userDao.selectById(userId);
    } catch (SQLException e) {
      return null;
    }
    return user;
  }

  @Override
  public String getServletInfo() {
    return "AdminEditUserServlet";
  }
}
