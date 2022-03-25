package org.nkjmlab.sorm4j.test.common;

import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.common.DriverManagerDataSource;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.util.table_def.BasicTableWithDefinition;
import org.nkjmlab.sorm4j.util.table_def.TableDefinition;
import org.nkjmlab.sorm4j.util.table_def.TableWithDefinition;

public class SormTestUtils {

  /**
   * <code>Guest("Alice", "Kyoto")</code>
   */
  public static final Guest GUEST_ALICE = new Guest("Alice", "Kyoto");
  /**
   * <code>Guest("Bob", "Tokyo")</code>
   */
  public static final Guest GUEST_BOB = new Guest("Bob", "Tokyo");
  /**
   * <code>Guest("Carol", "Osaka")</code>
   */
  public static final Guest GUEST_CAROL = new Guest("Carol", "Osaka");
  /**
   * <code>Guest("Dave", "Nara")</code>
   */
  public static final Guest GUEST_DAVE = new Guest("Dave", "Nara");


  /**
   * <code>Player(1, "Alice", "Kyoto")</code>
   */
  public static final Player PLAYER_ALICE = new Player(1, "Alice", "Kyoto");
  /**
   * <code>Player(2, "Bob", "Tokyo")</code>
   */
  public static final Player PLAYER_BOB = new Player(2, "Bob", "Tokyo");
  /**
   * <code>Player(3, "Carol", "Osaka")</code>
   */
  public static final Player PLAYER_CAROL = new Player(3, "Carol", "Osaka");
  /**
   * <code>Player(4, "Dave", "Nara")</code>
   */
  public static final Player PLAYER_DAVE = new Player(4, "Dave", "Nara");


  public static final Sport TENNIS = new Sport(1, Sport.Sports.TENNIS);
  public static final Sport SOCCER = new Sport(2, Sport.Sports.SOCCER);

  public static TableWithDefinition<Guest> createGuestsTable(Sorm sorm) {
    TableDefinition schema = TableDefinition.builder("guests")
        .addColumnDefinition("id", INT, AUTO_INCREMENT, PRIMARY_KEY)
        .addColumnDefinition("name", VARCHAR).addColumnDefinition("address", VARCHAR)
        .addIndexDefinition("name").addIndexDefinition("name").build();


    BasicTableWithDefinition<Guest> tbl = new BasicTableWithDefinition<>(sorm, Guest.class, schema);
    tbl.dropTableIfExists().createTableIfNotExists().createIndexesIfNotExists();
    return tbl;
  }

  public static TableWithDefinition<Player> createPlayersTable(Sorm sorm) {
    return createPlayersTable(sorm, "players");
  }

  public static TableWithDefinition<Player> createPlayersTable(Sorm sorm, String tableName) {

    TableDefinition schema =
        TableDefinition.builder(tableName).addColumnDefinition("id", INT, PRIMARY_KEY)
            .addColumnDefinition("name", VARCHAR).addColumnDefinition("address", VARCHAR)
            .addIndexDefinition("name").addIndexDefinition("name").build();

    TableWithDefinition<Player> tbl = new TableWithDefinition<>() {

      @Override
      public TableDefinition getTableDefinition() {
        return schema;
      }

      @Override
      public Class<Player> getValueType() {
        return Player.class;
      }

      @Override
      public Sorm getOrm() {
        return sorm;
      }

      @Override
      public String getTableName() {
        return schema.getTableName();
      }
    };
    tbl.dropTableIfExists();
    tbl.createTableIfNotExists().createIndexesIfNotExists();
    return tbl;
  }

  public static TableWithDefinition<Sport> createSportsTable(Sorm sorm) {
    TableDefinition schema = TableDefinition.builder("sports")
        .addColumnDefinition("id", INT, PRIMARY_KEY).addColumnDefinition("name", VARCHAR).build();

    TableWithDefinition<Sport> tbl = new TableWithDefinition<>() {

      @Override
      public TableDefinition getTableDefinition() {
        return schema;
      }

      @Override
      public Class<Sport> getValueType() {
        return Sport.class;
      }

      @Override
      public Sorm getOrm() {
        return sorm;
      }
    };
    tbl.dropTableIfExists();
    tbl.createTableIfNotExists().createIndexesIfNotExists();
    return tbl;
  }


  public static Sorm createSormWithNewDatabaseAndCreateTables(SormContext sormContext) {
    Sorm sorm = createSormWithNewDatabase(sormContext);
    dropAndCreateSportsTable(sorm);
    dropAndCreateGuestTable(sorm);
    dropAndCreatePlayerTable(sorm);
    return sorm;
  }


  public static Sorm createSormWithNewDatabaseAndCreateTables() {
    return createSormWithNewDatabaseAndCreateTables(SormContext.builder().build());
  }

  public static Sorm createSormWithNewContext() {
    return createSormWithNewDatabase(SormContext.builder().build());
  }

  private static AtomicInteger urlSuffuix = new AtomicInteger();

  public static DriverManagerDataSource createNewDatabaseDataSource() {
    final String JDBC_URL = "jdbc:h2:mem:test" + urlSuffuix.incrementAndGet()
        + ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
    final String USER = "sa";
    final String PASSWORD = "";
    return DriverManagerDataSource.create(JDBC_URL, USER, PASSWORD);
  }

  private static Sorm createSormWithNewDatabase(SormContext sormContext) {
    return Sorm.create(createNewDatabaseDataSource(), sormContext);
  }

  private static void dropAndCreateSportsTable(Sorm sorm) {
    createSportsTable(sorm).dropTableIfExists().createTableIfNotExists().createIndexesIfNotExists();

  }

  private static void dropAndCreateGuestTable(Sorm sorm) {
    createGuestsTable(sorm).dropTableIfExists().createTableIfNotExists().createIndexesIfNotExists();
  }

  private static void dropAndCreatePlayerTable(Sorm sorm) {
    createPlayersTable(sorm, "players").dropTableIfExists().createTableIfNotExists()
        .createIndexesIfNotExists();
    createPlayersTable(sorm, "players1").dropTableIfExists().createTableIfNotExists()
        .createIndexesIfNotExists();
  }

}
