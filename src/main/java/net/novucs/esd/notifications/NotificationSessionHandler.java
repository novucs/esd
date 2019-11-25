package net.novucs.esd.notifications;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.faces.bean.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.Session;
import net.novucs.esd.model.Notification;
import net.novucs.esd.orm.Dao;

@ApplicationScoped
public class NotificationSessionHandler {

  private final Map<String, NotificationSession> sessions = new ConcurrentHashMap<>();

  @Inject
  private Dao<Notification> notificationDao;

  public void addSession(String userId, NotificationSession session) {
    sessions.put(userId, session);
  }

  public void removeSession(Session session) {
    Optional<NotificationSession> toRemove = sessions.values().stream().filter(
        s -> s.getUserId() == Integer.parseInt(session.getId())).findFirst();
    sessions.remove(toRemove.get().getUserId());
  }

  public NotificationSession getSession(String sessionId){
    return sessions.get(sessionId);
  }

  public void sendNotification(Notification notification) throws IOException, SQLException {
    NotificationSession recipientSession = sessions.values().stream().filter(s ->
        s.getUserId() == notification.getRecipientId()).findFirst().orElse(null);

    if(recipientSession != null){
      JsonObject notificationJson = Json.createObjectBuilder().add("message",
          notification.getMessage()).build();
      this.sendNotificationToSession(recipientSession.getSession(), notificationJson);
    } else {
      notificationDao.insert(notification);
    }
  }

  private void sendNotificationToSession(Session session, JsonObject message) throws IOException {
    session.getBasicRemote().sendText(message.toString());
  }
}
