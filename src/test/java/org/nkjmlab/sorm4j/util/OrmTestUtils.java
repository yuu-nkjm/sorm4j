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

  public static final Player PLAYER_ALICE = new Player(1, "Alice", "Kyoto");
  public static final Player PLAYER_BOB = new Player(2, "Bob", "Tokyo");
  public static final Player PLAYER_CAROL = new Player(3, "Carol", "Osaka");
  public static final Player PLAYER_DAVE = new Player(4, "Dave", "Nara");

  private static final String SQL_CREATE_TABLE_GUESTS =
      "CREATE TABLE IF NOT EXISTS guests (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR, address VARCHAR)";
  private static final String SQL_CREATE_TABLE_PLAYERS =
      "CREATE TABLE IF NOT EXISTS players (id INT PRIMARY KEY, name VARCHAR, address VARCHAR)";

  private static final String SQL_CREATE_TABLE_PLAYERS1 =
      "CREATE TABLE IF NOT EXISTS players1 (id INT PRIMARY KEY, name VARCHAR, address VARCHAR)";

  public static OrmService createOrmService() {
    return OrmService.of(jdbcUrl, user, password);
  }

  public static void dropAndCreateTable(OrmService srv, Class<?> clazz) {
    String name = clazz.getSimpleName();
    if (name.equals(Guest.class.getSimpleName())) {
      dropAndCreateGuestTable(srv);
    } else if (name.equals(Player.class.getSimpleName())) {
      dropAndCreatePlayerTable(srv);
    } else {
      throw new IllegalArgumentException(clazz + " is illegal");
    }


  }


  private static void dropAndCreateGuestTable(OrmService srv) {
    srv.run(conn -> conn.execute("DROP TABLE guests IF EXISTS"));
    srv.run(conn -> conn.execute(SQL_CREATE_TABLE_GUESTS));
  }

  private static void dropAndCreatePlayerTable(OrmService srv) {
    srv.run(conn -> conn.execute("DROP TABLE players IF EXISTS"));
    srv.run(conn -> conn.execute(SQL_CREATE_TABLE_PLAYERS));
    srv.run(conn -> conn.execute("DROP TABLE players1 IF EXISTS"));
    srv.run(conn -> conn.execute(SQL_CREATE_TABLE_PLAYERS1));
  }


}
