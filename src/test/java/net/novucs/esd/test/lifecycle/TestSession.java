package net.novucs.esd.test.lifecycle;

import java.time.ZonedDateTime;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.User;
import net.novucs.esd.util.DateUtil;
import net.novucs.esd.util.Password;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestSession {

  private transient Session session;

  @Before
  public void initSessionTest() {
    session = new Session();
  }

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

  @Test
  public void testSessionHandlesUser() {
    DateUtil dateUtil = new DateUtil();
    ZonedDateTime dateOfBirth = dateUtil.getDateFromString("2000-01-01");

    User u = new User(
        "bob",
        "bob@bob.net",
        Password.fromPlaintext("bob"),
        "bob lane",
        dateOfBirth,
        "great");
    session.setUser(u);

    Assert.assertEquals(
        "Session must return the correct user.",
        "bob",
        session.getUser().getName()
    );
  }
}
