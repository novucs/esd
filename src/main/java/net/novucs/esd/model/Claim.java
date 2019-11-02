package net.novucs.esd.model;

import java.util.Objects;
import net.novucs.esd.orm.Column;
import net.novucs.esd.orm.Table;

@Table
public final class Claim {

  @Column(primary = true)
  private Integer id;

  @Column(foreign = Membership.class)
  private Integer membershipId;

  public Claim() {
    // This constructor is intentionally empty.
  }

  public Claim(Integer membershipId) {
    this.membershipId = membershipId;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getMembershipId() {
    return membershipId;
  }

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
