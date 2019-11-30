package net.novucs.esd.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
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
import net.novucs.esd.model.Role;

public abstract class BaseFilter implements Filter {

  @Resource(lookup = "java:app/AppName")
  private transient String appName;

  public boolean isPathExcluded(String path) {
    if (path == null) {
      return true;
    }

    String[] tokens = path.split("/");
    String basePath = String.join("/",
        Arrays.copyOfRange(tokens, Math.min(tokens.length, 2), tokens.length));

    return basePath.isEmpty()
        || "register".equalsIgnoreCase(basePath)
        || "login".equalsIgnoreCase(basePath)
        || basePath.contains("api")
        || basePath.matches("^.*(css|jpg|jpeg|png|gif|js)$");
  }

  public void filterByRole(String roleName, ServletRequest request,
      ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    String path = httpRequest.getRequestURI();

    if (isPathExcluded(path)) {
      chain.doFilter(request, response); // Just continue chain.
      return;
    }

    HttpServletResponse httpResponse = (HttpServletResponse) response;
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

    boolean roleExists = userRoles.stream()
        .anyMatch(r -> r.getName().toLowerCase(Locale.getDefault()).equals(roleName));
    if (roleExists) {
      chain.doFilter(request, response);
    } else {
      httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
    }
  }
}
