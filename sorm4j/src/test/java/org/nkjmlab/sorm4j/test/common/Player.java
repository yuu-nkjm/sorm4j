package org.nkjmlab.sorm4j.test.common;

import java.util.Objects;

import org.nkjmlab.sorm4j.mapping.annotation.OrmColumnAliasPrefix;
import org.nkjmlab.sorm4j.mapping.annotation.OrmConstructor;
import org.nkjmlab.sorm4j.mapping.annotation.OrmTableName;

/** With Constructor */
@OrmTableName("PLAYERS")
@OrmColumnAliasPrefix("p")
public class Player {

  public int id;
  private String name;
  public String address;

  @OrmConstructor({"id", "name", "address"})
  public Player(int id, String name, String address) {
    this.id = id;
    this.name = name;
    this.address = address;
  }

  public int getId() {
    return id;
  }

  // not match address field, Sorm accesses the public filed directly.
  public String readAddress() {
    return address;
  }

  public String getName() {
    return name;
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
    if (this == obj) return true;
    if (!(obj instanceof Player)) return false;
    Player other = (Player) obj;
    return Objects.equals(address, other.address)
        && id == other.id
        && Objects.equals(name, other.name);
  }
}
