package net.novucs.esd.test.util;

import java.time.ZonedDateTime;
import net.novucs.esd.model.Role;
import net.novucs.esd.model.User;
import net.novucs.esd.util.DateUtil;
import net.novucs.esd.util.Password;

public final class TestDummyDataUtil {

  private TestDummyDataUtil() {
    // Intentionally left empty & private.
  }

  public static User getDummyUser() {
    return new User(
        "testuser",
        "dummy-testuser",
        "testuser@example.com",
        Password.fromPlaintext("test_pass"),
        "Line 1,Line 2,City,County,Postcode",
        new DateUtil().getDateFromString("2000-01-01"),
        1
    );
  }

  public static User getDummyBobUser() {
    DateUtil dateUtil = new DateUtil();
    ZonedDateTime dateOfBirth = dateUtil.getDateFromString("2000-01-01");

    return new User(
        "bob",
        "bob-testuser",
        "bob@bob.net",
        Password.fromPlaintext("bob"),
        "bob lane",
        dateOfBirth,
        1
    );
  }

  public static User getDummyAdminUser() {
    DateUtil dateUtil = new DateUtil();
    ZonedDateTime dateOfBirth = dateUtil.getDateFromString("1990-01-01");
    return new User(
        "admin",
        "admin-testuser",
        "admin@admin.net",
        Password.fromPlaintext("bob"),
        "admin lane",
        dateOfBirth,
        1
    );
  }

  public static Role getAdminRole() {
    return new Role("Administrator");
  }

}
