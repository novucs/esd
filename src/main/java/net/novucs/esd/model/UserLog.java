package net.novucs.esd.model;

import java.util.Objects;
import net.novucs.esd.orm.Column;
import net.novucs.esd.orm.Table;

/**
 * The type User log.
 */
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

  /**
   * Instantiates a new User log.
   */
  public UserLog() {
    // This constructor is intentionally empty.
  }

  /**
   * Instantiates a new User log.
   *
   * @param userId  the user id
   * @param message the message
   * @param ip      the ip
   */
  public UserLog(Integer userId, String message, String ip) {
    this.userId = userId;
    this.message = message;
    this.ip = ip;
  }

  /**
   * Gets id.
   *
   * @return the id
   */
  public Integer getId() {
    return id;
  }

  /**
   * Sets id.
   *
   * @param id the id
   */
  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * Gets user id.
   *
   * @return the user id
   */
  public Integer getUserId() {
    return userId;
  }

  /**
   * Sets user id.
   *
   * @param userId the user id
   */
  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  /**
   * Gets message.
   *
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /**
   * Sets message.
   *
   * @param message the message
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * Gets ip.
   *
   * @return the ip
   */
  public String getIp() {
    return ip;
  }

  /**
   * Sets ip.
   *
   * @param ip the ip
   */
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
    return Objects.equals(id, userLog.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
