package net.novucs.esd.model;

import java.math.BigDecimal;
import java.util.Objects;
import net.novucs.esd.orm.Column;
import net.novucs.esd.orm.Table;

@Table
public final class Membership {

  @Column(primary = true)
  private Integer id;

  @Column(foreign = User.class)
  private Integer userId;

  // Pounds and pence integers make up balance, we do not want to store
  // monetary values as floating point numbers to prevent the possibility
  // of rounding errors.
  @Column
  private Integer pounds;

  @Column
  private Integer pence;

  @Column
  private String status;

  public Membership() {
    // This constructor is intentionally empty.
  }

  public Membership(Integer userId, BigDecimal balance, String status) {
    this.userId = userId;
    this.status = status;
    setBalance(balance);
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

  public BigDecimal getBalance() {
    return BigDecimal.valueOf(pounds + (pence / 100f));
  }

  public void setBalance(BigDecimal balance) {
    double doubleBalance = balance.doubleValue();
    pounds = (int) doubleBalance;
    pence = (int) ((doubleBalance - pounds) * 100);
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Membership that = (Membership) o;
    return Objects.equals(getId(), that.getId())
        && Objects.equals(getUserId(), that.getUserId())
        && Objects.equals(pounds, that.pounds)
        && Objects.equals(pence, that.pence)
        && Objects.equals(getStatus(), that.getStatus());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getUserId(), pounds, pence, getStatus());
  }
}
