package org.nkjmlab.sorm4j.example.first;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.util.table.Table;

public class FirstExample {

  public static void main(String[] args) {

    // Creates an entry point
    Sorm sorm = Sorm.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;");

    // Create customer table
    sorm.executeUpdate(Customer.CREATE_TABLE_SQL);

    // Insert an object
    sorm.insert(Customer.ALICE);

    // Insert objects
    sorm.insert(Customer.BOB, Customer.CAROL);

    // Execute select sql and convert result to pojo list.
    List<Customer> allCustomers = sorm.selectAll(Customer.class);
    System.out.println("all customers = " + allCustomers);

    // Execute select sql and convert result to stream. The stream must be closed.
    sorm.acceptHandler(conn -> {
      try (Stream<Customer> stream = conn.openStreamAll(Customer.class)) {
        List<String> msgs = stream.map(c -> c.getName() + " lives in " + c.getAddress())
            .collect(Collectors.toList());
        System.out.println("messages = " + msgs);
      }
    });

    sorm.acceptHandler(conn -> {
      try (Stream<Customer> stream = conn.openStreamAll(Customer.class)) {
        List<String> msgs = stream.map(c -> c.getName() + " lives in " + c.getAddress())
            .collect(Collectors.toList());
        System.out.println("messages = " + msgs);
      }
    });

    sorm.acceptHandler(conn -> conn.openStreamAll(Customer.class), stream -> {
      List<String> msgs =
          stream.map(c -> c.getName() + " lives in " + c.getAddress()).collect(Collectors.toList());
      System.out.println("messages = " + msgs);
    });

    List<String> tmp =
        sorm.applyHandler(conn -> conn.openStreamAll(Customer.class), stream -> stream
            .map(c -> c.getName() + " lives in " + c.getAddress()).collect(Collectors.toList()));
    System.out.println(tmp);

    // Execute select sql and convert result to a pojo object.
    Customer lastCustomer =
        sorm.readFirst(Customer.class, "SELECT * FROM customer ORDER BY id DESC");
    System.out.println("last customer = " + lastCustomer);

    // Read object by primary key.
    Customer customerId2 = sorm.selectByPrimaryKey(Customer.class, 2);
    System.out.println("customer of ID 2 = " + customerId2);

    // Execute select sql and convert result to pojo list.
    List<Customer> customersLivingInKyoto =
        sorm.readList(Customer.class, "SELECT * FROM customer WHERE address=?", "Kyoto");
    System.out.println("customers living in Kyoto = " + customersLivingInKyoto);


    Table<Customer> customerTable = sorm.getTable(Customer.class);
    customerTable.applyHandler(conn -> conn.openStreamAll(),
        stream -> stream.collect(Collectors.toList()));

  }


}
