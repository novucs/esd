package net.novucs.esd.notifications;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import net.novucs.esd.model.Notification;

@RequestScoped
public class NotificationService {

  @Inject
  private NotificationSessionHandler handler;

  public void sendNotification(Notification notification) throws SQLException {
    try {
      handler.sendNotification(notification);
    } catch (IOException e) {
      Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
          "Notification service unable to send notification." + e.getMessage());
    }
  }
}
