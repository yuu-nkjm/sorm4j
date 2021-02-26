package org.nkjmlab.sorm4j.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.OrmMapper;
import org.nkjmlab.sorm4j.OrmService;

public class Sorm4jDemo {
  private static org.slf4j.Logger log = org.nkjmlab.sorm4j.util.LoggerFactory.getLogger();

  private static final String jdbcUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;";
  private static final String user = "sa";
  private static final String password = "";

  private static final String SQL_CREATE_TABLE_CUSTOMERS =
      "CREATE TABLE IF NOT EXISTS customers (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR, address VARCHAR)";

  private static final Customer c1_1 = new Customer("Alice", "Kyoto");
  private static final Customer c1_2 = new Customer("Bob", "Tokyo");
  private static final List<Customer> customers1 = List.of(c1_1, c1_2);

  private static final Customer c2_1 = new Customer("Carol", "Osaka");
  private static final Customer c2_2 = new Customer("Dave", "Nara");
  private static final List<Customer> customers2 = List.of(c2_1, c2_2);

  public static void main(String[] args) {
    Sorm4jDemo demo = new Sorm4jDemo();
    demo.demoOfSorm4J();
    demo.demoOfUseJdbcConnection();
  }

  public static void createTable() {}

  private void demoOfSorm4J() {

    OrmService service = OrmService.of(jdbcUrl, user, password);
    service.run(conn -> conn.execute(SQL_CREATE_TABLE_CUSTOMERS));

    service.run(Customer.class, conn -> conn.insert(customers1));

    List<Customer> cs1 = service.execute(conn -> conn.readAll(Customer.class));
    log.debug("{}", cs1);

    List<String> msgs = service.execute(conn -> conn.readAllLazy(Customer.class)).stream()
        .map(c -> c.getName() + " lives in " + c.getAddress()).collect(Collectors.toList());
    log.debug("{}", msgs);

    Customer c1 = service
        .execute(conn -> conn.readFirst(Customer.class, "SELECT * FROM customers WHERE id=?", 1));
    log.debug("{}", c1);

    Customer c2 = service.execute(conn -> conn.readByPrimaryKey(Customer.class, 1));
    log.debug("{}", c2);


  }

  private void demoOfUseJdbcConnection() {

    try (Connection conn = DriverManager.getConnection(jdbcUrl, user, password)) {

      OrmMapper ormMapper = OrmService.toOrmMapper(conn);
      ormMapper.execute(SQL_CREATE_TABLE_CUSTOMERS);
      ormMapper.insert(customers2);

      List<Customer> cs1 = ormMapper.readList(Customer.class, "SELECT * FROM customers");
      log.debug("{}", cs1);


      List<String> msgs = ormMapper.readAllLazy(Customer.class).stream()
          .map(c -> c.getName() + " lives in " + c.getAddress()).collect(Collectors.toList());
      log.debug("{}", msgs);

    } catch (SQLException e) {
      e.printStackTrace();
    }

  }


}
