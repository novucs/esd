package net.novucs.esd.util;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.util.List;
import net.novucs.esd.lifecycle.Session;
import net.novucs.esd.model.Application;
import net.novucs.esd.model.Claim;
import net.novucs.esd.model.Membership;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Where;

public final class MembershipUtils {

  public final int ANNUAL_FEE = 10;
  public final String STATUS_ACTIVE = "ACTIVE";
  public final String STATUS_NONE = "NONE";
  public final String STATUS_FULL_CLAIM = "FULL_CLAIM";
  public final String STATUS_FULL_WAIT = "FULL_WAIT";
  public final String STATUS_FULL_USED = "FULL_USED";
  public final String STATUS_SUSPENDED = "SUSPENDED";
  public final String STATUS_EXPIRED = "EXPIRED";
  public final DecimalFormat df = new DecimalFormat("#.##");

  public String getAnnualFee() {
    return df.format(ANNUAL_FEE);
  }

  public Application getApplication(Session session, Dao<Application> applicationDao)
      throws SQLException {
    return applicationDao.select().where(new Where().eq("user_id",
        session.getUser().getId())).first();
  }

  public List<Claim> getAllClaims(Dao<Claim> claimDao, Membership currentMembership)
      throws SQLException {
    return claimDao.select()
        .where(new Where().eq("membership_id", currentMembership.getId())).all();
  }

  public Membership getCurrentMembership(Session session, Dao<Membership> membershipDao)
      throws SQLException {
    List<Membership> memberships = getAllMemberships(session, membershipDao);
    ZonedDateTime now = ZonedDateTime.now();
    return memberships.stream()
        .filter(m -> m.getStartDate().isBefore(now) && m.getEndDate().isAfter(now))
        .findFirst().orElse(null);
  }

  public Membership getLastMembership(Session session, Dao<Membership> membershipDao,
      List<Membership> memberships)
      throws SQLException {

    if (memberships.size() > 0) {
      Membership lastMembership = memberships.get(0);
      Membership currentMembership = getCurrentMembership(session, membershipDao);
      if (currentMembership == null) {
        for (Membership membership : memberships) {
          if (membership.getStartDate().isAfter(lastMembership.getEndDate())) {
            lastMembership = membership;
          }
        }
        Membership renewMembership = new Membership(session.getUser().getId(),
            "EXPIRED",
            lastMembership.getEndDate().plusDays(1),
            false
        );
        membershipDao.insert(renewMembership);
        return lastMembership;
      } else {
        return memberships.stream()
            .filter(m -> m.getStartDate().isAfter(currentMembership.getStartDate().minusMonths(13)))
            .findFirst().orElse(null);
      }
    } else {
      return null;
    }
  }

  public String evaluateMembershipClaimsBalance(List<Claim> claims) {
    double balance = 0;
    for (Claim claim : claims
    ) {
      balance += Double.parseDouble(claim.getAmount().toString());

    }
    return df.format(balance);
  }

  public List<Membership> getAllMemberships(Session session, Dao<Membership> membershipDao)
      throws SQLException {
    return membershipDao.select()
        .where(new Where().eq("user_id", session.getUser().getId())).all();
  }
}
