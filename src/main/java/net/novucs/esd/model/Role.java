package net.novucs.esd.model;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.novucs.esd.orm.Column;
import net.novucs.esd.orm.Table;

/**
 * The type Role.
 */
@Table
public final class Role {

  public static final String MEMBER = "Member";
  public static final String USER = "User";
  public static final String ADMINISTRATOR = "Administrator";
  public static final List<String> DEFAULT_VALUES = Arrays.asList(
      MEMBER,
      USER,
      ADMINISTRATOR
  );

  @Column(primary = true)
  private Integer id;

  @Column(unique = "name_uq")
  private String name;

  /**
   * Instantiates a new Role.
   */
  public Role() {
    // This constructor is intentionally empty.
  }

  /**
   * Instantiates a new Role.
   *
   * @param name the name
   */
  public Role(String name) {
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
    Role role = (Role) o;
    return Objects.equals(id, role.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
