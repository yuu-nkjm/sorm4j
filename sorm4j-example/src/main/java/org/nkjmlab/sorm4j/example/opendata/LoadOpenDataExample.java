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
import org.nkjmlab.sorm4j.example.opendata.LoadOpenDataExample.ElectronicsTable.Electronic;
import org.nkjmlab.sorm4j.example.opendata.LoadOpenDataExample.ModClothsTable.ModCloth;
import org.nkjmlab.sorm4j.extension.h2.datasource.H2DataSourceFactory;
import org.nkjmlab.sorm4j.extension.h2.datasource.H2DataSourceFactory.Config;
import org.nkjmlab.sorm4j.extension.h2.functions.table.CsvRead;
import org.nkjmlab.sorm4j.extension.h2.orm.table.definition.H2DefinedTableBase;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.nkjmlab.sorm4j.table.definition.annotation.PrimaryKeyConstraint;

public class LoadOpenDataExample {
  private static final org.apache.logging.log4j.Logger log =
      org.apache.logging.log4j.LogManager.getLogger();

  private static final H2DataSourceFactory dataSourceFactory =
      H2DataSourceFactory.of(Config.of(Path.of("$TMPDIR/sorm4j"), "sorm4j_example", "sa", ""));

  static {
    dataSourceFactory.makeDatabaseFileIfNotExists();
    log.debug(dataSourceFactory.getMixedModeJdbcUrl());
    log.info("{}", dataSourceFactory.getDatabaseDirectoryPath());
  }

  private Sorm fileDb = Sorm.create(dataSourceFactory.createEmbeddedModeDataSource());

  public static void main(String[] args) {
    LoadOpenDataExample example = new LoadOpenDataExample();
    example.loadElectronic();
    example.loadModCloth();
  }

  void loadElectronic() {
    ElectronicsTable tbl = new ElectronicsTable(fileDb);
    tbl.dropTableIfExists();
    File csvFile = tbl.getCsv();
    load(
        "create table as select to mem",
        tbl.getTableName(),
        memDb -> {
          tbl.createTableIfNotExists(CsvRead.builderForCsvWithHeader(csvFile).build());
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
          tbl.createTableIfNotExists(CsvRead.builderForCsvWithHeader(csvFile).build());
        });
  }

  private static File downloadFile(String fileURL, String fileName) {
    File outFile = new File(dataSourceFactory.getDatabaseDirectoryPath().toFile(), fileName);
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
  public static class ModClothsTable extends H2DefinedTableBase<ModCloth> {

    @PrimaryKeyConstraint("item_id, user_id")
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

  public static class ElectronicsTable extends H2DefinedTableBase<Electronic> {

    @PrimaryKeyConstraint("item_id, user_id")
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

  private void load(String logLabel, String tableName, Consumer<Sorm> func) {
    Sorm mem = Sorm.create(H2DataSourceFactory.createTemporalInMemoryDataSource());
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
