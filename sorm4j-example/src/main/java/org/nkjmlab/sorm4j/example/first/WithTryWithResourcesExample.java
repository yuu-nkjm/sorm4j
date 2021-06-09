package org.nkjmlab.sorm4j.example.first;

import java.util.List;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.Sorm;

public class WithTryWithResourcesExample {


  public static void main(String[] args) {
    // Creates an entry point
    Sorm sorm = Sorm.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", "username", "password");

    // Create customer table
    sorm.accept(conn -> conn.executeUpdate(Customer.CREATE_TABLE_SQL));

    // Open OrmConnection. It will be closed with try-with-resources block.
    try (OrmConnection conn = sorm.openConnection()) {

      // Insert an object
      conn.insert(Customer.ALICE);

      // Insert objects
      conn.insert(Customer.BOB, Customer.CAROL);

      // Execute select sql and convert result to pojo list.
      List<Customer> allCustomers = conn.readAll(Customer.class);
      System.out.println("all customers = " + allCustomers);

      // Execute select sql and convert result to stream.
      List<String> messages = conn.readAllLazy(Customer.class).stream()
          .map(c -> c.getName() + " lives in " + c.getAddress()).collect(Collectors.toList());
      System.out.println("messages = " + messages);

      // Execute select sql and convert result to a pojo object.
      Customer lastCustomer =
          conn.readFirst(Customer.class, "SELECT * FROM customer ORDER BY id DESC");
      System.out.println("last customer = " + lastCustomer);


      // Read object by primary key.
      Customer customerId2 = conn.readByPrimaryKey(Customer.class, 2);
      System.out.println("customer of ID 2 = " + customerId2);

      // Execute select sql and convert result to pojo list.
      List<Customer> customersLivingInKyoto =
          conn.readList(Customer.class, "SELECT * FROM customer WHERE address=?", "Kyoto");
      System.out.println("customers living in Kyoto = " + customersLivingInKyoto);
    }
  }

  private static class Customer {

    private static final Customer ALICE = new Customer(1, "Alice", "Kyoto");
    private static final Customer BOB = new Customer(2, "Bob", "Tokyo");
    private static final Customer CAROL = new Customer(3, "Carol", "Kyoto");
    private static final Customer DAVE = new Customer(4, "Dave", "Nara");


    public static final String CREATE_TABLE_SQL =
        "CREATE TABLE IF NOT EXISTS customer (id INT PRIMARY KEY, name VARCHAR, address VARCHAR)";

    private int id;
    private String name;
    private String address;

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


  }



}
