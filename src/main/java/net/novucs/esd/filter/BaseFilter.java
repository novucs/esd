package net.novucs.esd.filter;

public class BaseFilter {

  protected Boolean pathIsExcluded(String path) {
    return path == null || path.equals("/app/") || path.contains("/register")
        || path.contains("/login") || path.matches(".*(css|jpg|jpeg|png|gif|js)");
  }
}
