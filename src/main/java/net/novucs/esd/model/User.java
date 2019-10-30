package net.novucs.esd.model;

import net.novucs.esd.orm.Column;
import net.novucs.esd.orm.Table;

@Table()
public class User {

  @Column(primary = true)
  private transient Integer id;

  @Column()
  private transient String name;

  public User() {
    // This constructor is intentionally empty.
  }

  public User(String name) {
    this.name = name;
  }

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
