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
  private ZonedDateTime complete_by;

  @Column
  private ZonedDateTime date_created;

  public ZonedDateTime getDateCreated() {
    return date_created;
  }

  public void setDateCreated(ZonedDateTime date_created) {
    this.date_created = date_created;
  }

  public Action(Integer pounds, Integer pence, ZonedDateTime complete_by,
      ZonedDateTime date_created) {
    this.id = id;
    this.pounds = pounds;
    this.pence = pence;
    this.complete_by = complete_by;
    this.date_created = date_created;
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
    return complete_by;
  }

  public void setCompleteBy(ZonedDateTime complete_by) {
    this.complete_by = complete_by;
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
