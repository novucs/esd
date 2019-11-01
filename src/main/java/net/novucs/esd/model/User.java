package net.novucs.esd.model;

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
  private String status;

  public User() {
    // This constructor is intentionally empty.
  }

  public User(String name, String email, Password password, String address, String status) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.address = address;
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
    this.email = email;
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

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
