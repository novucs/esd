package net.novucs.esd.model;

import java.util.Objects;
import net.novucs.esd.orm.Column;
import net.novucs.esd.orm.Table;

/**
 * The type User role.
 */
@Table
public final class UserRole {

  @Column(primary = true)
  private Integer id;

  @Column(foreign = User.class, unique = "user_role_uq")
  private Integer userId;

  @Column(foreign = Role.class, unique = "user_role_uq")
  private Integer roleId;

  /**
   * Instantiates a new User role.
   */
  public UserRole() {
    // This constructor is intentionally empty.
  }

  /**
   * Instantiates a new User role.
   *
   * @param userId the user id
   * @param roleId the role id
   */
  public UserRole(Integer userId, Integer roleId) {
    this.userId = userId;
    this.roleId = roleId;
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
   * Gets user id.
   *
   * @return the user id
   */
  public Integer getUserId() {
    return userId;
  }

  /**
   * Sets user id.
   *
   * @param userId the user id
   */
  public void setUserId(Integer userId) {
    this.userId = userId;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserRole userRole = (UserRole) o;
    return Objects.equals(id, userRole.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
