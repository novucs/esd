package net.novucs.esd.controllers.admin;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.controllers.BaseServlet;

public class AdminViewUserServlet extends BaseServlet {

  private static final long serialVersionUID = 1426082847044519303L;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    super.forward(request, response, "View User", "admin.viewuser");
  }

  @Override
  public String getServletInfo() {
    return "AdminViewUserServlet";
  }
}
