package net.novucs.esd.model;

import java.time.ZonedDateTime;
import net.novucs.esd.orm.Column;
import net.novucs.esd.orm.Table;

@Table
public class UserSession {

  @Column(primary = true)
  private Integer id;

  @Column(foreign = User.class)
  private Integer userId;

  @Column
  private String hash;

  @Column
  private ZonedDateTime expiryDate;

  public UserSession() {
    // This constructor is intentionally empty.
  }

  public UserSession(Integer userId, String hash, ZonedDateTime expiryDate) {
    this.userId = userId;
    this.hash = hash;
    this.expiryDate = expiryDate;
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

  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  public ZonedDateTime getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(ZonedDateTime expiryDate) {
    this.expiryDate = expiryDate;
  }
}
