package net.novucs.esd.model;

import net.novucs.esd.orm.Column;
import net.novucs.esd.orm.Table;

@Table()
public class User {
  @Column(primary=true)
  private Integer id;

  @Column()
  private String name;
}
