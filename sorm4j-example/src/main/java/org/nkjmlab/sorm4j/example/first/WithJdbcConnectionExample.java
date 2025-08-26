package org.nkjmlab.sorm4j.example.first;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.nkjmlab.sorm4j.OrmConnection;

public class WithJdbcConnectionExample {

  public static void main(String[] args) {
    String jdbcUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;";
    String user = "username";
    String password = "password";


    try (Connection jdbcConn = DriverManager.getConnection(jdbcUrl, user, password);
        OrmConnection conn = OrmConnection.of(jdbcConn);) {

      // Creates a object-relation mapping connection by wrapping a JDBC connection.


      // Creates customer table
      conn.executeUpdate(Customer.CREATE_TABLE_SQL);

      // insert an object
      conn.insert(Customer.ALICE);

      // insert objects
      conn.insert(Customer.BOB, Customer.CAROL);

      // Execute select sql and convert result to pojo list.
      List<Customer> allCustomers = conn.selectAll(Customer.class);
      System.out.println("all customers = " + allCustomers);

      // Execute select sql and convert result to stream.
      List<String> messages = conn.streamAll(Customer.class).apply(stream -> stream
          .map(c -> c.getName() + " lives in " + c.getAddress()).collect(Collectors.toList()));
      System.out.println("messages = " + messages);

      // Execute select sql and convert result to a pojo object.
      Customer lastCustomer =
          conn.readFirst(Customer.class, "SELECT * FROM customer ORDER BY id DESC");
      System.out.println("last customer = " + lastCustomer);


      // Read object by primary key.
      Customer customerId2 = conn.selectByPrimaryKey(Customer.class, 2);
      System.out.println("customer of ID 2 = " + customerId2);

      // Execute select sql and convert result to pojo list.
      List<Customer> customersLivingInKyoto =
          conn.readList(Customer.class, "SELECT * FROM customer WHERE address=?", "Kyoto");
      System.out.println("customers living in Kyoto = " + customersLivingInKyoto);

    } catch (

    SQLException e) {
      e.printStackTrace();
    }
  }

}
