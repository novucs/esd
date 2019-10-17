package net.novucs.esd.test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.novucs.esd.ExampleServerlet;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

public class TestExampleServerlet {

  @Test
  public void testRequestGetsMapAttribute()
      throws ServletException, IOException, ReflectiveOperationException {
    // Given
    ExampleServerlet serverlet = new ExampleServerlet();
    Field appNameField = serverlet.getClass().getDeclaredField("appName");
    appNameField.setAccessible(true);
    appNameField.set(serverlet, "dummyApp");
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    // When
    Mockito.when(request.getRequestDispatcher("/example.jsp")).thenAnswer(
        (Answer<RequestDispatcher>) invocation -> Mockito.mock(RequestDispatcher.class));
    serverlet.doGet(request, response);

    // Assert
    Mockito.verify(request, Mockito.times(1))
        .setAttribute(Matchers.any(String.class), Matchers.any(Map.class));
  }

  @Test
  public void testExampleInServerlettInfo() {
    // Given
    ExampleServerlet serverlet = new ExampleServerlet();

    // When
    String serverletInfo = serverlet.getServletInfo();

    // Assert
    Assert.assertTrue("Example serverlett info must contain the key word example",
        serverletInfo.contains("example"));
  }
}
