package net.novucs.esd.controllers.admin;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.controllers.BaseServlet;
import net.novucs.esd.lifecycle.Session;

public class AdminManageUsersServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    // This page is limited to logged in members
    Session session = super.getSession(request);
    if (session.getUser() == null) {
      response.sendRedirect("../login");
      return;
    }

    super.forward(request, response, "Manage Users", "admin.manageusers");
  }

  @Override
  public String getServletInfo() {
    return "AdminManageUsersServlet";
  }
}
