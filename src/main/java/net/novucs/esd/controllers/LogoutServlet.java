package net.novucs.esd.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "LogoutServlet")
public class LogoutServlet extends HttpServlet {

  private static final long serialVersionUID = 1826081247044519303L;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    HttpSession session = request.getSession(false);

    if (session != null) {
      session.invalidate();
    }
    response.sendRedirect("login");
  }

  @Override
  public String getServletInfo() {
    return "LogoutServlet";
  }
}