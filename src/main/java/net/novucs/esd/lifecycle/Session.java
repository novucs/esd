package net.novucs.esd.lifecycle;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import net.novucs.esd.model.User;

/**
 * The type Session.
 */
public class Session {

  private final Stack<String> errors = new Stack<>();
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
}
