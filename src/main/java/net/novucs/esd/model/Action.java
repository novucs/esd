package net.novucs.esd.model;

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

  public ZonedDateTime getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(ZonedDateTime date_created) {
    this.dateCreated = date_created;
  }

  public Action(Integer pounds, Integer pence, ZonedDateTime completeBy,
      ZonedDateTime dateCreated) {
    this.pounds = pounds;
    this.pence = pence;
    this.completeBy = completeBy;
    this.dateCreated = dateCreated;
  }

  public Action() {
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

  public void setCompleteBy(ZonedDateTime complete_by) {
    this.completeBy = complete_by;
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
