package org.nkjmlab.sorm4j.tool;

public class Location {

  public enum Place {
    KYOTO, TOKYO
  }

  private int id;
  private Place name;

  public Location() {}

  public Location(Place place) {
    this.name = place;
  }


  public int getId() {
    return id;
  }

  public Place getName() {
    return name;
  }



}
