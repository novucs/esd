package net.novucs.esd.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(name = "LoginServlet")
public class LoginServlet extends BaseServlet {

  private  static final long serialVersionUID = 1826081247044519303L;

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    // String username = request.getParameter("username");
    // String password = request.getParameter("password");

    // TODO: Generate user model from the provided parameters,
    //  see if they exist and if they do, create a session.

    Boolean didLogin = false;

    if (didLogin) {
      super.forward(request, response, "Register", "/register");
    } else {
      // Add error
      super.addResponseError("LoginFailed", "Failed to login");
      super.addResponseError("LoginFailed2", "You suck");
      super.forward(request, response, "Login", "/login");
    }
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("text/html;charset=UTF-8");
    super.forward(request, response, "Login", "/login");
  }
}
