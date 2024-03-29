package org.nkjmlab.sorm4j.example;

import java.util.List;
import java.util.Map;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.example.first.Customer;
import org.nkjmlab.sorm4j.sql.OrderedParameterSqlParser;
import org.nkjmlab.sorm4j.sql.ParameterizedSql;
import org.nkjmlab.sorm4j.sql.ParameterizedSqlParser;

public class ParameterizedSqlExample {

  public static void main(String[] args) {
    Sorm sorm = Sorm.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;");

    sorm.acceptHandler(conn -> {
      conn.executeUpdate(Customer.CREATE_TABLE_SQL);
      conn.insert(Customer.ALICE, Customer.BOB, Customer.CAROL, Customer.DAVE);
    });


    // Ordered parameter binding and query execution
    String sql = "select * from customer where name=? and address=?";
    ParameterizedSql psql1 = ParameterizedSqlParser.parse(sql, "Alice", "Kyoto");
    ParameterizedSql psql2 =
        OrderedParameterSqlParser.of(sql).addParameter("Alice", "Kyoto").parse();

    List<Customer> customers = sorm.applyHandler(conn -> conn.readList(Customer.class, psql1));
    System.out.println("customers=" + customers);

    customers = sorm.applyHandler(conn -> conn.readList(Customer.class, psql2));
    System.out.println("customers=" + customers);

    // Named parameter binding and SQL execution
    ParameterizedSql psql3 =
        ParameterizedSqlParser.parse("insert into customer values(:id,:name,:address)",
            Map.of("id", 6, "name", "Frank", "address", "Tokyo"));
    sorm.applyHandler(conn -> conn.executeUpdate(psql3));
    customers = sorm.applyHandler(conn -> conn.selectAll(Customer.class));
    System.out.println("customers=" + customers);
  }



}
