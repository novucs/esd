package net.novucs.esd.model;

import java.time.ZonedDateTime;
import java.util.Objects;
import net.novucs.esd.orm.Column;
import net.novucs.esd.orm.Table;
import net.novucs.esd.util.Password;

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

  public User() {
    // This constructor is intentionally empty.
  }

  public User(String name, String email, Password password, String address,
      ZonedDateTime dateOfBirth, String status) {
    this.name = name;
    this.email = email.toLowerCase();
    this.password = password;
    this.address = address;
    this.dateOfBirth = dateOfBirth;
    this.status = status;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email.toLowerCase();
  }

  public Password getPassword() {
    return password;
  }

  public void setPassword(Password password) {
    this.password = password;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public ZonedDateTime getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(ZonedDateTime dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public String getStatus() {
    return status;
  }

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
