package org.nkjmlab.sorm4j.tool;

import java.util.Objects;
import org.nkjmlab.sorm4j.annotation.OrmTable;

@OrmTable("PLAYERS")
public class Player {

  private int id;
  private String name;
  private String address;

  public Player() {}

  public Player(int id, String name, String address) {
    this.id = id;
    this.name = name;
    this.address = address;
  }


  public int getId() {
    return id;
  }


  // not match address field
  public String readAddress() {
    return address;
  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "Player [id=" + id + ", address=" + address + ", name=" + name + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(address, id, name);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof Player))
      return false;
    Player other = (Player) obj;
    return Objects.equals(address, other.address) && id == other.id
        && Objects.equals(name, other.name);
  }


}
