package net.novucs.esd.model;

import net.novucs.esd.orm.Column;
import net.novucs.esd.orm.Table;

@Table()
public class User {
  @Column(primary=true)
  private Integer id;

  @Column()
  private String name;

  public User() {
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
}
