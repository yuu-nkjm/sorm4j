package org.nkjmlab.sorm4j.test.common;

import java.util.Objects;
import org.nkjmlab.sorm4j.annotation.OrmColumnAliasPrefix;
import org.nkjmlab.sorm4j.annotation.OrmConstructor;

@OrmColumnAliasPrefix("g")
public class Guest {

  private int id;
  private String name;
  private String address;

  // Require public no arg constructor (default constructor)
  public Guest() {}

  public Guest(String name, String address) {
    this.name = name;
    this.address = address;
  }

  @OrmConstructor({"id", "name", "address"})
  public Guest(int id, String name, String address) {
    this.id = id;
    this.name = name;
    this.address = address;
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
    if (this == obj)
      return true;
    if (!(obj instanceof Guest))
      return false;
    Guest other = (Guest) obj;
    return Objects.equals(address, other.address) && id == other.id
        && Objects.equals(name, other.name);
  }

  public void setId(int id) {
    this.id = id;
  }


}
