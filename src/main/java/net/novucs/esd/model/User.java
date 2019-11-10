package net.novucs.esd.model;

import java.time.ZonedDateTime;
import java.util.Objects;
import net.novucs.esd.orm.Column;
import net.novucs.esd.orm.Table;
import net.novucs.esd.util.Password;

/**
 * The type User.
 */
@Table
public class User {

  @Column(primary = true)
  private Integer id;

  @Column
  private String name;

  @Column
  private String email;

  @Column
  private Password password;

  @Column
  private String address;

  @Column
  private ZonedDateTime dateOfBirth;

  @Column
  private String status;

  /**
   * Instantiates a new User.
   */
  public User() {
    // This constructor is intentionally empty.
  }

  /**
   * Instantiates a new User.
   *
   * @param name        the name
   * @param email       the email
   * @param password    the password
   * @param address     the address
   * @param dateOfBirth the date of birth
   * @param status      the status
   */
  public User(String name, String email, Password password, String address,
      ZonedDateTime dateOfBirth, String status) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.address = address;
    this.dateOfBirth = dateOfBirth;
    this.status = status;
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
   * Gets name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets name.
   *
   * @param name the name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets email.
   *
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets email.
   *
   * @param email the email
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Gets password.
   *
   * @return the password
   */
  public Password getPassword() {
    return password;
  }

  /**
   * Sets password.
   *
   * @param password the password
   */
  public void setPassword(Password password) {
    this.password = password;
  }

  /**
   * Gets address.
   *
   * @return the address
   */
  public String getAddress() {
    return address;
  }

  /**
   * Sets address.
   *
   * @param address the address
   */
  public void setAddress(String address) {
    this.address = address;
  }

  /**
   * Gets date of birth.
   *
   * @return the date of birth
   */
  public ZonedDateTime getDateOfBirth() {
    return dateOfBirth;
  }

  /**
   * Sets date of birth.
   *
   * @param dateOfBirth the date of birth
   */
  public void setDateOfBirth(ZonedDateTime dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
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
    User user = (User) o;
    return Objects.equals(getId(), user.getId())
        && Objects.equals(getName(), user.getName())
        && Objects.equals(getEmail(), user.getEmail())
        && Objects.equals(getPassword(), user.getPassword())
        && Objects.equals(getAddress(), user.getAddress())
        && Objects.equals(getDateOfBirth(), user.getDateOfBirth())
        && Objects.equals(getStatus(), user.getStatus());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getName(), getEmail(), getPassword(), getAddress(), getStatus());
  }
}
