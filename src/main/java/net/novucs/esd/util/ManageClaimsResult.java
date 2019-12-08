package net.novucs.esd.util;

import net.novucs.esd.model.Claim;
import net.novucs.esd.model.Membership;
import net.novucs.esd.model.User;

public class ManageClaimsResult {

  private final Claim claim;
  private final Membership membership;
  private final User user;

  public ManageClaimsResult(Claim claim, Membership membership, User user) {
    this.claim = claim;
    this.membership = membership;
    this.user = user;
  }

  public Claim getClaim() {
    return claim;
  }

  public Membership getMembership() {
    return membership;
  }

  public User getUser() {
    return user;
  }
}
