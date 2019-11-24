package net.novucs.esd.util;

public final class Constants {

  public static final class APPLICATION { // NOPMD

    public static final String STATUS_OPEN = "OPEN";

    //    public static final String STATUS_APPROVED = "APPROVED";
    //    public static final String STATUS_DECLINED = "DECLINED";
    private APPLICATION() {  // NOPMD
    }
  }

  public static final class MEMBERSHIP {  // NOPMD

    public static final int ANNUAL_FEE = 10;
    public static final String STATUS_ACTIVE = "ACTIVE";
    //    public static final String STATUS_SUSPENDED = "SUSPENDED";
    //    public static final String STATUS_APPLICATION = "APPLICATION";
    //    public static final String STATUS_EXPIRED = "APPLICATION";

    private MEMBERSHIP() {  // NOPMD
    }
  }

  //  public final class ClaimConstants {
  //    private static final int MAX_CLAIM_YEAR = 100;
  //    private static final float MAX_CLAIM_SINGLE = 75;
  //    private static final float CLAIM_LIMIT = 2;
  //
  //    private ClaimConstants() {
  //    }
  //  }
  public static final class STRIPE {  // NOPMD

    //public static final String TEST_PUBLISH_KEY = "pk_test_b51qKB6WZmKC9I9vmLiKUAgK00zFG7dhQ2";
    public static final String TEST_SECRET_KEY = "sk_test_kMQ6gPhFRqsyWex4O8FxMU4200Poyj5KwH";

    private STRIPE() {  // NOPMD
    }

  }
}
