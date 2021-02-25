package org.nkjmlab.sorm4j.demo;

public class Customer {

  private int id;
  private String name;
  private String address;

  // Require public no arg constructor (default constructor)
  public Customer() {}

  public Customer(String name, String address) {
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
    return "Customer [id=" + id + ", address=" + address + ", name=" + name + "]";
  }


}
