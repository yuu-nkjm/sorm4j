package org.nkjmlab.sorm4j.common;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Properties;
import javax.sql.DataSource;
import org.h2.jdbcx.JdbcConnectionPool;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.SormFactory;

public class SormTestUtils {
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

  private static final String SQL_CREATE_TABLE_LOCATIONS =
      "CREATE TABLE IF NOT EXISTS locations (id INT PRIMARY KEY, name VARCHAR)";

  private static final String SQL_CREATE_TABLE_GUESTS =
      "CREATE TABLE IF NOT EXISTS guests (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR, address VARCHAR)";
  private static final String SQL_CREATE_TABLE_PLAYERS =
      "CREATE TABLE IF NOT EXISTS players (id INT PRIMARY KEY, name VARCHAR, address VARCHAR)";

  private static final String SQL_CREATE_TABLE_PLAYERS1 =
      "CREATE TABLE IF NOT EXISTS players1 (id INT PRIMARY KEY, name VARCHAR, address VARCHAR)";
  public static final Location LOCATION_TOKYO = new Location(1, Location.Place.TOKYO);
  public static final Location LOCATION_KYOTO = new Location(2, Location.Place.KYOTO);

  public static Sorm createSormAndDropAndCreateTableAll() {
    Sorm sorm = createSorm();
    dropAndCreateTableAll(sorm);
    return sorm;
  }

  public static Sorm createSorm() {
    Sorm ret = SormFactory.create(jdbcUrl, user, password);
    return ret;
  }

  public static Sorm createSorm(String confName) {
    Sorm ret = SormFactory.create(jdbcUrl, user, password, confName);
    return ret;

  }

  public static void dropAndCreateTableAll(Sorm sorm) {
    dropAndCreateGuestTable(sorm);
    dropAndCreatePlayerTable(sorm);
    dropAndCreateLocationTable(sorm);
    dropAndCreateCustomerTable(sorm);
  }

  private static void dropAndCreateCustomerTable(Sorm sorm) {
    sorm.accept(conn -> conn.executeUpdate("DROP TABLE customer IF EXISTS"));
    sorm.accept(conn -> conn.executeUpdate(Customer.CREATE_TABLE_SQL));
  }

  private static void dropAndCreateLocationTable(Sorm sorm) {
    sorm.accept(conn -> conn.executeUpdate("DROP TABLE locations IF EXISTS"));
    sorm.accept(conn -> conn.executeUpdate(SQL_CREATE_TABLE_LOCATIONS));
  }

  private static void dropAndCreateGuestTable(Sorm sorm) {
    sorm.accept(conn -> conn.executeUpdate("DROP TABLE guests IF EXISTS"));
    sorm.accept(conn -> conn.executeUpdate(SQL_CREATE_TABLE_GUESTS));
  }

  private static void dropAndCreatePlayerTable(Sorm sorm) {
    sorm.accept(conn -> conn.executeUpdate("DROP TABLE players IF EXISTS"));
    sorm.accept(conn -> conn.executeUpdate(SQL_CREATE_TABLE_PLAYERS));
    sorm.accept(conn -> conn.executeUpdate("DROP TABLE players1 IF EXISTS"));
    sorm.accept(conn -> conn.executeUpdate(SQL_CREATE_TABLE_PLAYERS1));
  }

  public static DataSource createDataSourceH2() {
    return createDataSourceH2(jdbcUrl, user, password);
  }


  private static DataSource createDataSourceH2(String url, String user, String password) {
    return JdbcConnectionPool.create(url, user, password);
  }

  public static DataSource getDataSource(Class<?> clazz, String defaultJdbcUrl) {
    try {
      Properties properties = new Properties();
      properties.load(clazz.getResourceAsStream("db.properties"));
      String url = properties.getProperty("url");
      String user = properties.getProperty("username");
      String password = properties.getProperty("password");
      return SormFactory.create(url, user, password).getDataSource();
    } catch (Exception e) {
      return JdbcConnectionPool.create(defaultJdbcUrl, "sorm", "sorm");
    }
  }

  public static void executeTableSchema(Class<?> clazz, DataSource dataSource) {
    try (Connection conn = dataSource.getConnection()) {
      Statement st = conn.createStatement();
      String[] sqls = String
          .join(System.lineSeparator(),
              Files.readAllLines(new File(clazz.getResource("schema.sql").toURI()).toPath()))
          .split(";");
      Arrays.asList(sqls).subList(0, sqls.length - 1).forEach(sql -> {
        try {
          sql = sql.trim();
          st.executeUpdate(sql);
        } catch (SQLException e) {
          System.err.println(sql);
          e.printStackTrace();
        }
      });
    } catch (SQLException | URISyntaxException | IOException e) {
      e.printStackTrace();
    }

  }


}
