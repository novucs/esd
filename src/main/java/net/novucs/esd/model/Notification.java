package net.novucs.esd.model;

import java.util.Objects;
import net.novucs.esd.orm.Column;
import net.novucs.esd.orm.Table;

@Table
public class Notification {

  @Column(primary = true)
  private Integer id;

  @Column
  private String message;

  @Column(foreign = User.class)
  private Integer senderId;

  @Column(foreign = User.class)
  private Integer recipientId;

  /**
   * Instantiates a new Notifications.
   */
  public Notification() {
    // This constructor is intentionally empty.
  }

  public Notification(String message, Integer senderId, Integer recipientId) {
    this.message = message;
    this.senderId = senderId;
    this.recipientId = recipientId;
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
