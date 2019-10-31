package net.novucs.esd.controllers;

import java.io.IOException;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RegistrationServlet extends HttpServlet {
  private class RegistrationResponse {
    private final String email;
    private final String password;
    private final String addressName;
    private final String street;
    private final String city;
    private final String county;
    private final String postcode;

    private RegistrationResponse(HttpServletRequest request) throws IOException, ServletException {
      this.email = request.getParameter("username");
      this.password = request.getParameter("password");
      this.addressName = request.getParameter("address-name");
      this.street = request.getParameter("address-street");
      this.city = request.getParameter("address-city");
      this.county = request.getParameter("address-county");
      this.postcode = request.getParameter("address-postcode");
    }
    private String GetEmail(){
      return this.email;
    }
    private String GetPassword(){
      return this.password;
    }
    private String GetAddress(){
      return this.addressName + this.street + this.city + this.county + this.postcode;
    }
  }

  @Resource(lookup = "java:app/AppName")
  private transient String appName;
  private static final long serialVersionUID = 1426082847044519303L;

  private void processRequest(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    System.out.println("RegistrationServlet | Method: " + request.getMethod());

    if(request.getMethod().equalsIgnoreCase("POST")){
      // TODO: Use ORM to store in DB
      RegistrationResponse registrationResponse = new RegistrationResponse(request);
      System.out.println("Email: " + registrationResponse.GetEmail());
      System.out.println("Password: " + registrationResponse.GetPassword());
      System.out.println("Address: " + registrationResponse.GetAddress());

      // TODO pass some user data back to success page for customised response
      response.setContentType("text/html;charset=UTF-8");
      request.setAttribute("title", "Registration Success");
      request.setAttribute("page", "/registersuccess.jsp");
      request.setAttribute("username", registrationResponse.email);
      request.getRequestDispatcher("/layout.jsp").forward(request, response);
    }
    else {
      response.setContentType("text/html;charset=UTF-8");
      request.setAttribute("title", "Register");
      request.setAttribute("page", "/register.jsp");
      request.getRequestDispatcher("/layout.jsp").forward(request, response);
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

  @Override
  public String getServletInfo() {
    return "RegistrationServlet";
  }
}
