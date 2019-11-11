package net.novucs.esd.controllers;

import java.io.IOException;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.novucs.esd.lifecycle.Session;

/**
 * The type Base servlet.
 */
public abstract class BaseServlet extends HttpServlet {

  private static final String SESSION_ATTRIBUTE = "session";
  private static final long serialVersionUID = 1426081247044519303L;

  @Resource(lookup = "java:app/AppName")
  private transient String appName;

  /**
   * Gets session.
   *
   * @param request the request
   * @return the session
   */
  public Session getSession(HttpServletRequest request) {
    HttpSession httpSession = request.getSession(false);

    // If a session doesn't exist, request GlassFish make a new one
    if (httpSession == null) {
      httpSession = request.getSession(true);
    }

    // Check if we have a session handler in our session
    Session sessionHandler;
    if (httpSession.getAttribute(SESSION_ATTRIBUTE) == null) {
      // Create a session
      sessionHandler = new Session();
      httpSession.setAttribute(SESSION_ATTRIBUTE, sessionHandler);
    } else {
      // Invoke our old session
      sessionHandler = (Session) httpSession.getAttribute(SESSION_ATTRIBUTE);
    }

    return sessionHandler;
  }

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
    Session session = getSession(request);
    response.setContentType("text/html;charset=UTF-8");
    request.setAttribute("errors", session.getErrors());
    request.setAttribute("title", String.format("%s", title));
    request.setAttribute("page", String.format("%s.jsp", page));
    request.setAttribute("name", page);
    request.setAttribute("baseUrl", "/" + appName);
    request.setAttribute(SESSION_ATTRIBUTE, session);
    request.getRequestDispatcher("/layout.jsp").forward(request, response);
  }
}
