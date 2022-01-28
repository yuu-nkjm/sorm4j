package org.nkjmlab.example.sorm4j;

import java.util.List;
import javax.sql.DataSource;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.example.first.Customer;
import org.nkjmlab.sorm4j.internal.util.logger.Log4jSormLogger;
import org.nkjmlab.sorm4j.util.command.Command;
import org.nkjmlab.sorm4j.util.logger.LoggerContext;

public class LoggingExample {

  public static void main(String[] args) {
    LoggerContext lc =
        LoggerContext.builder().setLoggerSupplier(Log4jSormLogger::getLogger).enableAll().build();

    DataSource ds =
        Sorm.createDataSource("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", "username", "password");
    SormContext context = SormContext.builder().setLoggerContext(lc).build();
    Sorm sorm = Sorm.create(ds, context);

    sorm.acceptHandler(conn -> {
      conn.executeUpdate(Customer.CREATE_TABLE_SQL);
      conn.insert(Customer.ALICE, Customer.BOB, Customer.CAROL, Customer.DAVE);
    });



    List<Customer> customers = sorm.applyHandler(
        conn -> Command.create(conn, "select * from customer where name=? and address=?")
            .addParameter("Alice", "Kyoto").readList(Customer.class));


    System.out.println("customers=" + customers);
    System.out.println(sorm);

  }

}
