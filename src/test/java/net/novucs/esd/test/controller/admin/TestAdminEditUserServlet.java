package net.novucs.esd.test.controller.admin;

import static junit.framework.TestCase.assertTrue;

import net.novucs.esd.controllers.admin.AdminEditUserServlet;
import org.junit.Test;

public class TestAdminEditUserServlet {

  /*
   TODO: Refactor this to test for the implementation of AdminEditUserServlet
   */

  @Test
  public void testServletInfo() {
    // Given
    AdminEditUserServlet servlet = new AdminEditUserServlet();

    // When
    String servletInfo = servlet.getServletInfo();

    // Assert
    assertTrue("getServletInfo must match the class name.",
        servletInfo.equalsIgnoreCase(servlet.getClass().getSimpleName()));
  }
}
