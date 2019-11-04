package net.novucs.esd.controllers;

import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.User;
import net.novucs.esd.orm.Dao;

@WebServlet(name = "LogoutServlet")
public class LogoutServlet extends BaseServlet {
  private static final long serialVersionUID = 1826081247044519303L;;


  private static final String LOGOUT_PATH = "/logout";
  private static final String LOGOUT_TITLE = "Logout";

  @Inject
  private Dao<User> userDao;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    Session session = getSession(request);
    if (session !=null) {
      request.getSession(false).invalidate();
      response.sendRedirect("login");
      return;
    }

    // Check if user is already logged in


    response.setContentType("text/html;charset=UTF-8");
    super.forward(request, response, LOGOUT_TITLE, LOGOUT_PATH);
  }
  @Override
  public String getServletInfo() {
    return "LogoutServlet";
  }
}
