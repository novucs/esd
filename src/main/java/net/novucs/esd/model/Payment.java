package net.novucs.esd.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;
import net.novucs.esd.orm.Column;
import net.novucs.esd.orm.Table;

/**
 * The type Payment.
 */
@Table
public final class Payment {

  @Column(primary = true)
  private Integer id;

  @Column(foreign = User.class)
  private Integer userId;

  // Pounds and pence integers make up balance, we do not want to store
  // monetary values as floating point numbers to prevent the possibility
  // of rounding errors.
  @Column
  private Integer pounds;

  @Column
  private Integer pence;

  @Column(nullable = true)
  private String stripeId;

  @Column
  private String reference;

  @Column
  private ZonedDateTime date;

  @Column
  private String approvalStatus;

  /**
   * Instantiates a new Payment.
   */
  public Payment() {
    // This constructor is intentionally empty.
  }

  /**
   * Instantiates a new Payment.
   *
   * @param userId    the user id
   * @param amount    the amount
   * @param stripeId  the stripe id
   * @param reference the reference
   * @param date      the payment date
   */
  public Payment(Integer userId, BigDecimal amount, String stripeId, String reference,
      ZonedDateTime date, String approvalStatus) {
    this.userId = userId;
    double doubleBalance = amount.doubleValue();
    pounds = (int) doubleBalance;
    pence = (int) ((doubleBalance - pounds) * 100);
    this.stripeId = stripeId;
    this.reference = reference;
    this.date = date;
    this.approvalStatus = approvalStatus;
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
   * Gets amount.
   *
   * @return the amount
   */
  public BigDecimal getAmount() {
    return BigDecimal.valueOf(pounds + (pence / 100f));
  }

  /**
   * Sets amount.
   *
   * @param balance the balance
   */
  public void setAmount(BigDecimal balance) {
    double doubleBalance = balance.doubleValue();
    this.pounds = (int) doubleBalance;
    this.pence = (int) ((doubleBalance - pounds) * 100);
  }

  /**
   * Gets stripe id.
   *
   * @return the stripe id
   */
  public String getStripeId() {
    return stripeId;
  }

  /**
   * Sets stripe id.
   *
   * @param stripeId the stripe id
   */
  public void setStripeId(String stripeId) {
    this.stripeId = stripeId;
  }

  /**
   * Gets reference.
   *
   * @return the reference
   */
  public String getReference() {
    return reference;
  }

  /**
   * Sets reference.
   *
   * @param reference the reference
   */
  public void setReference(String reference) {
    this.reference = reference;
  }

  /**
   * Get payment date.
   *
   * @return date
   */
  public ZonedDateTime getDate() {
    return date;
  }

  /**
   * Set payment date.
   *
   * @param date the date
   */
  public void setDate(ZonedDateTime date) {
    this.date = date;
  }

  /**
   * Gets approval status.
   *
   * @return the approval status
   */
  public String getApprovalStatus() {
    return approvalStatus;
  }

  /**
   * Sets approval status.
   *
   * @param approvalStatus the approval status
   */
  public void setApprovalStatus(String approvalStatus) {
    this.approvalStatus = approvalStatus;
  }

  /**
   * Gets whether the payment was paid offline.
   *
   * @return the boolean
   */
  public boolean wasPaidOffline() {
    return stripeId == null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Payment payment = (Payment) o;
    return Objects.equals(getId(), payment.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}