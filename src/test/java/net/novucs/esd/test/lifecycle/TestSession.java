package net.novucs.esd.test.lifecycle;

import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.User;
import net.novucs.esd.test.TestDummyDataUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * The type Test session.
 */
public class TestSession {

  private transient Session session;

  /**
   * Init session test.
   */
  @Before
  public void initSessionTest() {
    session = new Session();
  }

  /**
   * Test session handles errors.
   */
  @Test
  public void testSessionHandlesErrors() {
    for (int i = 0; i < 5; i++) {
      session.pushError("Testing error " + i);
    }
    Assert.assertEquals(
        "Errors must be able to pushed to the stack.",
        5,
        session.getErrors().size()
    );
  }

  /**
   * Test session handles user.
   */
  @Test
  public void testSessionHandlesUser() {
    User u = TestDummyDataUtils.getDummyBobUser();
    session.setUser(u);

    Assert.assertEquals(
        "Session must return the correct user.",
        "bob",
        session.getUser().getName()
    );
  }
}
