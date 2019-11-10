package net.novucs.esd.test.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.novucs.esd.util.Password;
import org.junit.Test;

/**
 * The type Test password.
 */
public class TestPassword {

  private static final String TEST_PASSWORD = "password123";

  /**
   * Test valid password.
   */
  @Test
  public void testValidPassword() {
    Password password = Password.fromPlaintext(TEST_PASSWORD);
    assertTrue("Matching passwords must authenticate",
        password.authenticate(TEST_PASSWORD));
  }

  /**
   * Test invalid password.
   */
  @Test
  public void testInvalidPassword() {
    Password password = Password.fromPlaintext(TEST_PASSWORD);
    assertFalse("Miss-matching passwords must not authenticate",
        password.authenticate("not a valid password"));
  }

  /**
   * Test password serializes.
   */
  @Test
  public void testPasswordSerializes() {
    Password password = Password.fromPlaintext(TEST_PASSWORD);
    String hashAndSalt = password.toHashAndSalt();
    Password generated = Password.fromHashAndSalt(hashAndSalt);
    assertTrue("Password should authenticate even when serialized then deserialized",
        generated.authenticate(TEST_PASSWORD));
  }
}
