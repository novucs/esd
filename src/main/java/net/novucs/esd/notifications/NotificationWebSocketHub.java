package net.novucs.esd.notifications;

import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import net.novucs.esd.model.Notification;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Where;

@ApplicationScoped
@ServerEndpoint("/notifications/{userId}")
public class NotificationWebSocketHub {

  @Inject
  private NotificationSessionHandler sessionHandler;

  @Inject
  private Dao<Notification> notificationDao;

  @OnOpen
  public void open(@PathParam("userId") String userId, Session session) throws SQLException {
    try {
      NotificationSession notificationSession = new NotificationSession(session,
          Integer.parseInt(userId));
      sessionHandler.addSession(session.getId(), notificationSession);
      // Poll the notifications in the database and see if any of them pertain to this session.
      pollNotificationQueue(userId);
    } catch (NumberFormatException e) {
      Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Could not recognise user ID"
          + "for websocket.");
    }
  }

  @OnClose
  public void close(Session session) {
    sessionHandler.removeSession(session);
  }

  @OnError
  public void onError(Throwable error) {
    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
        error.toString());
  }

  @OnMessage
  public void handleNotification(String notificationJSON, Session session) {
    try (JsonReader reader = Json.createReader(new StringReader(notificationJSON))) {
      JsonObject jsonNotification = reader.readObject();
      if (jsonNotification != null) {
        String message = jsonNotification.getString("message");
        int recipientId = Integer.parseInt(jsonNotification.getString("recipientId"));
        int sender = sessionHandler.getSession(session.getId()).getUserId();
        Notification notification = new Notification();
        notification.setSenderId(sender);
        notification.setRecipientId(recipientId);
        notification.setMessage(message);
        sessionHandler.sendNotification(notification);
      }
    } catch (IOException | SQLException e) {
      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
          e.toString());
    }
  }

  private void pollNotificationQueue(String userId) throws SQLException {
    List<Notification> notifications = notificationDao.select().where(new Where().eq(
        "recipient_id", userId)).all();

    if (!notifications.isEmpty()) {
      List<Notification> notificationsToDelete = new ArrayList<>();
      try {
        for (Notification notification : notifications) {
          sessionHandler.sendNotification(notification);
          notificationsToDelete.add(notification);
        }
      } catch (IOException e) {
        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
            "Failed to send notifications.", e);
      } finally {
        notificationDao.delete(notificationsToDelete);
      }
    }
  }
}