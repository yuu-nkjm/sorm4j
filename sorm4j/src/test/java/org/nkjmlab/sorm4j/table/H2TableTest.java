package org.nkjmlab.sorm4j.table;

import java.io.File;
import java.util.function.BiConsumer;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.annotation.OrmRecord;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.util.h2.BasicH2Table;
import org.nkjmlab.sorm4j.util.h2.datasource.H2LocalDataSourceFactory;
import org.nkjmlab.sorm4j.util.table_def.annotation.PrimaryKeyColumns;

/** @see <a href="https://github.com/JRappaz/liverec">JRappaz/liverec</a> */
class H2TableTest {

  private static final org.apache.logging.log4j.Logger log =
      org.apache.logging.log4j.LogManager.getLogger();
  private static final File tableCsv = new File("E:/Desktop/100k_a.csv");
  private static final File tableSql = new File("E:/Desktop/100k_a.sql");

  public static void main(String[] args) {

    run(
        "create table as select to file",
        (mem, file) -> {
          TwitchsTable tbl = new TwitchsTable(file);
          tbl.dropTableIfExists();
          tbl.createTableAsSelectFromCsv(tableCsv);
        });

    run(
        "read from file db and create table and insert into mem db",
        (mem, file) -> {
          TwitchsTable ftbl = new TwitchsTable(file);
          TwitchsTable mtbl = new TwitchsTable(mem);
          mtbl.createTableIfNotExists();
          mtbl.insert(ftbl.selectAll());
        });

    run(
        "create table and insert into",
        (mem, file) -> {
          TwitchsTable tbl = new TwitchsTable(mem);
          tbl.createTableIfNotExists();
          tbl.insertCsv(tableCsv);
        });

    run(
        "runscript to mem",
        (mem, file) -> {
          TwitchsTable tbl = new TwitchsTable(mem);
          tbl.runscript(tableSql);
        });
    run(
        "runscript to file",
        (mem, file) -> {
          TwitchsTable tbl = new TwitchsTable(file);
          tbl.runscript(tableSql);
        });

    run(
        "create table as select to mem",
        (mem, file) -> {
          TwitchsTable tbl = new TwitchsTable(mem);
          tbl.createTableAsSelectFromCsv(tableCsv);
        });
  }

  private static void run(String name, BiConsumer<Sorm, Sorm> func) {
    Sorm mem = Sorm.create(H2LocalDataSourceFactory.createTemporalInMemoryDataSource());
    Sorm file =
        Sorm.create(
            H2LocalDataSourceFactory.builder(new File("E:/Desktop"), "twitch", "sa", "")
                .build()
                .createEmbeddedModeDataSource());
    long start = System.currentTimeMillis();
    log.info("[START] {}", name);
    func.accept(mem, file);
    long end = System.currentTimeMillis() - start;
    int countMem =
        Try.getOrElse(() -> mem.readFirst(Integer.class, "select count(*) from twitchs"), 0);
    int countFile =
        Try.getOrElse(() -> file.readFirst(Integer.class, "select count(*) from twitchs"), 0);
    log.info("[end] {} msec, mem={}, file={}", end, countMem, countFile);
  }

  @OrmRecord
  @PrimaryKeyColumns({"user_id", "stream_id"})
  public static record Twitch(long userId, long streamId, String userName, long start, long stop) {}

  public static class TwitchsTable extends BasicH2Table<Twitch> {

    public TwitchsTable(Sorm orm) {
      super(orm, Twitch.class);
    }
  }
}
