package net.novucs.esd.model;

import java.time.ZonedDateTime;
import java.util.Objects;
import net.novucs.esd.orm.Column;
import net.novucs.esd.orm.Table;

/**
 * The type Membership.
 */
@Table
public final class Membership {

  @Column(primary = true)
  private Integer id;

  @Column(foreign = User.class)
  private Integer userId;

  @Column
  private ZonedDateTime startDate;

  @Column
  private ZonedDateTime claimFromDate;

  @Column
  private String status;

  /**
   * Instantiates a new Membership.
   */
  public Membership() {
    // This constructor is intentionally empty.
  }

  /**
   * Instantiates a new Membership.
   *
   * @param userId      the user id
   * @param status      the status
   * @param startDate   the start date
   * @param isNewMember the is new member
   */
  public Membership(Integer userId, String status, ZonedDateTime startDate,
      Boolean isNewMember) {
    this.userId = userId;
    this.status = status;
    this.startDate = startDate;
    this.claimFromDate = isNewMember ? startDate.plusMonths(6) : startDate;
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
   * Gets status.
   *
   * @return the status
   */
  public String getStatus() {
    return status;
  }

  /**
   * Sets status.
   *
   * @param status the status
   */
  public void setStatus(String status) {
    this.status = status;
  }


  /**
   * Gets start date.
   *
   * @return the start date
   */
  public ZonedDateTime getStartDate() {
    return startDate;
  }

  /**
   * Sets start date.
   *
   * @param startDate the start date
   */
  public void setStartDate(ZonedDateTime startDate) {
    this.startDate = startDate;
  }

  /**
   * Gets end date.
   *
   * @return the end date
   */
  public ZonedDateTime getEndDate() {
    return startDate.plusYears(1);
  }

  /**
   * Gets claim from date.
   *
   * @return the claim from date
   */
  public ZonedDateTime getClaimFromDate() {
    return claimFromDate;
  }

  /**
   * Sets claim from date.
   *
   * @param claimFromDate the claim from date
   */
  public void setClaimFromDate(ZonedDateTime claimFromDate) {
    this.claimFromDate = claimFromDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Membership that = (Membership) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
