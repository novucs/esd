package net.novucs.esd.controllers;

import net.novucs.esd.enums.LoginStatus;
import net.novucs.esd.model.Login;
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

    String username = request.getParameter("username");
    String password = request.getParameter("password");

    Login loginModel = new Login(username, password);
    LoginStatus status = loginModel.go();

    if (status == LoginStatus.LoggedIn) {
      super.forward(request, response, "Homepage", "/homepage");
    } else if(status == LoginStatus.IncorrectCredentials) {

      super.addResponseError("IncorrectCredentials", "Incorrect username or password.");
      super.forward(request, response, "Login", "/login");
    } else if(status == LoginStatus.LoginFailed){
      super.addResponseError("FailedLogin", "Failed to login.");
      super.forward(request, response, "Login", "/login");
    }

    super.addResponseError("Error", "Internal error.");
    super.forward(request, response, "Login", "/login");
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("text/html;charset=UTF-8");
    super.forward(request, response, "Login", "/login");
  }

  @Override
  public String getServletInfo(){
    return "LoginServlet";
  }
}
