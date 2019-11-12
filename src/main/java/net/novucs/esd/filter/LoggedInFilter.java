package net.novucs.esd.filter;

import java.io.IOException;
import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.lifecycle.Session;

public class LoggedInFilter extends BaseFilter {

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

    String path = ((HttpServletRequest) request).getRequestURI();
    if (super.isPathExcluded(path)) {
      chain.doFilter(request, response); // Just continue chain.
      return;
    }

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    Session sessionHandler = Session.fromRequest(httpRequest);

    if (sessionHandler.getUser() == null) {
      httpResponse.sendRedirect("/" + appName + "/login");
      return;
    }

    chain.doFilter(request, response);
  }
}
