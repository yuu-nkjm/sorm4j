package org.nkjmlab.sorm4j.test.common;

import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.AUTO_INCREMENT;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.INT;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.PRIMARY_KEY;
import static org.nkjmlab.sorm4j.sql.statement.SqlKeyword.VARCHAR;

import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.table.definition.TableDefinition;
import org.nkjmlab.sorm4j.table.orm.DefinedTable;
import org.nkjmlab.sorm4j.util.datasource.DataSourceFactory;

public class SormTestUtils {

  /** <code>Guest("Alice", "Kyoto")</code> */
  public static final Guest GUEST_ALICE = Guest.of("Alice", "Kyoto");

  /** <code>Guest("Bob", "Tokyo")</code> */
  public static final Guest GUEST_BOB = Guest.of("Bob", "Tokyo");

  /** <code>Guest("Carol", "Osaka")</code> */
  public static final Guest GUEST_CAROL = Guest.of("Carol", "Osaka");

  /** <code>Guest("Dave", "Nara")</code> */
  public static final Guest GUEST_DAVE = Guest.of("Dave", "Nara");

  /** <code>Player(1, "Alice", "Kyoto")</code> */
  public static final Player PLAYER_ALICE = new Player(1, "Alice", "Kyoto");

  /** <code>Player(2, "Bob", "Tokyo")</code> */
  public static final Player PLAYER_BOB = new Player(2, "Bob", "Tokyo");

  /** <code>Player(3, "Carol", "Osaka")</code> */
  public static final Player PLAYER_CAROL = new Player(3, "Carol", "Osaka");

  /** <code>Player(4, "Dave", "Nara")</code> */
  public static final Player PLAYER_DAVE = new Player(4, "Dave", "Nara");

  public static final Sport TENNIS = new Sport(1, Sport.Sports.TENNIS);
  public static final Sport SOCCER = new Sport(2, Sport.Sports.SOCCER);

  public static DefinedTable<Guest> createGuestsTable(Sorm sorm) {
    TableDefinition schema =
        TableDefinition.builder("guests")
            .addColumnDefinition("id", INT, AUTO_INCREMENT, PRIMARY_KEY)
            .addColumnDefinition("name", VARCHAR)
            .addColumnDefinition("address", VARCHAR)
            .addIndexDefinition("name")
            .addIndexDefinition("name")
            .build();

    DefinedTable<Guest> tbl = DefinedTable.of(sorm, Guest.class, schema);
    tbl.dropTableIfExists().createTableIfNotExists().createIndexesIfNotExists();
    return tbl;
  }

  public static DefinedTable<Player> createPlayersTable(Sorm sorm) {
    return createPlayersTable(sorm, "players");
  }

  public static DefinedTable<Player> createPlayersTable(Sorm sorm, String tableName) {

    TableDefinition schema =
        TableDefinition.builder(tableName)
            .addColumnDefinition("id", INT, PRIMARY_KEY)
            .addColumnDefinition("name", VARCHAR)
            .addColumnDefinition("address", VARCHAR)
            .addIndexDefinition("name")
            .addIndexDefinition("name")
            .build();

    DefinedTable<Player> tbl = DefinedTable.of(sorm, Player.class, schema);
    tbl.dropTableIfExists();
    tbl.createTableIfNotExists().createIndexesIfNotExists();
    return tbl;
  }

  public static DefinedTable<Sport> createSportsTable(Sorm sorm) {
    TableDefinition schema =
        TableDefinition.builder("sports")
            .addColumnDefinition("id", INT, PRIMARY_KEY)
            .addColumnDefinition("name", VARCHAR)
            .build();

    DefinedTable<Sport> tbl = DefinedTable.of(sorm, Sport.class, schema);

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

  private static final AtomicInteger urlSuffuix = new AtomicInteger();

  public static DataSource createNewDatabaseDataSource() {
    final String JDBC_URL =
        "jdbc:h2:mem:test" + urlSuffuix.incrementAndGet() + ";DB_CLOSE_DELAY=-1";
    return DataSourceFactory.create(JDBC_URL);
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
    createPlayersTable(sorm, "players")
        .dropTableIfExists()
        .createTableIfNotExists()
        .createIndexesIfNotExists();
    createPlayersTable(sorm, "players1")
        .dropTableIfExists()
        .createTableIfNotExists()
        .createIndexesIfNotExists();
  }
}
