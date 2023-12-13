package org.nkjmlab.sorm4j.example.opendata;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.function.Consumer;

import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.annotation.OrmRecord;
import org.nkjmlab.sorm4j.example.opendata.LoadOpenDataExample.ElectronicsTable.Electronic;
import org.nkjmlab.sorm4j.example.opendata.LoadOpenDataExample.ModClothsTable.ModCloth;
import org.nkjmlab.sorm4j.example.opendata.LoadOpenDataExample.TwitchsTable.Twitch;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.util.h2.BasicH2Table;
import org.nkjmlab.sorm4j.util.h2.datasource.H2LocalDataSourceFactory;
import org.nkjmlab.sorm4j.util.h2.server.H2Startup;
import org.nkjmlab.sorm4j.util.h2.sql.CsvReadSql;
import org.nkjmlab.sorm4j.util.table_def.annotation.PrimaryKeyColumns;

public class LoadOpenDataExample {
  private static final org.apache.logging.log4j.Logger log =
      org.apache.logging.log4j.LogManager.getLogger();

  private static final H2LocalDataSourceFactory dataSourceFactory =
      H2LocalDataSourceFactory.builder(new File("$TMPDIR/sorm4j"), "sorm4j_example", "sa", "")
          .build();

  static {
    dataSourceFactory.makeFileDatabaseIfNotExists();
    dataSourceFactory.getDatabaseDirectory().mkdirs();
    log.debug(dataSourceFactory.getMixedModeJdbcUrl());
    log.info("{}", dataSourceFactory.getDatabaseDirectory());
  }

  private Sorm fileDb = Sorm.create(dataSourceFactory.createMixedModeDataSource());

  public static void main(String[] args) {
    H2Startup.startDefaultLocalTcpServer();
    H2Startup.startDefaultWebConsole();
    LoadOpenDataExample example = new LoadOpenDataExample();
    example.loadElectronic();
    example.loadModCloth();
    example.loadTwitch();
  }

  void loadElectronic() {
    ElectronicsTable tbl = new ElectronicsTable(fileDb);
    tbl.dropTableIfExists();
    File csvFile = tbl.getCsv();
    load(
        "create table as select to mem",
        tbl.getTableName(),
        memDb -> {
          tbl.createTableIfNotExists(CsvReadSql.builderForCsvWithHeader(csvFile).build());
        });
  }

  void loadModCloth() {
    ModClothsTable tbl = new ModClothsTable(fileDb);
    tbl.dropTableIfExists();
    File csvFile = tbl.getCsv();
    load(
        "create table as select to mem",
        tbl.getTableName(),
        memDb -> {
          tbl.createTableIfNotExists(CsvReadSql.builderForCsvWithHeader(csvFile).build());
        });
  }

  void loadTwitch() {
    TwitchsTable tbl = new TwitchsTable(fileDb);
    tbl.dropTableIfExists();
    File csvFile = tbl.getCsv();

    load(
        "create table as select to mem",
        tbl.getTableName(),
        memDb -> {
          tbl.createTableIfNotExists(CsvReadSql.builderForCsvWithoutHeader(csvFile, 5).build());
        });
  }

  private static File downloadFile(String fileURL, String fileName) {
    File outFile = new File(dataSourceFactory.getDatabaseDirectory(), fileName);
    if (outFile.exists()) {
      return outFile;
    }

    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(fileURL)).GET().build();

    try {
      HttpResponse<Path> response =
          client.send(request, HttpResponse.BodyHandlers.ofFile(outFile.toPath()));
      log.debug("Download completed: {}", response.body());
      return outFile;
    } catch (IOException | InterruptedException e) {
      throw Try.rethrow(e);
    }
  }

  /**
   * @see <a href="https://github.com/MengtingWan/marketBias/tree/master/data">marketBias/data at
   *     master · MengtingWan/marketBias</a>
   */
  public static class ModClothsTable extends BasicH2Table<ModCloth> {

    @OrmRecord
    @PrimaryKeyColumns({"item_id", "user_id"})
    public static record ModCloth(
        int item_id,
        String user_id,
        int rating,
        LocalDateTime timestamp,
        String size,
        String fit,
        String user_attr,
        String model_attr,
        String category,
        String brand,
        int yearOfItem,
        int split) {}

    public ModClothsTable(Sorm orm) {
      super(orm, ModCloth.class);
    }

    public File getCsv() {
      return downloadFile(
          "https://raw.githubusercontent.com/MengtingWan/marketBias/master/data/df_modcloth.csv",
          "df_modcloth.csv");
    }
  }

  public static class ElectronicsTable extends BasicH2Table<Electronic> {

    @OrmRecord
    @PrimaryKeyColumns({"item_id", "user_id"})
    public static record Electronic(
        int item_id,
        String user_id,
        double rating,
        LocalDateTime timestamp,
        String model_attr,
        String category,
        String brand,
        int yearOfItem,
        String user_attr,
        int split) {}

    public ElectronicsTable(Sorm orm) {
      super(orm, Electronic.class);
    }

    public File getCsv() {
      return downloadFile(
          "https://raw.githubusercontent.com/MengtingWan/marketBias/master/data/df_electronics.csv",
          "df_electronics.csv");
    }
  }

  public static class TwitchsTable extends BasicH2Table<Twitch> {

    /**
     * This is a dataset of users consuming streaming content on Twitch. We retrieved all streamers,
     * and all users connected in their respective chats, every 10 minutes during 43 days.
     *
     * <pre>
     * user_id: user identifier (anonymized).
     * stream id: stream identifier, could be used to retreive a single broadcast segment (not used in our study).
     * streamer name: name of the channel.
     * start time: first crawling round at which the user was seen in the chat.
     * stop time: last crawling round at which the user was seen in the chat.</pre>
     *
     * @see <a href="https://github.com/JRappaz/liverec">JRappaz/liverec</a>
     */
    @OrmRecord
    @PrimaryKeyColumns({"user_id", "stream_id"})
    public static record Twitch(
        long userId, long streamId, String streamer, long startTime, long stopTime) {}

    public TwitchsTable(Sorm orm) {
      super(orm, Twitch.class);
    }

    public File getCsv() {
      return new File(dataSourceFactory.getDatabaseDirectory(), "100k_a.csv");
    }
  }

  private void load(String logLabel, String tableName, Consumer<Sorm> func) {
    Sorm mem = Sorm.create(H2LocalDataSourceFactory.createTemporalInMemoryDataSource());
    long start = System.currentTimeMillis();
    log.info("[START] {} - {}", logLabel, tableName);
    func.accept(mem);
    long end = System.currentTimeMillis() - start;
    int countMem =
        Try.getOrElse(() -> mem.readFirst(Integer.class, "select count(*) from " + tableName), 0);
    int countFile =
        Try.getOrElse(
            () -> fileDb.readFirst(Integer.class, "select count(*) from " + tableName), 0);
    log.info("[end] {} msec, mem={}, file={}", end, countMem, countFile);
  }
}
