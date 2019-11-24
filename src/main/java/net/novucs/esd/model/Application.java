package net.novucs.esd.model;

import java.util.Objects;
import net.novucs.esd.orm.Column;
import net.novucs.esd.orm.Table;

/**
 * The type Application.
 */
@Table
public final class Application {

  @Column(primary = true)
  private Integer id;

  @Column(foreign = User.class, unique = "user_id_uq")
  private Integer userId;

  /**
   * Instantiates a new Application.
   */
  public Application() {
    // This constructor is intentionally empty.
  }

  /**
   * Instantiates a new Application.
   *
   * @param userId the user id
   */
  public Application(Integer userId) {
    this.userId = userId;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Application that = (Application) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
