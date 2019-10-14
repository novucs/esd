package net.novucs.esd;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ExampleServerlet extends HttpServlet {

  private static final long serialVersionUID = 1426082847044519303L;

  private void processRequest(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    response.setContentType("text/html;charset=UTF-8");
    Map<String, String> anExampleMap = new HashMap<>();
    anExampleMap.put("a", "b");
    anExampleMap.put("hello", "friend");
    request.setAttribute("anExampleMap", anExampleMap);
    request.getRequestDispatcher("/example.jsp").forward(request, response);
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    processRequest(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    processRequest(request, response);
  }

  @Override
  public String getServletInfo() {
    return "An example serverlet";
  }
}
