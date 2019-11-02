package net.novucs.esd.model;

import java.util.Objects;
import net.novucs.esd.orm.Column;
import net.novucs.esd.orm.Table;

@Table
public final class UserRole {

  @Column(primary = true)
  private Integer id;

  @Column(foreign = User.class)
  private Integer userId;

  @Column(foreign = Role.class)
  private Integer roleId;

  public UserRole() {
    // This constructor is intentionally empty.
  }

  public UserRole(Integer userId, Integer roleId) {
    this.userId = userId;
    this.roleId = roleId;
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

  public Integer getRoleId() {
    return roleId;
  }

  public void setRoleId(Integer roleId) {
    this.roleId = roleId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserRole userRole = (UserRole) o;
    return Objects.equals(getId(), userRole.getId())
        && Objects.equals(getUserId(), userRole.getUserId())
        && Objects.equals(getRoleId(), userRole.getRoleId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getUserId(), getRoleId());
  }
}
