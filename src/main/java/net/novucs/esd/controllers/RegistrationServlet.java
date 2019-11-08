package net.novucs.esd.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.StringJoiner;
import java.util.UUID;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.model.User;
import net.novucs.esd.model.UserLog;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Where;
import net.novucs.esd.util.DateUtil;
import net.novucs.esd.util.Password;

public class RegistrationServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;

  private static final String PAGE = "register";
  private static final String SUCCESS_PAGE = PAGE + "success";
  private static final String FAIL_PAGE = PAGE + "fail";

  @Inject
  private Dao<User> userDao;

  @Inject
  private Dao<UserLog> userLogDao;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    super.forward(request, response, "Register", PAGE);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String generatedPassword = generatePassword();
    User user = parseUser(request, generatedPassword);
    try {
      // Ensure user with same email does not already exist.
      User matched = userDao.select()
          .where(new Where().eq("email", user.getEmail()))
          .first();

      if (matched != null) {
        super.forward(request, response, "Email already in use", FAIL_PAGE);
        return;
      }

      // Insert new user to database.
      userDao.insert(user);
      UserLog userLog = parseUserLog(request, user);
      userLogDao.insert(userLog);

    } catch (SQLException e) {
      super.forward(request, response, "Database error", FAIL_PAGE);
      return;
    }

    // Set password attribute so temporary password
    // can be provided to user on registration success page.
    request.setAttribute("password", generatedPassword);
    super.forward(request, response, "Registration Success", SUCCESS_PAGE);
  }

  private String generatePassword() {
    // Use last 12 characters of random uuid to create password
    String uuid = UUID.randomUUID().toString();
    String[] uuidArray = uuid.split("-");
    return uuidArray[uuidArray.length - 1];
  }

  private User parseUser(HttpServletRequest request, String randomPassword) {
    String name = request.getParameter("full-name");
    String email = request.getParameter("username");
    Password password = Password.fromPlaintext(randomPassword);
    String address = parseAddress(request);
    DateUtil dateUtil = new DateUtil();
    ZonedDateTime dateOfBirth = dateUtil.getDateFromString(request.getParameter("dob"));
    return new User(name, email, password, address, dateOfBirth, "APPLICATION");
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
