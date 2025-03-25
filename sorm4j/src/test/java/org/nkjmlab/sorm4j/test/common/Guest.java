package org.nkjmlab.sorm4j.test.common;

import java.util.Objects;

import org.nkjmlab.sorm4j.mapping.annotation.OrmColumnAliasPrefix;

/** Results POJO container */
@OrmColumnAliasPrefix("g")
public class Guest {

  private int id;
  private String name;
  private String address;

  public Guest() {}

  public static Guest of(String name, String address) {
    Guest g = new Guest();
    g.setName(name);
    g.setAddress(address);
    return g;
  }

  public int getId() {
    return id;
  }

  public String getAddress() {
    return address;
  }

  public String getName() {
    return name;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  @Override
  public String toString() {
    return "Guest [id=" + id + ", address=" + address + ", name=" + name + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(address, id, name);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Guest)) return false;
    Guest other = (Guest) obj;
    return Objects.equals(address, other.address)
        && id == other.id
        && Objects.equals(name, other.name);
  }
}
