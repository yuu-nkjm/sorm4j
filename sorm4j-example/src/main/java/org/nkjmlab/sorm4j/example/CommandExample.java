package org.nkjmlab.sorm4j.example;

import java.util.List;
import java.util.Map;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.example.first.Customer;

public class CommandExample {

  public static void main(String[] args) {
    Sorm sorm = Sorm.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;");
    sorm.acceptHandler(conn -> {
      conn.executeUpdate(Customer.CREATE_TABLE_SQL);
      conn.insert(Customer.ALICE, Customer.BOB, Customer.CAROL, Customer.DAVE);
    });


    // Ordered parameter binding and query execution
    List<Customer> customers =
        sorm.applyHandler(conn -> conn.createCommand("select * from customer where name=? and address=?")
            .addParameter("Alice", "Kyoto").readList(Customer.class));
    System.out.println("customers=" + customers);


    // Named parameter binding and query execution
    customers = sorm.applyHandler(
        conn -> conn.createCommand("select * from customer where name=:name and address=:address")
            .bind("name", "Alice").bind("address", "Kyoto").readList(Customer.class));
    System.out.println("customers=" + customers);


    // Ordered parameter binding and SQL execution
    sorm.applyHandler(conn -> conn.createCommand("insert into customer values(?,?,?)")
        .addParameter("5", "Eve", "Tokyo").executeUpdate());
    customers = sorm.applyHandler(conn -> conn.selectAll(Customer.class));
    System.out.println("customers=" + customers);


    // Named parameter binding and SQL execution
    sorm.applyHandler(conn -> conn.createCommand("insert into customer values(:id,:name,:address)")
        .bindAll(Map.of("id", 6, "name", "Frank", "address", "Tokyo")).executeUpdate());
    customers = sorm.applyHandler(conn -> conn.selectAll(Customer.class));
    System.out.println("customers=" + customers);

  }



}
