package net.novucs.esd.model;

import net.novucs.esd.orm.Column;
import net.novucs.esd.orm.Table;

@Table
public class Membership {

  @Column(primary = true)
  private Integer id;

  @Column(foreign = User.class)
  private Integer userId;

  // Pounds and pence integers make up balance, we do not want to store monetary values as floating
  // point numbers to prevent the possibility of rounding errors.
  @Column
  private Integer pounds;

  @Column
  private Integer pence;

  @Column
  private String status;

  public Membership() {
    // This constructor is intentionally empty.
  }

  public Membership(Integer userId, Integer pounds, Integer pence, String status) {
    this.userId = userId;
    this.pounds = pounds;
    this.pence = pence;
    this.status = status;
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

  public Integer getPounds() {
    return pounds;
  }

  public void setPounds(Integer pounds) {
    this.pounds = pounds;
  }

  public Integer getPence() {
    return pence;
  }

  public void setPence(Integer pence) {
    this.pence = pence;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}