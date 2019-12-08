package net.novucs.esd.test.controller.member;

import static junit.framework.TestCase.assertTrue;

import net.novucs.esd.controllers.member.MemberDashboardServlet;
import org.junit.Test;

public class TestMemberDashboardServlet {

  @Test
  public void testServletInfo() {
    // Given
    MemberDashboardServlet servlet = new MemberDashboardServlet();

    // When
    String servletInfo = servlet.getServletInfo();

    // Assert
    assertTrue("getServletInfo must match the class name.",
        servletInfo.equalsIgnoreCase(servlet.getClass().getSimpleName()));
  }
}
