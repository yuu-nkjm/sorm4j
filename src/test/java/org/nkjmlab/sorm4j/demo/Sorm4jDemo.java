package org.nkjmlab.sorm4j.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import org.nkjmlab.sorm4j.OrmConnection;
import org.nkjmlab.sorm4j.Sorm;

public class Sorm4jDemo {
  private static org.slf4j.Logger log = org.nkjmlab.sorm4j.util.LoggerFactory.getLogger();

  private static final String jdbcUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;";
  private static final String user = "sa";
  private static final String password = "";

  private static final String SQL_CREATE_TABLE_CUSTOMERS =
      "CREATE TABLE IF NOT EXISTS customers (id INT PRIMARY KEY, name VARCHAR, address VARCHAR)";


  private static final String SQL_CREATE_TABLE_CUSTOMERS_LOG =
      "CREATE TABLE IF NOT EXISTS customer_logs (id INT AUTO_INCREMENT PRIMARY KEY, int customer_id, name VARCHAR, address VARCHAR)";

  private static final Customer c1_1 = new Customer(1, "Alice", "Kyoto");
  private static final Customer c1_2 = new Customer(2, "Bob", "Tokyo");
  private static final Customer c2_1 = new Customer(3, "Carol", "Osaka");
  private static final Customer c2_2 = new Customer(4, "Dave", "Nara");

  public static void main(String[] args) {
    System.out.println(log.getName());
    Sorm4jDemo demo = new Sorm4jDemo();
    demo.createAndDropTable();
    demo.demoOfSorm4J();
    demo.demoOfUseJdbcConnection();
  }

  private void createAndDropTable() {
    // TODO Auto-generated method stub

  }

  public static void createTable() {}

  private void demoOfSorm4J() {

    Sorm service = Sorm.create(jdbcUrl, user, password);
    service.run(conn -> conn.execute(SQL_CREATE_TABLE_CUSTOMERS));


    List<Customer> cs1 = service.execute(conn -> conn.readAll(Customer.class));
    log.debug("{}", cs1);

    List<String> msgs = service.execute(conn -> conn.readAllLazy(Customer.class).stream()
        .map(c -> c.getName() + " lives in " + c.getAddress()).collect(Collectors.toList()));
    log.debug("{}", msgs);

    Customer c1 = service
        .execute(conn -> conn.readFirst(Customer.class, "SELECT * FROM customers WHERE id=?", 1));
    log.debug("{}", c1);

    Customer c2 = service.execute(conn -> conn.readByPrimaryKey(Customer.class, 1));
    log.debug("{}", c2);

    service.run(Customer.class, conn -> {
      conn.insert(new Customer(1, "Alice", "Kyoto"));
      conn.getJdbcConnection().commit();
    });
    service.run(Customer.class, conn -> conn.insert(new Customer(2, "Bob", "Tokyo"),
        new Customer(3, "Carol", "Osaka"), new Customer(4, "Dave", "Nara")));


    System.out.println(service
        .execute(Customer.class, conn -> conn.readList("select * from customers where id=?", 1))
        .toString());

  }

  private void demoOfUseJdbcConnection() {

    try (Connection conn = DriverManager.getConnection(jdbcUrl, user, password)) {

      OrmConnection ormMapper = Sorm.getOrmConnection(conn);
      ormMapper.execute(SQL_CREATE_TABLE_CUSTOMERS);

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
