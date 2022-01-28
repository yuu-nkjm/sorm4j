package org.nkjmlab.sorm4j.example.first;

import java.util.List;
import org.nkjmlab.sorm4j.Sorm;

public class FirstRecordExample {

  public static void main(String[] args) {

    // Creates an entry point
    Sorm sorm = Sorm.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;");

    // Create customer table
    sorm.executeUpdate(Player.CREATE_TABLE_SQL);

    // Insert an object
    sorm.insert(new Player(0, "Alice", "Kyoto"));


    // Execute select sql and convert result to pojo list.
    List<Player> allCustomers = sorm.selectAll(Player.class);
    System.out.println("all customers = " + allCustomers);


  }


}
