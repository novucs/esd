package net.novucs.esd.filter;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Role;

public class MemberFilter extends BaseFilter implements Filter {

  @Resource(lookup = "java:app/AppName")
  private transient String appName;

  @Override
  public void init(FilterConfig filterConfig) {
  }

  @Override
  public void destroy() {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    String path = httpRequest.getRequestURI();

    if (super.pathIsExcluded(path)) {
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
          .anyMatch(r -> r.getName().toLowerCase(Locale.getDefault()).equals("member"));

      if (roleExists) {
        chain.doFilter(request, response);
      } else {
        // comment
        httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
      }
    }
  }
}
