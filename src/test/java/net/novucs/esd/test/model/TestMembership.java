package net.novucs.esd.test.model;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import net.novucs.esd.model.Membership;
import org.junit.Test;

public class TestMembership {

  @Test
  public void testBalanceCalculations() {
    Membership membership = new Membership();
    BigDecimal original = new BigDecimal("100.5");
    membership.setBalance(original);
    BigDecimal calculated = membership.getBalance();
    assertEquals("Balance should be converted between integers and BigDecimal "
        + "without errors", original, calculated);
  }
}
