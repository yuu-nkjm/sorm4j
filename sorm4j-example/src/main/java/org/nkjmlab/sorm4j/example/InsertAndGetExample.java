package org.nkjmlab.sorm4j.example;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.example.first.Guest;
import org.nkjmlab.sorm4j.result.InsertResult;

public class InsertAndGetExample {

  public static void main(String[] args) {
    Sorm sorm = Sorm.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;");

    sorm.acceptHandler(conn -> {
      conn.executeUpdate(Guest.CREATE_TABLE_SQL);
      InsertResult<Guest> customer = conn.insertAndGet(Guest.ALICE);
      System.out.println(customer.getObject().getId());
      customer = conn.insertAndGet(Guest.ALICE);
      System.out.println(customer.getObject().getId());

    });

  }
}
