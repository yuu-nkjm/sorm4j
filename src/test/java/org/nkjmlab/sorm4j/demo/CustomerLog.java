package org.nkjmlab.sorm4j.demo;

public class CustomerLog {

  private int id;
  private int customerId;
  private String name;
  private String address;

  // Require public no arg constructor (default constructor)
  public CustomerLog() {}

  public CustomerLog(int customerId, String name, String address) {
    this.customerId = customerId;
    this.name = name;
    this.address = address;
  }


  public int getId() {
    return id;
  }

  public int getCustomerId() {
    return customerId;
  }


  public String getAddress() {
    return address;
  }


  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "CustomerLog [id=" + id + ", customerId=" + customerId + ", name=" + name + ", address="
        + address + "]";
  }

}
