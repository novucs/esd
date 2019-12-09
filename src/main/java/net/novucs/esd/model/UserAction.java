package net.novucs.esd.model;

import java.util.Objects;
import net.novucs.esd.orm.Column;
import net.novucs.esd.orm.Table;

@Table
public final class UserAction {

  @Column(primary = true)
  private Integer id;

  @Column(foreign = User.class, unique = "user_action_uq")
  private Integer userId;

  @Column(foreign = Action.class, unique = "user_action_uq")
  private Integer actionId;

  public UserAction() {
    this.actionId = actionId;
  }

  public UserAction(Integer userId, Integer actionId) {
    this.id = id;
    this.userId = userId;
    this.actionId = actionId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserAction that = (UserAction) o;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
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

  public Integer getActionId() {
    return actionId;
  }

  public void setActionId(Integer actionId) {
    this.actionId = actionId;
  }
}
