package org.nkjmlab.sorm4j.example.first;

import org.nkjmlab.sorm4j.annotation.OrmConstructor;

public class Guest {

  public static final Guest ALICE = new Guest("Alice", "Kyoto");
  public static final Guest BOB = new Guest("Bob", "Tokyo");
  public static final Guest CAROL = new Guest("Carol", "Kyoto");
  public static final Guest DAVE = new Guest("Dave", "Nara");


  public static final String CREATE_TABLE_SQL =
      "CREATE TABLE IF NOT EXISTS guests (id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR, address VARCHAR)";

  private int id;
  private String name;
  private String address;

  public Guest(String name, String address) {
    this(-1, name, address);
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

  public void setId(int id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "Guest [id=" + id + ", address=" + address + ", name=" + name + "]";
  }


}
