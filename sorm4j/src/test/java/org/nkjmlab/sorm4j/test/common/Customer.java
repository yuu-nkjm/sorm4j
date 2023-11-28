package org.nkjmlab.sorm4j.test.common;

import org.nkjmlab.sorm4j.annotation.OrmColumnAliasPrefix;

@OrmColumnAliasPrefix("c")
public class Customer {

  public static final Customer ALICE = new Customer(1, "Alice", "Kyoto");
  public static final Customer BOB = new Customer(2, "Bob", "Tokyo");
  public static final Customer CAROL = new Customer(3, "Carol", "Kyoto");
  public static final Customer DAVE = new Customer(4, "Dave", "Nara");

  public static final String CREATE_TABLE_SQL =
      "CREATE TABLE IF NOT EXISTS customer (id INT PRIMARY KEY, name VARCHAR, address VARCHAR)";

  public int id;
  private String name;
  public String address;

  // Require public no arg constructor (default constructor)
  public Customer() {}

  public Customer(int id, String name, String address) {
    this.id = id;
    this.name = name;
    this.address = address;
  }

  public String getAddress() {
    return address;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "Customer [id=" + id + ", address=" + address + ", name=" + name + "]";
  }

  public void setName(String name) {
    this.name = name;
  }
}
