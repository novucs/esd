package net.novucs.esd.controllers.admin;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.controllers.BaseServlet;

public class AdminDashboardServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    // todo: pull outstanding members, current members etc from db.
    request.setAttribute("outstandingMemberApplications", "45");
    request.setAttribute("currentMembers", "100");
    request.setAttribute("outstandingBalances", "15");
    super.forward(request, response, "Dashboard", "admin.dashboard");
  }

  @Override
  public String getServletInfo() {
    return "AdminDashboardServlet";
  }
}
