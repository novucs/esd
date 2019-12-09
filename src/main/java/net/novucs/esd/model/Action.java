package net.novucs.esd.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;
import net.novucs.esd.orm.Column;
import net.novucs.esd.orm.Table;

@Table
public class Action {

  @Column(primary = true)
  private Integer id;

  @Column
  private Integer pounds;

  @Column
  private Integer pence;

  @Column
  private ZonedDateTime completeBy;

  @Column
  private ZonedDateTime dateCreated;

  @Column
  private Integer amountPaid;

  public ZonedDateTime getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(ZonedDateTime dateCreated) {
    this.dateCreated = dateCreated;
  }

  public int getAmountPaid() {
    return amountPaid;
  }

  public void setAmountPaid(int amountPaid) {
    this.amountPaid = amountPaid;
  }

  public Action(Integer pounds, Integer pence, ZonedDateTime completeBy,
      ZonedDateTime dateCreated) {
    this.pounds = pounds;
    this.pence = pence;
    this.completeBy = completeBy;
    this.dateCreated = dateCreated;
    this.amountPaid = 0;
  }


  public Action() {
    // This constructor is intentionally empty.
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getPounds() {
    return pounds;
  }

  public BigDecimal getBalance() {
    return BigDecimal.valueOf(pounds + (pence / 100f));
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

  public ZonedDateTime getCompleteBy() {
    return completeBy;
  }

  public void setCompleteBy(ZonedDateTime completeBy) {
    this.completeBy = completeBy;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Action action = (Action) o;
    return id.equals(action.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
