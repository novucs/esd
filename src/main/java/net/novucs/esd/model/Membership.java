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

  public static final int LENGTH_IN_MONTHS = 12;

  @Column(primary = true)
  private Integer id;

  @Column(foreign = User.class)
  private Integer userId;

  @Column
  private ZonedDateTime startDate;

  @Column
  private ZonedDateTime claimFromDate;

  @Column
  private Integer suspended;

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
   * @param isNewMember the is new member
   */
  public Membership(Integer userId, Boolean isNewMember) {
    this(userId, ZonedDateTime.now(), isNewMember);
  }

  /**
   * Instantiates a new Membership.
   *
   * @param userId      the user id
   * @param startDate   the start date
   * @param isNewMember the is new member
   */
  public Membership(Integer userId, ZonedDateTime startDate, Boolean isNewMember) {
    this.userId = userId;
    this.startDate = startDate;
    this.claimFromDate = isNewMember ? startDate.plusMonths(6) : startDate;
    this.suspended = 0;
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

  public boolean isExpired() {
    ZonedDateTime now = ZonedDateTime.now();
    return now.toEpochSecond() < startDate.plusMonths(LENGTH_IN_MONTHS).toEpochSecond();
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

  /**
   * Gets whether the membership is suspended.
   *
   * @return <code>true</code> if the membership is suspended.
   */
  public boolean isSuspended() {
    return suspended != 0;
  }

  /**
   * Update suspended status.
   *
   * @param suspended whether the user is suspended.
   */
  public void setSuspended(boolean suspended) {
    this.suspended = suspended ? 1 : 0;
  }

  /**
   * Gets whether the membership is active.
   *
   * @return <code>true</code> if the membership is active.
   */
  public boolean isActive() {
    return !isSuspended() && !isExpired();
  }

  /**
   * Gets whether the membership is able to make a claim.
   *
   * @return <code>true</code> if the membership is able to make a claim.
   */
  public boolean isAbleToClaim() {
    if (!isActive()) {
      return false;
    }

    ZonedDateTime now = ZonedDateTime.now();
    return now.toEpochSecond() >= claimFromDate.toEpochSecond();
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
