package net.novucs.esd.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.novucs.esd.lifecycle.Session;

public class HomepageServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    Session session = getSession(request);
    if (session.getUser() == null) {
      response.sendRedirect("login");
      return;
    }

    response.setContentType("text/html;charset=UTF-8");
    request.getRequestDispatcher("/homepage.jsp").forward(request, response);
  }

  @Override
  public String getServletInfo() {
    return "Homepage Servlet";
  }
}
