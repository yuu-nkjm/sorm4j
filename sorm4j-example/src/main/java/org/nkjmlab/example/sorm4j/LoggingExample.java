package org.nkjmlab.example.sorm4j;

import java.util.List;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.example.first.Customer;
import org.nkjmlab.sorm4j.extension.logger.JulSormLogger;
import org.nkjmlab.sorm4j.extension.logger.Log4jSormLogger;

public class LoggingExample {

  public static void main(String[] args) {
    Sorm sorm = Sorm.newBuilder("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", "username", "password")
        .setLoggerSupplier(JulSormLogger::getLogger).setLoggerOnAll().build();

    sorm.accept(conn -> {
      conn.executeUpdate(Customer.CREATE_TABLE_SQL);
      conn.insert(Customer.ALICE, Customer.BOB, Customer.CAROL, Customer.DAVE);
    });

    sorm = Sorm.newBuilder("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", "username", "password")
        .setLoggerSupplier(Log4jSormLogger::getLogger).setLoggerOnAll().build();
    List<Customer> customers =
        sorm.apply(conn -> conn.createCommand("select * from customer where name=? and address=?")
            .addParameter("Alice", "Kyoto").readList(Customer.class));
    System.out.println("customers=" + customers);

  }

}
