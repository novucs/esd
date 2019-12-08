package net.novucs.esd.util;

import net.novucs.esd.model.Application;
import net.novucs.esd.model.User;

public class ManageApplicationResult {

  private final Application application;
  private final User user;

  public ManageApplicationResult(Application application, User user) {
    this.application = application;
    this.user = user;
  }

  public Application getApplication() {
    return application;
  }

  public User getUser() {
    return user;
  }
}
