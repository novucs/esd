package net.novucs.esd.model;

import net.novucs.esd.orm.Column;
import net.novucs.esd.orm.Table;

@Table
public class UserRole {

  @Column(primary = true)
  private transient Integer id;

  @Column(foreign = User.class)
  private transient Integer userId;

  @Column(foreign = Role.class)
  private transient Integer roleId;

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
}
