package net.novucs.esd.controllers;

import java.io.IOException;
import java.sql.SQLException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.model.User;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Where;

@WebServlet(name = "LoginServlet")
public class LoginServlet extends BaseServlet {

  private static final long serialVersionUID = 1826081247044519303L;

  private static final String LOGIN_PATH = "/login";
  private static final String LOGIN_TITLE = "Login";

  @Inject
  private Dao<User> userDao;

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String username = request.getParameter("username");
    String password = request.getParameter("password");
    boolean loggedIn;

    try {
      User user = userDao.select().where(new Where().eq("email", username)).first();
      loggedIn = user != null && user.getPassword().authenticate(password);
    } catch (SQLException e) {
      throw new IOException("Failed to communicate with database", e);
    }

    if (loggedIn) {
      response.sendRedirect("homepage");
    } else {
      super.addResponseError("IncorrectCredentials", "Incorrect username or password");
      super.forward(request, response, LOGIN_TITLE, LOGIN_PATH);
    }
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
