package org.nkjmlab.sorm4j.example;

import java.sql.Connection;

import org.nkjmlab.sorm4j.OrmTransaction;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.example.first.Customer;

public class TransactionExample {

  public static void main(String[] args) {
    Sorm sorm = Sorm.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;");
    sorm.acceptHandler(conn -> conn.executeUpdate(Customer.CREATE_TABLE_SQL));


    int num0 =
        sorm.applyHandler(conn -> conn.readOne(Integer.class, "select count(*) from customer"));
    System.out.println("num0=" + num0); // 0 record

    // Transaction with lambda expression. In this way transaction is not committed.
    sorm.acceptHandler(Connection.TRANSACTION_READ_COMMITTED, transaction -> {
      try {
        transaction.insert(Customer.ALICE);
        throwException();
        transaction.commit();
      } catch (Exception e) {
      }
    });

    // In this way transaction is not committed.
    sorm.acceptHandler(Connection.TRANSACTION_READ_COMMITTED, transaction -> {
      transaction.insert(Customer.ALICE);
      transaction.commit();
    });


    int num1 =
        sorm.applyHandler(conn -> conn.readOne(Integer.class, "select count(*) from customer"));
    System.out.println("num1=" + num1); // 1 record because of auto commit


    try {
      sorm.acceptHandler(Connection.TRANSACTION_READ_COMMITTED, transaction -> {
        transaction.insert(Customer.BOB);
        int num2 = transaction.readOne(Integer.class, "select count(*) from customer");
        System.out.println("num2=" + num2); // 2 record
        throw new RuntimeException("error");
      });
    } catch (Exception e) {
    }

    int num3 =
        sorm.applyHandler(conn -> conn.readOne(Integer.class, "select count(*) from customer"));
    System.out.println("num3=" + num3); // 1 record because of rollback



    // Transaction with try-with-resources block. In this way transaction is auto rollback.
    try (OrmTransaction tran = sorm.open(Connection.TRANSACTION_READ_COMMITTED)) {
      tran.readOne(Integer.class, "select count(*) from customer");
      tran.insert(Customer.CAROL);
      // If the transaction is not committed, it will be automatically rollback when closing the
      // OrmTransaction
    }
    int num4 =
        sorm.applyHandler(conn -> conn.readOne(Integer.class, "select count(*) from customer"));
    System.out.println("num4=" + num4); // 1 record because of rollback


    try (OrmTransaction tran = sorm.open(Connection.TRANSACTION_READ_COMMITTED)) {
      tran.insert(Customer.DAVE);
      tran.commit();
    }
    int num5 =
        sorm.applyHandler(conn -> conn.readOne(Integer.class, "select count(*) from customer"));
    System.out.println("num5=" + num5); // 2 records after commit
  }

  private static void throwException() {
    throw new RuntimeException();
  }


}
