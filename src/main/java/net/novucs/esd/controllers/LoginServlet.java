package net.novucs.esd.controllers;

import java.io.IOException;
import java.sql.SQLException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.novucs.esd.lifecycle.Session;
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
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String username = request.getParameter("username");
    User user;

    try {
      user = userDao.select().where(new Where().eq("email", username)).first();
    } catch (SQLException e) {
      throw new IOException("Failed to communicate with database", e);
    }

    String password = request.getParameter("password");
    Session session = getSession(request);

    if (user != null && user.getPassword().authenticate(password)) {
      session.setUser(user);
      response.sendRedirect("homepage");
    } else {
      session.pushError("Incorrect username or password");
      super.forward(request, response, LOGIN_TITLE, LOGIN_PATH);
    }
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    Session session = getSession(request);

    // Check if user is already logged in
    if (session.getUser() != null) {
      response.sendRedirect("homepage");
      return;
    }

    response.setContentType("text/html;charset=UTF-8");
    super.forward(request, response, LOGIN_TITLE, LOGIN_PATH);
  }

  @Override
  public String getServletInfo() {
    return "LoginServlet";
  }
}
