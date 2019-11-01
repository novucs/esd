package net.novucs.esd.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.novucs.esd.enums.LoginStatus;

@WebServlet(name = "LoginServlet")
public class LoginServlet extends BaseServlet {

  private static final long serialVersionUID = 1826081247044519303L;

  private static final String LOGIN_PATH = "/login";
  private static final String LOGIN_TITLE = "Login";

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    // String username = request.getParameter("username");
    // String password = request.getParameter("password");

    LoginStatus status = LoginStatus.INCORRECT_CREDENTIALS;

    if (status == LoginStatus.LOGGED_IN) {
      super.forward(request, response, "Homepage", "/homepage");
    } else if (status == LoginStatus.INCORRECT_CREDENTIALS) {
      super.addResponseError("IncorrectCredentials", "Incorrect username or password");
      super.forward(request, response, LOGIN_TITLE, LOGIN_PATH);
    } else if (status == LoginStatus.LOGIN_FAILED) {
      super.addResponseError("FailedLogin", "Failed to login");
      super.forward(request, response, LOGIN_TITLE, LOGIN_PATH);
    }

    super.addResponseError("Error", "Internal error.");
    super.forward(request, response, LOGIN_TITLE, LOGIN_PATH);
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("text/html;charset=UTF-8");
    super.forward(request, response, LOGIN_TITLE, LOGIN_PATH);
  }

  @Override
  public String getServletInfo() {
    return "LoginServlet";
  }
}
