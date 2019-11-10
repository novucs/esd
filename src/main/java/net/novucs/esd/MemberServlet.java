package net.novucs.esd;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The type Member servlet.
 */
public class MemberServlet extends HttpServlet {

  private static final long serialVersionUID = 1426082847044519303L;

  private void processRequest(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    response.setContentType("text/html;charset=UTF-8");

    // Setting the title and the location of the page you would like to view
    // while dispatching to the layout will ensure consistent UI.
    request.setAttribute("title", "Members");
    request.setAttribute("page", "/members.jsp");
    request.getRequestDispatcher("/layout.jsp").forward(request, response);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    processRequest(request, response);
  }

  @Override
  public String getServletInfo() {
    return "Member Dashboard Servlet";
  }
}
