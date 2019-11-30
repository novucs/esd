package net.novucs.esd.lifecycle;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import net.novucs.esd.webservices.AddressLookup;

@ApplicationPath("/api")
public class ApplicationConfig extends Application {
  
  @Override
  public Set<Class<?>> getClasses() {
    final Set<Class<?>> classes = new HashSet<>();
    classes.add(AddressLookup.class);
    return classes;
  }
}
