package org.nkjmlab.sorm4j.example.first;

import java.util.List;
import org.nkjmlab.sorm4j.Sorm;

public class InstantCallExample {

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
    List<Customer> allCustomers = sorm.readAll(Customer.class);
    System.out.println("all customers = " + allCustomers);

    // Execute select sql and convert result to a pojo object.
    Customer lastCustomer =
        sorm.readFirst(Customer.class, "SELECT * FROM customer ORDER BY id DESC");
    System.out.println("last customer = " + lastCustomer);

    // Read object by primary key.
    Customer customerId2 = sorm.findByPrimaryKey(Customer.class, 2);
    System.out.println("customer of ID 2 = " + customerId2);

    // Execute select sql and convert result to pojo list.
    List<Customer> customersLivingInKyoto =
        sorm.readList(Customer.class, "SELECT * FROM customer WHERE address=?", "Kyoto");
    System.out.println("customers living in Kyoto = " + customersLivingInKyoto);
  }


}
