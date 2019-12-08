package net.novucs.esd.util;

import java.util.List;
import net.novucs.esd.model.Claim;

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
}
