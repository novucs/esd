package net.novucs.esd.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.StringJoiner;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.model.User;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Where;
import net.novucs.esd.util.Password;

public class RegistrationServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;

  private static final String PAGE = "register";
  private static final String SUCCESS_PAGE = PAGE + "success";
  private static final String FAIL_PAGE = PAGE + "fail";

  @Inject
  private Dao<User> userDao;

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
        super.forward(request, response, "Email already in use", FAIL_PAGE);
        return;
      }

      // Insert new user to database.
      userDao.insert(user);
    } catch (SQLException e) {
      super.forward(request, response, "Database error", FAIL_PAGE);
      return;
    }

    super.forward(request, response, "Registration Success", SUCCESS_PAGE);
  }

  private User parseUser(HttpServletRequest request) {
    String name = request.getParameter("full-name");
    String email = request.getParameter("username");
    Password password = Password.fromPlaintext(request.getParameter("password"));
    String address = parseAddress(request);
    return new User(name, email, password, address, "APPLICATION");
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

  @Override
  public String getServletInfo() {
    return "RegistrationServlet";
  }
}
