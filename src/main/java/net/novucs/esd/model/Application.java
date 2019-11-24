package net.novucs.esd.model;

import java.math.BigDecimal;
import java.util.Objects;
import net.novucs.esd.orm.Column;
import net.novucs.esd.orm.Table;

/**
 * The type Application.
 */
@Table
public final class Application {

  @Column(primary = true)
  private Integer id;

  @Column(foreign = User.class, unique = "user_id_uq")
  private Integer userId;


  // Pounds and pence integers make up balance, we do not want to store
  // monetary values as floating point numbers to prevent the possibility
  // of rounding errors.
  @Column
  private Integer pounds;

  @Column
  private Integer pence;

  @Column
  private String status;

  /**
   * Instantiates a new Application.
   */
  public Application() {
    // This constructor is intentionally empty.
  }

  /**
   * Instantiates a new Application.
   *
   * @param userId the user id
   */
  public Application(Integer userId, BigDecimal balance) {
    this.userId = userId;
    double doubleBalance = balance.doubleValue();
    this.pounds = (int) doubleBalance;
    this.pence = (int) ((doubleBalance - pounds) * 100);
    this.status = "OPEN";
    setBalance(balance);
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
   * Gets balance.
   *
   * @return the balance
   */
  public BigDecimal getBalance() {
    return BigDecimal.valueOf(pounds + (pence / 100f));
  }


  /**
   * Sets balance.
   *
   * @param balance the balance
   */
  public void setBalance(BigDecimal balance) {
    double doubleBalance = balance.doubleValue();
    this.pounds = (int) doubleBalance;
    this.pence = (int) ((doubleBalance - pounds) * 100);
  }

  /**
   * Gets status.
   *
   * @return the status
   */
  public String getStatus() {
    return status;
  }

  /**
   * Sets status.
   *
   * @param status the status
   */
  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Application that = (Application) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
