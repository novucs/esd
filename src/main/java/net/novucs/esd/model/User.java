package net.novucs.esd.model;

import java.time.ZonedDateTime;
import java.util.Locale;
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

  @Column(unique = "username_uq")
  private String username;

  @Column(unique = "email_uq")
  private String email;

  @Column
  private Password password;

  @Column
  private Integer needsPasswordChange = 1;

  @Column
  private String address;

  @Column
  private ZonedDateTime dateOfBirth;

  /**
   * Instantiates a new User.
   */
  public User() {
    // This constructor is intentionally empty.
  }

  /**
   * Instantiates a new User.
   *
   * @param name                the name
   * @param email               the email
   * @param password            the password
   * @param address             the address
   * @param dateOfBirth         the date of birth
   * @param needsPasswordChange does the User need a password change
   */
  public User(String name, String username, String email, Password password, String address,
      ZonedDateTime dateOfBirth, Integer needsPasswordChange) {
    this.name = name;
    this.username = username;
    this.email = email.toLowerCase(Locale.UK);
    this.password = password;
    this.address = address;
    this.dateOfBirth = dateOfBirth;
    this.needsPasswordChange = needsPasswordChange;
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
   * Get username.
   *
   * @return username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Set the username.
   *
   * @param username username
   */
  public void setUsername(String username) {
    this.username = username;
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
    this.email = email.toLowerCase(Locale.UK);
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
   * Gets whether they this User needs a password change or not.
   *
   * @return Integer
   */
  public Integer getNeedsPasswordChange() {
    return this.needsPasswordChange;
  }

  /**
   * Set the flag for if this User needs a password change.
   *
   * @param needsPasswordChange flag
   */
  public void setNeedsPasswordChange(Integer needsPasswordChange) {
    this.needsPasswordChange = needsPasswordChange;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return Objects.equals(id, user.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
