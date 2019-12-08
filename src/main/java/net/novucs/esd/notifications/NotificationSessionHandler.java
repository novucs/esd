package net.novucs.esd.notifications;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.websocket.Session;
import net.novucs.esd.model.Notification;
import net.novucs.esd.orm.Dao;

@Singleton
public class NotificationSessionHandler {

  private final Map<String, NotificationSession> sessions = new ConcurrentHashMap<>();

  @Inject
  private Dao<Notification> notificationDao;

  public void addSession(String sessionId, NotificationSession session) {
    sessions.put(sessionId, session);
  }

  public void removeSession(Session session) {
    sessions.remove(session.getId());
  }

  public NotificationSession getSession(String sessionId) {
    return sessions.get(sessionId);
  }

  public void sendNotification(Notification notification)
      throws IOException, SQLException {
    this.sendNotification(notification, false);
  }

  public void sendNotification(Notification notification, boolean polling)
      throws IOException, SQLException {

    NotificationSession recipientSession = sessions.values().stream().filter(s ->
        s.getUserId() == notification.getRecipientId()).findFirst().orElse(null);

    synchronized (this) {
      if (recipientSession == null
          || notification.getRecipientId() == notification.getSenderId()
          && !polling) {
        notificationDao.insert(notification);
      } else {
        JsonObjectBuilder builder = Json.createObjectBuilder()
            .add("message", notification.getMessage())
            .add("type", notification.getType().name());

        JsonObject notificationJson = builder.build();
        this.sendNotificationToSession(recipientSession.getSession(), notificationJson);
      }
    }
  }

  private void sendNotificationToSession(Session session, JsonObject message) throws IOException {
    session.getBasicRemote().sendText(message.toString());
  }
}
