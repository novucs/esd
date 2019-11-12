package net.novucs.esd.controllers.member;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.controllers.BaseServlet;

public class MemberDashboardServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    super.forward(request, response, "Dashboard", "member.dashboard");
  }

  @Override
  public String getServletInfo() {
    return "MemberDashboardServlet";
  }
}
