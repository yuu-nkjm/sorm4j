package org.nkjmlab.sorm4j.test.common;

import static org.nkjmlab.sorm4j.util.sql.SqlKeyword.*;
import org.h2.jdbcx.JdbcConnectionPool;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.context.SormContext;
import org.nkjmlab.sorm4j.internal.util.DriverManagerDataSource;
import org.nkjmlab.sorm4j.util.table.BasicTableWithSchema;
import org.nkjmlab.sorm4j.util.table.TableSchema;
import org.nkjmlab.sorm4j.util.table.TableWithSchema;

public class SormTestUtils {
  public static final String JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;";
  public static final String USER = "sa";
  public static final String PASSWORD = "";



  public static final Guest GUEST_ALICE = new Guest("Alice", "Kyoto");
  public static final Guest GUEST_BOB = new Guest("Bob", "Tokyo");
  public static final Guest GUEST_CAROL = new Guest("Carol", "Osaka");
  public static final Guest GUEST_DAVE = new Guest("Dave", "Nara");


  public static final Player PLAYER_ALICE = new Player(1, "Alice", "Kyoto");
  public static final Player PLAYER_BOB = new Player(2, "Bob", "Tokyo");
  public static final Player PLAYER_CAROL = new Player(3, "Carol", "Osaka");
  public static final Player PLAYER_DAVE = new Player(4, "Dave", "Nara");


  public static final Sport TENNIS = new Sport(1, Sport.Sports.TENNIS);
  public static final Sport SOCCER = new Sport(2, Sport.Sports.SOCCER);

  public static final Sorm SORM = createSormWithNewContextAndTables();


  public static TableWithSchema<Guest> createGuestsTable(Sorm sorm) {
    TableSchema schema =
        TableSchema.builder("guests").addColumnDefinition("id", INT, AUTO_INCREMENT, PRIMARY_KEY)
            .addColumnDefinition("name", VARCHAR).addColumnDefinition("address", VARCHAR)
            .addIndexDefinition("name").addIndexDefinition("name").build();


    BasicTableWithSchema<Guest> tbl = new BasicTableWithSchema<>(sorm, Guest.class, schema);
    tbl.dropTableIfExists().createTableIfNotExists().createIndexesIfNotExists();
    return tbl;
  }

  public static TableWithSchema<Player> createPlayersTable(Sorm sorm) {
    return createPlayersTable(sorm, "players");
  }

  public static TableWithSchema<Player> createPlayersTable(Sorm sorm, String tableName) {

    TableSchema schema = TableSchema.builder(tableName).addColumnDefinition("id", INT, PRIMARY_KEY)
        .addColumnDefinition("name", VARCHAR).addColumnDefinition("address", VARCHAR)
        .addIndexDefinition("name").addIndexDefinition("name").build();

    TableWithSchema<Player> tbl = new TableWithSchema<>() {

      @Override
      public TableSchema getTableSchema() {
        return schema;
      }

      @Override
      public Class<Player> getValueType() {
        return Player.class;
      }

      @Override
      public Sorm getSorm() {
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

  public static TableWithSchema<Sport> createSportsTable(Sorm sorm) {
    TableSchema schema = TableSchema.builder("sports").addColumnDefinition("id", INT, PRIMARY_KEY)
        .addColumnDefinition("name", VARCHAR).build();

    TableWithSchema<Sport> tbl = new TableWithSchema<>() {

      @Override
      public TableSchema getTableSchema() {
        return schema;
      }

      @Override
      public Class<Sport> getValueType() {
        return Sport.class;
      }

      @Override
      public Sorm getSorm() {
        return sorm;
      }
    };
    tbl.dropTableIfExists();
    tbl.createTableIfNotExists().createIndexesIfNotExists();
    return tbl;
  }


  public static Sorm createSormWithNewContextAndTables(SormContext sormContext) {
    Sorm sorm = createNewContextSorm(sormContext);
    dropAndCreateSportsTable(sorm);
    dropAndCreateGuestTable(sorm);
    dropAndCreatePlayerTable(sorm);
    return sorm;
  }


  private static Sorm createNewContextSorm(SormContext sormContext) {
    return Sorm.create(Sorm.createDataSource(JDBC_URL, USER, PASSWORD), sormContext);
  }

  public static Sorm createSormWithNewContextAndTables() {
    return createSormWithNewContextAndTables(Sorm.getDefaultContext());
  }


  public static Sorm createNewContextSorm() {
    return createNewContextSorm(SormContext.builder().build());
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

  public static JdbcConnectionPool createDataSourceH2() {
    return JdbcConnectionPool.create(JDBC_URL, USER, PASSWORD);
  }

  public static DriverManagerDataSource createDriverManagerDataSource() {
    return DriverManagerDataSource.create(JDBC_URL, USER, PASSWORD);
  }

}
