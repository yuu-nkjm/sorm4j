package org.nkjmlab.sorm4j.util;

import org.nkjmlab.sorm4j.OrmService;

public class OrmTestUtils {
  public static final String jdbcUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;";
  public static final String user = "sa";
  public static final String password = "";

  public static final Guest GUEST_ALICE = new Guest("Alice", "Kyoto");
  public static final Guest GUEST_BOB = new Guest("Bob", "Tokyo");
  public static final Guest GUEST_CAROL = new Guest("Carol", "Osaka");
  public static final Guest GUEST_DAVE = new Guest("Dave", "Nara");

  private static final String SQL_CREATE_TABLE_GUESTS =
      "CREATE TABLE IF NOT EXISTS guests (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR, address VARCHAR)";
  private static final String SQL_CREATE_TABLE_PLAYERS =
      "CREATE TABLE IF NOT EXISTS guests (id INT PRIMARY KEY, name VARCHAR, address VARCHAR)";

  public static OrmService createOrmService() {
    return OrmService.of(jdbcUrl, user, password);
  }

  public static void createTable(OrmService srv, Class<?> clazz) {
    String name = clazz.getSimpleName();
    if (name.equals(Guest.class.getSimpleName())) {
      createGuestTable(srv);
    } else if (name.equals(Player.class.getSimpleName())) {
      createPlayerTable(srv);
    } else {
      throw new IllegalArgumentException(clazz + " is illegal");
    }


  }


  private static void createGuestTable(OrmService srv) {
    srv.run(conn -> conn.execute(SQL_CREATE_TABLE_GUESTS));
  }

  private static void createPlayerTable(OrmService srv) {
    srv.run(conn -> conn.execute(SQL_CREATE_TABLE_PLAYERS));
  }


}
