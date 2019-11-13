package net.novucs.esd.controllers;

import java.io.IOException;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.lifecycle.Session;

/**
 * The type Base servlet.
 */
public abstract class BaseServlet extends HttpServlet {

  private static final long serialVersionUID = 1426081247044519303L;

  @Resource(lookup = "java:app/AppName")
  private transient String appName;

  /**
   * Forward.
   *
   * @param request  the request
   * @param response the response
   * @param title    the title
   * @param page     the page
   * @throws IOException      the io exception
   * @throws ServletException the servlet exception
   */
  protected void forward(HttpServletRequest request, HttpServletResponse response,
      String title, String page) throws IOException, ServletException {
    Session session = Session.fromRequest(request);
    response.setContentType("text/html;charset=UTF-8");
    request.setAttribute("errors", session.getErrors());
    request.setAttribute("title", title);
    request.setAttribute("page", String.format("%s.jsp", page));
    request.setAttribute("name", page);
    request.setAttribute("baseUrl", "/" + appName);
    request.setAttribute(Session.ATTRIBUTE_NAME, session);
    request.getRequestDispatcher("/layout.jsp").forward(request, response);
  }
}
