package net.novucs.esd.controllers;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HomepageServlet extends HttpServlet {

  private static final long serialVersionUID = 1426082847044519303L;

  private void processRequest(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    response.setContentType("text/html;charset=UTF-8");
    Map<String, String> anHomepageMap = new ConcurrentHashMap<>();
    anHomepageMap.put("a", "b");
    anHomepageMap.put("hello", "friend");
    request.setAttribute("anHomepageMap", anHomepageMap);

    // Setting the title and the location of the page you would like to view
    // while dispatching to the layout will ensure consistent UI.
    request.setAttribute("title", "Homepage");
    request.setAttribute("page", "/homepage.jsp");
    request.getRequestDispatcher("/homepage.jsp").forward(request, response);
  }


  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    processRequest(request, response);
  }

  @Override
  public String getServletInfo() {
    return "Homepage Servlet";
  }
}
