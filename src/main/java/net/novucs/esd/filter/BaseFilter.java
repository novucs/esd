package net.novucs.esd.filter;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Role;

public class BaseFilter {

  @Resource(lookup = "java:app/AppName")
  private transient String appName;

  protected Boolean pathIsExcluded(String path) {
    return path == null || path.equals("/app/") || path.contains("/register")
        || path.contains("/login") || path.matches(".*(css|jpg|jpeg|png|gif|js|.jsp)");
  }

  public void handleRoleFilter(String roleName, ServletRequest request,
      ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    String path = httpRequest.getRequestURI();

    if (pathIsExcluded(path)) {
      chain.doFilter(request, response); // Just continue chain.
    } else {
      HttpSession httpSession = httpRequest.getSession(false);

      if (httpSession == null || httpSession.getAttribute("session") == null) {
        httpResponse.sendRedirect("/" + appName + "/login");
        return;
      }

      Session session = (Session) httpSession.getAttribute("session");
      List<Role> userRoles = session.getRoles();
      if (userRoles == null || userRoles.isEmpty()) {
        httpResponse.sendRedirect("/" + appName + "/login");
        return;
      }

      Boolean roleExists = userRoles.stream()
          .anyMatch(r -> r.getName().toLowerCase(Locale.getDefault()).equals(roleName));
      if (roleExists) {
        chain.doFilter(request, response);
      } else {
        httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
      }
    }
  }
}
