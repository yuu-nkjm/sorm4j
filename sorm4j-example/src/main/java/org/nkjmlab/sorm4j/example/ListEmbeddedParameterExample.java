package org.nkjmlab.sorm4j.example;

import java.util.List;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.example.first.Customer;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;

public class ListEmbeddedParameterExample {

  public static void main(String[] args) {
    Sorm sorm = Sorm.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;");

    sorm.acceptHandler(conn -> {
      conn.executeUpdate(Customer.CREATE_TABLE_SQL);
      conn.insert(Customer.ALICE, Customer.BOB, Customer.CAROL, Customer.DAVE);
    });


    // List parameter and embedded parameter binding
    ParameterizedSql psql =
        ParameterizedSql.parse("select * from customer where name like {?} and address in(<?>)",
            "'A%'", List.of("Kyoto", "Tokyo"));

    System.out.println(psql);

    List<Customer> customers =
        sorm.applyHandler(conn -> conn.createCommand(psql).readList(Customer.class));
    System.out.println("customers=" + customers);

    // try {
    // // this way does not working. readList(Class, String, Object...) only accepts simple ordered
    // // parameters.
    // customers = sorm.apply(orm -> orm.readList(Customer.class,
    // "select * from customer where name like {?} and address in(<?>)", "'A%'",
    // List.of("Kyoto", "Tokyo")));
    // } catch (Exception e) {
    // System.err.println(e.getMessage());
    // }

  }


}
