package net.novucs.esd.controllers;

import java.io.IOException;
import java.sql.SQLException;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.novucs.esd.model.User;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Where;
import net.novucs.esd.util.Password;


public class RegistrationServlet extends BaseServlet {

  private static String addressBuilder(String addressName, String street, String city,
      String county, String postcode) {
    return addressName + "," + street + "," + city + "," + county + "," + postcode;
  }

  @Resource(lookup = "java:app/AppName")
  private transient String appName;
  private static final long serialVersionUID = 1426082847044519303L;

  private static final String REGISTER_TITLE = "Register";
  private static final String REGISTER_SUCCESS_TITLE = "Registration Success";
  private static final String REGISTER_FAIL_TITLE = "Registration Unsuccessful";

  private static final String REGISTER_PAGE = "register";
  private static final String REGISTER_SUCCESS_PAGE = REGISTER_PAGE + "success";
  private static final String REGISTER_FAIL_PAGE = REGISTER_PAGE + "fail";
  @Inject
  private Dao<User> userDao;

  private void processRequest(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException, SQLException {
    System.out.println("RegistrationServlet | Method: " + request.getMethod());

    if (request.getMethod().equalsIgnoreCase("GET")) {
      super.forward(request, response, REGISTER_TITLE, REGISTER_PAGE);
    } else {
      String username = request.getParameter("username");
      User user;
      try {
        user = userDao.select().where(new Where().eq("email", username)).first();
      } catch (SQLException e) {
        throw new IOException("Failed to communicate with database", e);
      }

      // User with email address already exists
      if (user != null) {
        // Delete reference to user as security measure
        user = null;
        super.forward(request, response, REGISTER_FAIL_TITLE, REGISTER_FAIL_PAGE);
      }
      // New user can be stored in database
      else {
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
        user = new User(name, email, hashedPassword, address, "APPLICATION");
        userDao.insert(user);
        user = null;
        user = userDao.select().where(new Where().eq("email", username)).first();
        if(user != null){
          super.forward(request, response, REGISTER_SUCCESS_TITLE, REGISTER_SUCCESS_PAGE);
        } else throw new SQLException("Create User failed: Expected one result, found none.");
      }
    }
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    try {
      processRequest(request, response);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    try {
      processRequest(request, response);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String getServletInfo() {
    return "RegistrationServlet";
  }
}
