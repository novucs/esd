package net.novucs.esd.lifecycle;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import net.novucs.esd.model.Role;
import net.novucs.esd.model.User;

/**
 * The type Session.
 */
public class Session {

  public static final String ATTRIBUTE_NAME = "session";
  private final Stack<String> errors = new Stack<>();
  private List<Role> roles = new ArrayList<>();
  private Map<String, Object> filters = new ConcurrentHashMap<>();
  private User user;

  /**
   * Push error.
   *
   * @param message the message
   */
  public void pushError(String message) {
    errors.push(message);
  }

  /**
   * Gets errors.
   *
   * @return the errors
   */
  public List<String> getErrors() {
    List<String> errors = new ArrayList<>();
    while (!this.errors.isEmpty()) {
      errors.add(this.errors.pop());
    }
    return errors;
  }

  /**
   * Gets user.
   *
   * @return the user
   */
  public User getUser() {
    return user;
  }

  /**
   * Sets user.
   *
   * @param user the user
   */
  public void setUser(User user) {
    this.user = user;
  }

  /**
   * Get the user roles.
   *
   * @return List of roles
   */
  public List<Role> getRoles() {
    return roles;
  }

  /**
   * Get the user role names.
   *
   * @return List of role names
   */
  public List<String> getRoleNames() {
    return this.getRoles().stream().map(r -> r.getName().toLowerCase(Locale.UK))
        .collect(Collectors.toList());
  }

  /**
   * Check if the role name exists within the users roles.
   *
   * @return Yes / No
   */
  public Boolean hasRole(String name) {
    return this.getRoleNames().contains(name.toLowerCase(Locale.UK));
  }

  /**
   * Sets the user roles.
   *
   * @param roles The user roles
   */
  public void setRoles(List<Role> roles) {
    this.roles = roles;
  }


  /**
   * Add a filter to the session e.g. page size
   *
   * @param filterName Name of the filter
   * @param filterValue Value of the filter
   */
  public void setFilter(String filterName, Object filterValue) {
    this.filters.put(filterName, filterValue);
  }

  /**
   * Add a filter to the session e.g. page size
   *
   * @param filterName Name of the filter to remove
   */
  public void removeFilter(String filterName) {
    this.filters.remove(filterName);
  }


  /**
   * Add a filter to the session e.g. page size
   *
   * @param filterName Name of the filter
   */
  public Object getFilter(String filterName) {
    try{
      return this.filters.get(filterName);
    } catch (Exception e){
      return null;
    }
  }


  /**
   * Gets session.
   *
   * @param request the request
   * @return the session
   */
  public static Session fromRequest(HttpServletRequest request) {
    HttpSession httpSession = request.getSession(false);

    // If a session doesn't exist, request GlassFish make a new one
    if (httpSession == null) {
      httpSession = request.getSession(true);
    }

    // Check if we have a session handler in our session
    Session sessionHandler;
    if (httpSession.getAttribute(ATTRIBUTE_NAME) == null) {
      // Create a session
      sessionHandler = new Session();
      httpSession.setAttribute(ATTRIBUTE_NAME, sessionHandler);
    } else {
      // Invoke our old session
      sessionHandler = (Session) httpSession.getAttribute(ATTRIBUTE_NAME);
    }

    return sessionHandler;
  }
}
