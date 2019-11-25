package net.novucs.esd.notifications;

import javax.websocket.Session;
import net.novucs.esd.model.Notification;

public class NotificationSession {

  private Session session;

  private int userId;

  public NotificationSession(){

  }

  public NotificationSession(Session session, int userId){
    this.session = session;
    this.userId = userId;
  }

  public Session getSession() {
    return session;
  }

  public void setSession(Session notificationSession) {
    this.session = notificationSession;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

}
