package net.novucs.esd.model;

import net.novucs.esd.orm.Column;
import net.novucs.esd.orm.Table;

@Table()
public class User {

  @Column(primary = true)
  private transient Integer id;

  @Column()
  private transient String name;

  @Column()
  private transient String email;

  // todo: encrypt this
  @Column()
  private transient String password;

  @Column()
  private transient String address;

  @Column()
  private transient String status;

  public User() {
    // This constructor is intentionally empty.
  }

  public User(String name) {
    this.name = name;
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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
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
