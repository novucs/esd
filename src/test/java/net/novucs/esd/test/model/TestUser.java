package net.novucs.esd.test.model;

import net.novucs.esd.model.User;
import net.novucs.esd.orm.ConnectionSource;
import net.novucs.esd.orm.Dao;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.sql.SQLException;

public class TestUser {
    private static final String SHANIQUA = "shaniqua";
    private transient Dao<User> userDao;

    @Before
    public void setUp() throws SQLException {
        String dbUrl = "jdbc:derby:memory:testDB;create=true";
        String dbUser = "impact";
        String dbPass = "derbypass";

        ConnectionSource connectionSource = new ConnectionSource(dbUrl, dbUser, dbPass);
        userDao = new Dao<>(connectionSource, User.class);
        userDao.createTable();
    }

    @Test
    public void testUserModelHasName() {
        // Given
        User shaniqua = new User(SHANIQUA);

        // Assert
        assertEquals("The name of the user should be set to 'shaniqua'", SHANIQUA, shaniqua.getName());
    }
}