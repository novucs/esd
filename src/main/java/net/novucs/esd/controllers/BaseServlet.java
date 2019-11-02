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
    HttpSession httpSession = request.getSession();
    Session session = (Session) httpSession.getAttribute("session");

    // Create a new session
    if (session == null) {
      session = new Session();
      httpSession.setAttribute("session", session);
    }

    return session;
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
