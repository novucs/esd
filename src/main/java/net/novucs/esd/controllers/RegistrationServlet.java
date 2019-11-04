package net.novucs.esd.controllers;

import java.io.IOException;
import java.sql.SQLException;
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

  private static final String TITLE = "Register";
  private static final String SUCCESS_TITLE = "Registration Success";
  private static final String FAIL_TITLE = "Registration Unsuccessful";

  private static final String PAGE = "register";
  private static final String SUCCESS_PAGE = PAGE + "success";
  private static final String FAIL_PAGE = PAGE + "fail";

  @Inject
  private Dao<User> userDao;

  private void processRequest(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (request.getMethod().equalsIgnoreCase("GET")) {
      super.forward(request, response, TITLE, PAGE);
    } else {
      String username = request.getParameter("username");
      User user;
      try {
        user = userDao.select().where(new Where().eq("email", username)).first();
      } catch (SQLException e) {
        throw new IOException("Failed to communicate with database", e);
      }

      // New user can be stored in database
      if (user == null) {
        // No validation required as handled on page
        String name = request.getParameter("full-name");
        String email = request.getParameter("username");
        String password = request.getParameter("password");
        String addressName = request.getParameter("address-name");
        String street = request.getParameter("address-street");
        String city = request.getParameter("address-city");
        String county = request.getParameter("address-county");
        String postcode = request.getParameter("address-postcode");

        // Password securely hashed
        Password hashedPassword = Password.fromPlaintext(password);

        // TODO Confirm how we are dealing with Addresses.
        String address = addressBuilder(addressName, street, city, county, postcode);

        // Create user and insert into DB
        try {
          user = new User(name, email, hashedPassword, address, "APPLICATION");
          userDao.insert(user);
          user = userDao.select().where(new Where().eq("email", username)).first();
          if (user == null) {
            throw new SQLException("Create User failed: Expected one result, found none.");
          } else {
            super.forward(request, response, SUCCESS_TITLE, SUCCESS_PAGE);
          }
        } catch (SQLException ignore) {  // NOPMD
        }
      } else {
        // User with email address already exists
        super.forward(request, response, FAIL_TITLE, FAIL_PAGE);
      }
    }
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    processRequest(request, response);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    processRequest(request, response);
  }

  private static String addressBuilder(String addressName, String street, String city,
      String county, String postcode) {
    return addressName + "," + street + "," + city + "," + county + "," + postcode;
  }

  @Override
  public String getServletInfo() {
    return "RegistrationServlet";
  }
}
