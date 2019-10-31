package net.novucs.esd.controllers;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public abstract class BaseServlet extends HttpServlet {

  @Resource(lookup = "java:app/AppName")
  private transient String appName;

  private static final long serialVersionUID = 1426081247044519303L;

  private final transient Map<String, String> errors = new ConcurrentHashMap<>();

  protected void addResponseError(String title, String message) {
    errors.put(title, message);
  }

  protected void forward(HttpServletRequest req, HttpServletResponse resp,
                         String title, String page) throws IOException, ServletException {

    req.setAttribute("errors", this.errors);
    req.setAttribute("title", String.format("%s - %s", appName, title));
    req.setAttribute("page", String.format("%s.jsp", page));
    req.getRequestDispatcher("/layout.jsp").forward(req, resp);
  }
}
