package net.novucs.esd.test;

import java.util.logging.Logger;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public final class TestRunner {

  private static final Logger LOGGER = Logger.getLogger(TestRunner.class.getName());

  private TestRunner() {
  }

  public static void main(String[] args) {
    Result result = JUnitCore.runClasses(TestJunit.class);

    for (Failure failure : result.getFailures()) {
      LOGGER.severe(failure.toString());
    }

    LOGGER.info(result.wasSuccessful() ? "All tests passed" : "Some tests failed");
  }
}
