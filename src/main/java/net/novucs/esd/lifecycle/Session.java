package net.novucs.esd.lifecycle;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import net.novucs.esd.model.User;

public class Session {

  private final Stack<String> errors = new Stack<>();
  private User user;

  public void pushError(String message) {
    errors.push(message);
  }

  public List<String> getErrors() {
    List<String> errors = new ArrayList<>();
    while (!this.errors.isEmpty()) {
      errors.add(this.errors.pop());
    }
    return errors;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
