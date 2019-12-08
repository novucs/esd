package net.novucs.esd.util;

import java.sql.SQLException;
import java.util.List;
import net.novucs.esd.model.Claim;
import net.novucs.esd.model.ClaimStatus;
import net.novucs.esd.model.Membership;
import net.novucs.esd.orm.Dao;
import net.novucs.esd.orm.Where;

public final class ClaimUtil {

  private ClaimUtil() {
  }

  /**
   * Gets total.
   *
   * @param claims the claims
   * @return the total
   */
  public static double getTotal(List<Claim> claims) {
    double total = 0;
    for (Claim claim : claims) {
      total += claim.getAmount().doubleValue();
    }
    return total;
  }

  /**
   * Gets all non rejected claims claims.
   *
   * @param membership the membership
   * @return the claims
   * @throws SQLException the sql exception
   */
  public static List<Claim> getNonRejectedClaims(Dao<Claim> dao, Membership membership)
      throws SQLException {
    return dao.select()
        .where(new Where()
            .eq("membership_id", membership.getId())
            .and()
            .neq("status", ClaimStatus.REJECTED.name())
            .and()
            .neq("status", ClaimStatus.CANCELLED.name()))
        .all();
  }
}
