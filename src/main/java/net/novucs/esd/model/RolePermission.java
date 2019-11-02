package net.novucs.esd.model;

import java.util.Objects;
import net.novucs.esd.orm.Column;
import net.novucs.esd.orm.Table;

@Table
public final class RolePermission {

  @Column(primary = true)
  private Integer id;

  @Column(foreign = Role.class)
  private Integer roleId;

  @Column
  private String name;

  public RolePermission() {
    // This constructor is intentionally empty.
  }

  public RolePermission(Integer roleId, String name) {
    this.roleId = roleId;
    this.name = name;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getRoleId() {
    return roleId;
  }

  public void setRoleId(Integer roleId) {
    this.roleId = roleId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RolePermission that = (RolePermission) o;
    return Objects.equals(getId(), that.getId())
        && Objects.equals(getRoleId(), that.getRoleId())
        && Objects.equals(getName(), that.getName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getRoleId(), getName());
  }
}
