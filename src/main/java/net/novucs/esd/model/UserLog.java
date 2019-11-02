package net.novucs.esd.model;

import java.util.Objects;
import net.novucs.esd.orm.Column;
import net.novucs.esd.orm.Table;

@Table
public final class UserLog {

  @Column(primary = true)
  private Integer id;

  @Column(foreign = User.class)
  private Integer userId;

  @Column
  private String message;

  @Column
  private String ip;

  public UserLog() {
    // This constructor is intentionally empty.
  }

  public UserLog(Integer userId, String message, String ip) {
    this.userId = userId;
    this.message = message;
    this.ip = ip;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserLog userLog = (UserLog) o;
    return Objects.equals(getId(), userLog.getId())
        && Objects.equals(getUserId(), userLog.getUserId())
        && Objects.equals(getMessage(), userLog.getMessage())
        && Objects.equals(getIp(), userLog.getIp());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getUserId(), getMessage(), getIp());
  }
}
