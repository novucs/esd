package net.novucs.esd.handler;

import java.security.SecureRandom;
import net.novucs.esd.model.User;
import java.util.UUID;

public class SessionHandler {

  public enum SESSION_RESPONSE {
    INVALID_SESSION,
    INVALID_SESSION_NOUSER,
    INVALID_SESSION_EXPIRED,
    VALID_SESSION
  }

  public SessionHandler(String sessionId) {
    // Do stuff
  }

  public void validateUserSession(User user) {
    // Check if the user has a session

    // Check if the received Request has a cookie with the session in

    // Check if that session has expired (20 minutes)

    // Not expired: call updateUserSession(user, true)
  }

  public void updateUserSession(User user, boolean shouldCreateSession) {
    // Check if a session exists
    // -> If no session, create one if shouldCreateSession
    // -> If has session, update the expiry time in the database table to be 20min from current unix ts
  }

  public boolean doesSessionExist(String sessionId) {
    // Check if the session exists by doing a count lookup on the 'UserSession' model
    // If the session exists and isn't expired, get our user, otherwise return enum
    return false;
  }
}
