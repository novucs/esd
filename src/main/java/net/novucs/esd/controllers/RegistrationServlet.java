package net.novucs.esd.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.model.Application;
import net.novucs.esd.model.Role;
import net.novucs.esd.model.User;
import net.novucs.esd.model.UserLog;
import net.novucs.esd.model.UserRole;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Where;
import net.novucs.esd.util.DateUtil;
import net.novucs.esd.util.Password;
import net.novucs.esd.util.StringUtil;

/**
 * The type Registration servlet.
 */
public class RegistrationServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;

  private static final String PAGE = "register";

  @Inject
  private Dao<User> userDao;

  @Inject
  private Dao<Role> roleDao;

  @Inject
  private Dao<UserRole> userRoleDao;

  @Inject
  private Dao<UserLog> userLogDao;

  @Inject
  private Dao<Application> applicationDao;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    super.forward(request, response, "Register", PAGE);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    User user = parseUser(request);
    try {
      // Ensure user with same email does not already exist.
      User matched = userDao.select()
          .where(new Where().eq("email", user.getEmail()))
          .first();

      if (matched != null) {
        request.setAttribute("registerStatus", "fail");
        super.forward(request, response, "Email already in use", PAGE);
        return;
      }

      // Get the 'User' role
      Role userRole = roleDao.select()
          .where(new Where().eq("name", "User"))
          .first();

      // Insert new user to database.
      userDao.insert(user);
      Application application = new Application(user.getId(), BigDecimal.ZERO);
      applicationDao.insert(application);
      UserLog userLog = parseUserLog(request, user);
      userLogDao.insert(userLog);
      userRoleDao.insert(new UserRole(user.getId(), userRole.getId()));
    } catch (SQLException e) {
      Logger.getLogger(RegistrationServlet.class.getName())
          .log(Level.SEVERE, null, e);
      request.setAttribute("registerStatus", "fail");
      super.forward(request, response, "Database error", PAGE);
      return;
    }

    // Set password attribute so temporary password
    // can be provided to user on registration success page.
    request.setAttribute("password", Password
        .getPasswordFromDateOfBirth(request.getParameter("dob")));
    request.setAttribute("registerStatus", "success");
    super.forward(request, response, "Registration Success", PAGE);
  }

  private User parseUser(HttpServletRequest request) {
    String name = request.getParameter("full-name");
    String email = request.getParameter("email");
    String address = parseAddress(request);
    DateUtil dateUtil = new DateUtil();
    ZonedDateTime dateOfBirth = dateUtil.getDateFromString(request.getParameter("dob"));
    return new User(
        name,
        StringUtil.parseUsername(name),
        email,
        Password.fromPlaintext(
            Password.getPasswordFromDateOfBirth(request.getParameter("dob"))
        ),
        address,
        dateOfBirth,
        "APPLICATION",
        1
    );
  }

  private String parseAddress(HttpServletRequest request) {
    // TODO: Confirm how we are dealing with addresses.
    StringJoiner addressJoiner = new StringJoiner(", ");
    addressJoiner.add(request.getParameter("address-name"));
    addressJoiner.add(request.getParameter("address-street"));
    addressJoiner.add(request.getParameter("address-city"));
    addressJoiner.add(request.getParameter("address-county"));
    addressJoiner.add(request.getParameter("address-postcode"));
    return addressJoiner.toString();
  }

  private UserLog parseUserLog(HttpServletRequest request, User user) {
    DateUtil dateUtil = new DateUtil();
    String message = "APPLICATION: " + dateUtil.getDateTimeNow();
    return new UserLog(user.getId(), message, request.getRemoteAddr());
  }

  @Override
  public String getServletInfo() {
    return "RegistrationServlet";
  }
}
