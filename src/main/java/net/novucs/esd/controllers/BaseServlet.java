package net.novucs.esd.controllers;

import java.io.IOException;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.novucs.esd.lifecycle.Session;

public abstract class BaseServlet extends HttpServlet {

  @Resource(lookup = "java:app/AppName")
  private transient String appName;

  private static final long serialVersionUID = 1426081247044519303L;

  public Session getSession(HttpServletRequest request) {
    HttpSession httpSession = request.getSession(false);

    // If a session doesn't exist, request GlassFish make a new one
    if (httpSession == null) {
      httpSession = request.getSession(true);
    }

    // Check if we have a session handler in our session
    Session sessionHandler = (Session) httpSession.getAttribute("session");

    // Create a new session
    if (sessionHandler == null) {
      sessionHandler = new Session();
      httpSession.setAttribute("session", sessionHandler);
    }

    return sessionHandler;
  }

  protected void forward(HttpServletRequest request, HttpServletResponse response,
      String title, String page) throws IOException, ServletException {

    Session session = getSession(request);
    request.setAttribute("errors", session.getErrors());
    request.setAttribute("title", String.format("%s - %s", appName, title));
    request.setAttribute("page", String.format("%s.jsp", page));
    request.getRequestDispatcher("/layout.jsp").forward(request, response);
  }
}
