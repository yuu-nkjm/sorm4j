package org.nkjmlab.sorm4j.tool;

import org.nkjmlab.sorm4j.annotation.OrmColumnAliasPrefix;

@OrmColumnAliasPrefix("L")
public class Location {

  public enum Place {
    KYOTO, TOKYO
  }

  private int id;
  private Place name;

  public Location() {}

  public Location(int id, Place name) {
    this.id = id;
    this.name = name;
  }


  public int getId() {
    return id;
  }

  public Place getName() {
    return name;
  }

  @Override
  public String toString() {
    return "Location [id=" + id + ", name=" + name + "]";
  }



}
