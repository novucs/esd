package net.novucs.esd.filter;

import java.io.IOException;
import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.novucs.esd.lifecycle.Session;

public class LoggedInFilter extends BaseFilter implements Filter {

  private static final String SESSION_ATTRIBUTE = "session";

  @Resource(lookup = "java:app/AppName")
  private transient String appName;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    String path = ((HttpServletRequest) request).getRequestURI();
    if (super.pathIsExcluded(path)) {
      chain.doFilter(request, response); // Just continue chain.
    } else {

      HttpServletRequest httpRequest = (HttpServletRequest) request;
      HttpServletResponse httpResponse = (HttpServletResponse) response;

      HttpSession httpSession = httpRequest.getSession(false);

      // If a session doesn't exist, request GlassFish make a new one
      if (httpSession == null) {
        httpSession = httpRequest.getSession(true);
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

      if (sessionHandler.getUser() == null) {
        httpResponse.sendRedirect("/" + appName + "/login");
        return;
      }

      chain.doFilter(request, response);
    }
  }
}
