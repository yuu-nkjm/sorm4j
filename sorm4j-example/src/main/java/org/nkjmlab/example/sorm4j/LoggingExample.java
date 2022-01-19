package org.nkjmlab.example.sorm4j;

import java.util.List;
import javax.sql.DataSource;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.SormContext;
import org.nkjmlab.sorm4j.example.first.Customer;
import org.nkjmlab.sorm4j.internal.util.logger.JulSormLogger;
import org.nkjmlab.sorm4j.internal.util.logger.Log4jSormLogger;

public class LoggingExample {

  public static void main(String[] args) {
    DataSource ds =
        Sorm.createDataSource("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", "username", "password");
    SormContext context =
        SormContext.builder().setLoggerSupplier(JulSormLogger::getLogger).setLoggerOnAll().build();
    Sorm sorm = Sorm.create(ds, context);


    sorm.acceptHandler(conn -> {
      conn.executeUpdate(Customer.CREATE_TABLE_SQL);
      conn.insert(Customer.ALICE, Customer.BOB, Customer.CAROL, Customer.DAVE);
    });


    context = SormContext.builder().setLoggerSupplier(Log4jSormLogger::getLogger).setLoggerOnAll()
        .build();
    sorm = Sorm.create(ds, context);

    List<Customer> customers =
        sorm.applyHandler(conn -> conn.createCommand("select * from customer where name=? and address=?")
            .addParameter("Alice", "Kyoto").readList(Customer.class));
    System.out.println("customers=" + customers);

  }

}
