package net.novucs.esd;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class ExampleServlet extends HttpServlet {

  private static final long serialVersionUID = 1426082847044519303L;

  @Resource(lookup = "java:app/AppName")
  private transient String appName;

  private void processRequest(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    response.setContentType("text/html;charset=UTF-8");
    Map<String, String> anExampleMap = new ConcurrentHashMap<>();
    anExampleMap.put("a", "b");
    anExampleMap.put("hello", "friend");
    anExampleMap.put("appname", this.appName);
    request.setAttribute("anExampleMap", anExampleMap);
    
    // Setting the title and the location of the page you would like to view
    // while dispatching to the layout will ensure consistent UI.
    request.setAttribute("title", "Example");
    request.setAttribute("page", "/example.jsp");
    request.getRequestDispatcher("/layout.jsp").forward(request, response);    
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    processRequest(request, response);
  }

  @Override
  public String getServletInfo() {
    return "An example servlet";
  }
}
