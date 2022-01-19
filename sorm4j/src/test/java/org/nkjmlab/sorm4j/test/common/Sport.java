package org.nkjmlab.sorm4j.test.common;

import org.nkjmlab.sorm4j.annotation.OrmColumnAliasPrefix;
import org.nkjmlab.sorm4j.annotation.OrmRecord;

@OrmColumnAliasPrefix("s")
@OrmRecord
public class Sport {

  public enum Sports {
    TENNIS, SOCCER
  }

  private int id;
  private Sports name;

  public Sport(int id, Sports name) {
    this.id = id;
    this.name = name;
  }


  public int getId() {
    return id;
  }

  public Sports getName() {
    return name;
  }

  @Override
  public String toString() {
    return "Sport [id=" + id + ", name=" + name + "]";
  }

}
