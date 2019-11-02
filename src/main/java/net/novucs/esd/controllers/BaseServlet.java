package net.novucs.esd.controllers;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.novucs.esd.handler.SessionHandler;
import net.novucs.esd.model.User;
import net.novucs.esd.util.Password;

public abstract class BaseServlet extends HttpServlet {

  @Resource(lookup = "java:app/AppName")
  private transient String appName;

  private static final long serialVersionUID = 1426081247044519303L;

  private final transient Map<String, String> errors = new ConcurrentHashMap<>();

  protected void addResponseError(String title, String message) {
    errors.put(title, message);
  }

  protected void forward(HttpServletRequest request, HttpServletResponse response,
                         String title, String page) throws IOException, ServletException {

    SessionHandler clientSession = new SessionHandler(request.getSession().getId().toString());


    HttpSession userSession = request.getSession();
    userSession.setAttribute("user", new User("ZombieBot", "bob@email.com", Password.fromPlaintext("test"), "", "0"));

    User sessionUser = ((User) userSession.getAttribute("user"));

    request.setAttribute("errors", this.errors);
    request.setAttribute("title", String.format("%s - %s (%s)", appName, title, sessionUser.getName()));
    request.setAttribute("page", String.format("%s.jsp", page));
    request.getRequestDispatcher("/layout.jsp").forward(request, response);
    errors.clear();
  }
}
