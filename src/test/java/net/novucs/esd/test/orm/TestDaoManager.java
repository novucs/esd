package net.novucs.esd.test.orm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import net.novucs.esd.model.User;
import net.novucs.esd.orm.ConnectionSource;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Where;
import org.junit.Before;
import org.junit.Test;

public class TestDaoManager {

  private static final String ALICE = "alice";
  private static final String BOB = "bob";
  private static ConnectionSource connectionSource;
  private static Dao<User> userDao;

  @Before
  public void setUp() throws SQLException {
    String dbUrl = "jdbc:derby:memory:testDB;create=true";
    String dbUser = "impact";
    String dbPass = "derbypass";
    connectionSource = new ConnectionSource(dbUrl, dbUser, dbPass);
  }

  @Test
  public void testCreateTable() throws SQLException {
    // Given
    userDao = new Dao<>(connectionSource, User.class);
    userDao.createTable();

    // Assert
    try (PreparedStatement statement = connectionSource.getConnection().prepareStatement(
        "SELECT TABLEID FROM SYS.SYSTABLES WHERE TABLENAME=?"
    )) {
      statement.setString(1, User.class.getSimpleName());
      statement.execute();
      try (ResultSet rs = statement.getResultSet()) {
        int rowCount = rs.getFetchSize();
        assertEquals(
            "Expected one table to be create as " + User.class.getSimpleName()
                + ", none were found.",
            1,
            rowCount
        );
      }
    }
  }

  @Test
  public void testSelectAll() throws SQLException {
    // Check two bobs are inserted and can be fetched.
    userDao.insert(new User(BOB));
    userDao.insert(new User(BOB));
    userDao.insert(new User(BOB));
    userDao.insert(new User(ALICE));
    List<User> allUsers = userDao.select()
        .where(new Where()
            .eq("name", BOB)
            .and()
            .eq("id", 1)
            .or()
            .eq("id", 2)
        )
        .limit(2)
        .all();
    assertEquals("Two users should be selected", 2, allUsers.size());
  }

  @Test
  public void testSelectById() throws SQLException {
    userDao.insert(new User(BOB));
    User bob = userDao.selectById(1);
    assertEquals("The user bob should be selected by ID 1", BOB, bob.getName());
  }

  @Test
  public void testUpdate() throws SQLException {
    User bob = new User(BOB);
    userDao.insert(bob);
    bob.setName(ALICE);
    userDao.update(bob);
    User alice = userDao.selectById(bob.getId());
    assertEquals("The user bob should be renamed to alice", ALICE, alice.getName());
  }

  @Test
  public void testDeletion() throws SQLException {
    User bob = new User(BOB);
    userDao.insert(bob);
    userDao.delete(bob);
    User deletedUser = userDao.select().where(new Where().eq("id", bob.getId())).first();
    assertNull("The user bob should be deleted", deletedUser);
  }
}
