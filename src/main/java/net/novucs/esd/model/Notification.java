package net.novucs.esd.model;

import java.util.Objects;
import net.novucs.esd.orm.Column;
import net.novucs.esd.orm.Table;

@Table
public final class Notification {

  @Column(primary = true)
  private Integer id;

  @Column
  private String message;

  @Column(foreign = User.class)
  private Integer senderId;

  @Column(foreign = User.class)
  private Integer recipientId;

  @Column
  private String type;

  /**
   * Instantiates a new Notifications.
   */
  public Notification() {
    // This constructor is intentionally empty.
  }

  public Notification(String message, Integer senderId, Integer recipientId,
      NotificationType type) {
    this.message = message;
    this.senderId = senderId;
    this.recipientId = recipientId;
    this.type = type.name();
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Integer getSenderId() {
    return senderId;
  }

  public void setSenderId(Integer senderId) {
    this.senderId = senderId;
  }

  public Integer getRecipientId() {
    return recipientId;
  }

  public void setRecipientId(Integer recipientId) {
    this.recipientId = recipientId;
  }

  public NotificationType getType() {
    return NotificationType.valueOf(this.type);
  }

  public void setType(NotificationType type) {
    this.type = type.name();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Notification that = (Notification) o;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
