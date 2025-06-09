package org.nkjmlab.sorm4j.example.first;

public record Player(int id, String name, String address) {

  public static final String CREATE_TABLE_SQL =
      "CREATE TABLE IF NOT EXISTS players (id INT PRIMARY KEY, name VARCHAR, address VARCHAR)";
}
