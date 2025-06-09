package org.nkjmlab.sorm4j.example.first;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.table.orm.DefinedTable;
import org.nkjmlab.sorm4j.table.orm.TableConnection;

public class TableExample {

  public static void main(String[] args) {
    // Creates an entry point
    Sorm sorm = Sorm.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;");

    // Create a table access object.
    DefinedTable<Customer> customerTable = DefinedTable.of(sorm, Customer.class);

    // Create the table based on Customer.class
    customerTable.createTableIfNotExists();

    // Open TableConnection. It will be closed with try-with-resources block.
    try (TableConnection<Customer> conn = customerTable.open()) {

      // Print table definition.
      System.out.println(conn.getOrmTableMetaData());

      // Insert objects.
      conn.insert(Customer.ALICE, Customer.BOB, Customer.CAROL, Customer.DAVE);

      // Print all rows on the customer table.
      System.out.println(conn.selectAll());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static record Customer(int id, String name, String address) {
    private static final Customer ALICE = new Customer(1, "Alice", "Kyoto");
    private static final Customer BOB = new Customer(2, "Bob", "Tokyo");
    private static final Customer CAROL = new Customer(3, "Carol", "Kyoto");
    private static final Customer DAVE = new Customer(4, "Dave", "Nara");
  }
}
