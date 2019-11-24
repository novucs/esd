package net.novucs.esd.model;

import java.util.Objects;
import net.novucs.esd.orm.Column;
import net.novucs.esd.orm.Table;

/**
 * The type Role permission.
 */
@Table
public final class RolePermission {

  @Column(primary = true)
  private Integer id;

  @Column(foreign = Role.class)
  private Integer roleId;

  @Column(unique = "name_uq")
  private String name;

  /**
   * Instantiates a new Role permission.
   */
  public RolePermission() {
    // This constructor is intentionally empty.
  }

  /**
   * Instantiates a new Role permission.
   *
   * @param roleId the role id
   * @param name   the name
   */
  public RolePermission(Integer roleId, String name) {
    this.roleId = roleId;
    this.name = name;
  }

  /**
   * Gets id.
   *
   * @return the id
   */
  public Integer getId() {
    return id;
  }

  /**
   * Sets id.
   *
   * @param id the id
   */
  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * Gets role id.
   *
   * @return the role id
   */
  public Integer getRoleId() {
    return roleId;
  }

  /**
   * Sets role id.
   *
   * @param roleId the role id
   */
  public void setRoleId(Integer roleId) {
    this.roleId = roleId;
  }

  /**
   * Gets name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets name.
   *
   * @param name the name
   */
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
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
