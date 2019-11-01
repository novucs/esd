package net.novucs.esd.model;

import net.novucs.esd.orm.Column;
import net.novucs.esd.orm.Table;

@Table
public class Claim {

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
}
