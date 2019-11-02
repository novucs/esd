package net.novucs.esd.controllers;

import net.novucs.esd.controllers.BaseServlet;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.User;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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

    User user = session.getUser();
    response.setContentType("text/html;charset=UTF-8");
    request.setAttribute("title", "Homepage - " + user.getName());
    request.setAttribute("page", "/homepage.jsp");
    request.getRequestDispatcher("/layout.jsp").forward(request, response);
  }

  @Override
  public String getServletInfo() {
    return "Homepage Servlet";
  }
}
