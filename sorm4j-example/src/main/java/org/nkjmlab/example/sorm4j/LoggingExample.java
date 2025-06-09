package org.nkjmlab.example.sorm4j;

import java.util.List;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.context.logging.LogContext;
import org.nkjmlab.sorm4j.example.first.Customer;
import org.nkjmlab.sorm4j.internal.context.logging.logger.Log4jSormLogger;
import org.nkjmlab.sorm4j.sql.parameterize.OrderedParameterSqlBuilder;

public class LoggingExample {

  public static void main(String[] args) {
    LogContext lc =
        LogContext.builder().setLoggerSupplier(Log4jSormLogger::getLogger).enableAll().build();

    SormContext context = SormContext.builder().setLogContext(lc).build();
    Sorm sorm = Sorm.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", context);

    sorm.acceptHandler(
        conn -> {
          conn.executeUpdate(Customer.CREATE_TABLE_SQL);
          conn.insert(Customer.ALICE, Customer.BOB, Customer.CAROL, Customer.DAVE);
        });

    List<Customer> customers =
        sorm.readList(
            Customer.class,
            OrderedParameterSqlBuilder.builder("select * from customer where name=? and address=?")
                .addParameters("Alice", "Kyoto")
                .build());
    System.out.println("customers=" + customers);

    System.out.println(sorm);
  }
}
