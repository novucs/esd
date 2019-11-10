package net.novucs.esd.test.model;

import static net.novucs.esd.test.util.TestUtils.createTestDaoManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.novucs.esd.lifecycle.DatabaseLifecycle;
import net.novucs.esd.orm.DaoManager;
import org.junit.Assert;
import org.junit.Test;

/**
 * The type Test models.
 */
public class TestModels {

  /**
   * Test models initialise.
   *
   * @throws SQLException the sql exception
   */
  @Test
  public void testModelsInitialise() throws SQLException {
    DaoManager daoManager = createTestDaoManager();
    daoManager.init(DatabaseLifecycle.MODEL_CLASSES);
    List<Class<?>> registered = new ArrayList<>();
    for (Class<?> modelClass : DatabaseLifecycle.MODEL_CLASSES) {
      if (daoManager.get(modelClass) != null) {
        registered.add(modelClass);
      }
    }
    Assert.assertEquals("DAO Manager must have initialised all the provided models "
        + "successfully", DatabaseLifecycle.MODEL_CLASSES, registered);
  }
}
