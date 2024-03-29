# sorm4j-example

## Sample with lambda expressions
- After creating a Sorm object, various database accesses could be written in one line of code.

```java
package org.nkjmlab.sorm4j.example.first;

import java.util.List;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.Sorm;

public class FirstExample {

  public static void main(String[] args) {

    // Creates an entry point
    Sorm sorm = Sorm.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", "username", "password");


    // Create customer table
    sorm.apply(conn -> conn.executeUpdate(Customer.CREATE_TABLE_SQL));

    // Insert an object
    sorm.apply(conn -> conn.insert(Customer.ALICE));

    // Insert objects
    sorm.apply(conn -> conn.insert(Customer.BOB, Customer.CAROL));

    // Execute select sql and convert result to pojo list.
    List<Customer> allCustomers = sorm.apply(conn -> conn.readAll(Customer.class));
    System.out.println("all customers = " + allCustomers);

    // Execute select sql and convert result to stream.
    List<String> messages = sorm.apply(conn -> conn.readAllLazy(Customer.class).stream()
        .map(c -> c.getName() + " lives in " + c.getAddress()).collect(Collectors.toList()));
    System.out.println("messages = " + messages);

    // Execute select sql and convert result to a pojo object.
    Customer lastCustomer = sorm
        .apply(conn -> conn.readFirst(Customer.class, "SELECT * FROM customer ORDER BY id DESC"));
    System.out.println("last customer = " + lastCustomer);

    // Read object by primary key.
    Customer customerId2 = sorm.apply(conn -> conn.readByPrimaryKey(Customer.class, 2));
    System.out.println("customer of ID 2 = " + customerId2);

    // Execute select sql and convert result to pojo list.
    List<Customer> customersLivingInKyoto = sorm.apply(
        conn -> conn.readList(Customer.class, "SELECT * FROM customer WHERE address=?", "Kyoto"));
    System.out.println("customers living in Kyoto = " + customersLivingInKyoto);
  }

}

```
