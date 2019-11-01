package net.novucs.esd.handler;

import java.security.SecureRandom;
import net.novucs.esd.model.User;
import java.util.UUID;

public class Session {

  private static final String alphabet =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  private static SecureRandom random = new SecureRandom();

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

  /**
   * Generate a Session Key
   *
   * @return String
   */
  private String generateSessionKey() {
    StringBuilder key = new StringBuilder(32);
    for (int i = 0; i < 32; i++) {
      key.append(alphabet.charAt(random.nextInt(alphabet.length())));
    }
    return key.toString();
  }
}
