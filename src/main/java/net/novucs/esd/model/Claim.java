package net.novucs.esd.model;

import java.util.Objects;
import net.novucs.esd.orm.Column;
import net.novucs.esd.orm.Table;

/**
 * The type Claim.
 */
@Table
public final class Claim {

  @Column(primary = true)
  private Integer id;

  @Column(foreign = Membership.class)
  private Integer membershipId;

  /**
   * Instantiates a new Claim.
   */
  public Claim() {
    // This constructor is intentionally empty.
  }

  /**
   * Instantiates a new Claim.
   *
   * @param membershipId the membership id
   */
  public Claim(Integer membershipId) {
    this.membershipId = membershipId;
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
   * Gets membership id.
   *
   * @return the membership id
   */
  public Integer getMembershipId() {
    return membershipId;
  }

  /**
   * Sets membership id.
   *
   * @param membershipId the membership id
   */
  public void setMembershipId(Integer membershipId) {
    this.membershipId = membershipId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Claim claim = (Claim) o;
    return Objects.equals(getId(), claim.getId())
        && Objects.equals(getMembershipId(), claim.getMembershipId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getMembershipId());
  }
}
