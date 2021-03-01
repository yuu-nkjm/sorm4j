package org.nkjmlab.sorm4j.performance;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.nkjmlab.sorm4j.OrmMapper;
import org.nkjmlab.sorm4j.util.DataSourceHelper;
import org.sql2o.Sql2o;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import net.sf.persist.Persist;

public class PerformanceH2 {


  private final DataSourceHelper connectionHelper;
  private final String mode;
  private final File reportPath;

  private final int inserts = 5000;
  private final int batchInserts = 1000;
  private final int reads = 1000;
  private final int readLists = 5;
  private final int batchSize = 32;
  private Sql2o sql2o;

  public PerformanceH2(String mode) {
    File reportDir = new File("./tmp/");
    reportDir.mkdirs();
    this.mode = mode;
    this.reportPath = new File(reportDir, mode + "-report.html");
    this.connectionHelper = new DataSourceHelper(getPackageNameToPath() + mode + ".h2.properties",
        url -> user -> pwd -> DataSourceHelper.createDataSourceHikari(url, user, pwd));
    this.sql2o = prepareSql2o(connectionHelper);


  }

  public static void main(String[] args) throws SQLException, IOException {
    // waitInput();
    List.of("h2.mem").forEach(mode -> {
      PerformanceH2 performaceH2 = new PerformanceH2(mode);
      performaceH2.run();
    });

  }


  private static Sql2o prepareSql2o(DataSourceHelper connectionHelper) {
    Sql2o sql2o = new Sql2o(connectionHelper.getDataSource());
    Map<String, String> mappings = new HashMap<>();
    mappings.put("INT_COL", "intCol");
    mappings.put("BOOLEAN_COL", "booleanCol");
    mappings.put("TINYINT_COL", "tinyintCol");
    mappings.put("SMALLINT_COL", "smallintCol");
    mappings.put("BIGINT_COL", "bigintCol");
    mappings.put("DECIMAL_COL", "decimalCol");
    mappings.put("DOUBLE_COL", "doubleCol");
    mappings.put("REAL_COL", "realCol");
    mappings.put("TIME_COL", "timeCol");
    mappings.put("DATE_COL", "dateCol");
    mappings.put("TIMESTAMP_COL", "timestampCol");
    mappings.put("BINARY_COL", "binaryCol");
    mappings.put("BLOB_COL", "blobCol");
    mappings.put("OTHER_COL", "otherCol");
    mappings.put("UUID_COL", "uuidCol");
    mappings.put("VARCHAR_COL", "varcharCol");
    mappings.put("VARCHAR_IGNORECASE_COL", "varcharIgnorecaseCol");
    mappings.put("CHAR_COL", "charCol");
    mappings.put("CLOB_COL", "clobCol");
    mappings.put("ID", "id");
    sql2o.setDefaultColumnMappings(mappings);
    return sql2o;
  }



  private void run() {
    dropAndCreateTable();
    profileMultiInsert();
    profileSingleInsert();
    profileReadSingleByPrimaryKey();
    profileReadListAll();
    System.out.println();
    System.out.println("finished");
    createReport(getPackageNameToPath(PerformanceH2.class), reportPath);
  }


  private void dropAndCreateTable() {
    try (Connection connection = connectionHelper.getConnection()) {
      OrmMapper orm = OrmMapper.of(connection);
      connection.createStatement().execute("drop table if exists all_types");
      connection.createStatement().execute(
          "create table if not exists all_types ( int_col int, boolean_col boolean, tinyint_col tinyint, smallint_col smallint, bigint_col bigint, decimal_col decimal, double_col double, real_col real, time_col time, date_col date, timestamp_col timestamp, binary_col binary, blob_col blob, other_col other, uuid_col uuid, varchar_col varchar, varchar_ignorecase_col varchar_ignorecase, char_col char, clob_col clob, id int auto_increment primary key )");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void profileMultiInsert() {

    AllTypes[] as =
        Stream.generate(() -> buildAllTypes()).limit(batchSize).toArray(AllTypes[]::new);
    System.out.print("batch insert");
    String batchSql =
        "insert into ALL_TYPES(INT_COL,BOOLEAN_COL,TINYINT_COL,SMALLINT_COL,BIGINT_COL,DECIMAL_COL,"
            + "DOUBLE_COL,REAL_COL,TIME_COL,DATE_COL,TIMESTAMP_COL,BINARY_COL,BLOB_COL,OTHER_COL,UUID_COL,"
            + "VARCHAR_COL,VARCHAR_IGNORECASE_COL,CHAR_COL,CLOB_COL)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    String multiRowSql =
        "insert into ALL_TYPES(INT_COL,BOOLEAN_COL,TINYINT_COL,SMALLINT_COL,BIGINT_COL,DECIMAL_COL,"
            + "DOUBLE_COL,REAL_COL,TIME_COL,DATE_COL,TIMESTAMP_COL,BINARY_COL,BLOB_COL,OTHER_COL,UUID_COL,"
            + "VARCHAR_COL,VARCHAR_IGNORECASE_COL,CHAR_COL,CLOB_COL)values"
            + String.join(",", Stream.generate(() -> "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)")
                .limit(batchSize).collect(Collectors.toList()));

    for (int i = 1; i <= batchInserts; i++) {
      if (i % 10 == 0)
        System.out.print(" " + i);
      if (i % 250 == 0)
        System.out.println();

      final int warmUp = 10;

      wrapMonitor(i, warmUp, "[insert multi batch] jdbc", () -> execJdbcBatchInsert(batchSql, as));
      wrapMonitor(i, warmUp, "[insert multi multirow] orm", () -> execOrmMultiRowInsert(as));
      wrapMonitor(i, warmUp, "[insert multi multirow] jdbc",
          () -> execJdbcMultiRowInsert(multiRowSql, as));

    }
    System.out.println();
  }

  private void wrapMonitor(int i, int warmUp, String label, Runnable task) {
    if (i <= warmUp) {
      task.run();
      return;
    }
    Monitor mon = MonitorFactory.start("[" + mode + "]" + label);
    task.run();
    mon.stop();
  }


  private void execOrmMultiRowInsert(AllTypes[] as) {
    try (Connection connection = connectionHelper.getConnection()) {
      OrmMapper.of(connection).insert(as);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void execJdbcMultiRowInsert(String sql, AllTypes[] as) {
    try (Connection connection = connectionHelper.getConnection()) {
      PreparedStatement stmt = connection.prepareStatement(sql);

      for (int i = 0; i < as.length; i++) {
        AllTypes a = as[i];
        stmt.setLong((19 * i) + 1, a.getIntCol());
        stmt.setBoolean((19 * i) + 2, a.getBooleanCol());
        stmt.setInt((19 * i) + 3, a.getTinyintCol());
        stmt.setInt((19 * i) + 4, a.getSmallintCol());
        stmt.setLong((19 * i) + 5, a.getBigintCol());
        stmt.setLong((19 * i) + 6, a.getDecimalCol());
        stmt.setDouble((19 * i) + 7, a.getDoubleCol());
        stmt.setFloat((19 * i) + 8, a.getRealCol());
        stmt.setTime((19 * i) + 9, a.getTimeCol());
        stmt.setDate((19 * i) + 10, a.getDateCol());
        stmt.setTimestamp((19 * i) + 11, a.getTimestampCol());
        stmt.setBytes((19 * i) + 12, a.getBinaryCol());
        stmt.setBytes((19 * i) + 13, a.getBlobCol());
        stmt.setObject((19 * i) + 14, a.getOtherCol());
        stmt.setBytes((19 * i) + 15, a.getUuidCol());
        stmt.setString((19 * i) + 16, a.getVarcharCol());
        stmt.setString((19 * i) + 17, a.getVarcharIgnorecaseCol());
        stmt.setString((19 * i) + 18, a.getCharCol());
        stmt.setString((19 * i) + 19, a.getClobCol());
      }
      stmt.executeUpdate();
      stmt.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void execJdbcBatchInsert(String sql, AllTypes[] as) {
    try (Connection connection = connectionHelper.getConnection()) {
      PreparedStatement stmt = null;
      for (AllTypes a : as) {
        stmt = connection.prepareStatement(sql);
        stmt.setLong(1, a.getIntCol());
        stmt.setBoolean(2, a.getBooleanCol());
        stmt.setInt(3, a.getTinyintCol());
        stmt.setInt(4, a.getSmallintCol());
        stmt.setLong(5, a.getBigintCol());
        stmt.setLong(6, a.getDecimalCol());
        stmt.setDouble(7, a.getDoubleCol());
        stmt.setFloat(8, a.getRealCol());
        stmt.setTime(9, a.getTimeCol());
        stmt.setDate(10, a.getDateCol());
        stmt.setTimestamp(11, a.getTimestampCol());
        stmt.setBytes(12, a.getBinaryCol());
        stmt.setBytes(13, a.getBlobCol());
        stmt.setObject(14, a.getOtherCol());
        stmt.setBytes(15, a.getUuidCol());
        stmt.setString(16, a.getVarcharCol());
        stmt.setString(17, a.getVarcharIgnorecaseCol());
        stmt.setString(18, a.getCharCol());
        stmt.setString(19, a.getClobCol());
        stmt.addBatch();
      }
      stmt.executeBatch();
      stmt.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void profileSingleInsert() {
    AllTypes a = buildAllTypes();

    System.out.print("insert");

    String sql =
        "insert into ALL_TYPES(INT_COL,BOOLEAN_COL,TINYINT_COL,SMALLINT_COL,BIGINT_COL,DECIMAL_COL,"
            + "DOUBLE_COL,REAL_COL,TIME_COL,DATE_COL,TIMESTAMP_COL,BINARY_COL,BLOB_COL,OTHER_COL,UUID_COL,"
            + "VARCHAR_COL,VARCHAR_IGNORECASE_COL,CHAR_COL,CLOB_COL)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    String sqlForSql2o =
        "insert into ALL_TYPES(INT_COL,BOOLEAN_COL,TINYINT_COL,SMALLINT_COL,BIGINT_COL,DECIMAL_COL,"
            + "DOUBLE_COL,REAL_COL,TIME_COL,DATE_COL,TIMESTAMP_COL,BINARY_COL,BLOB_COL,OTHER_COL,UUID_COL,"
            + "VARCHAR_COL,VARCHAR_IGNORECASE_COL,CHAR_COL,CLOB_COL)values(:intCol,:booleanCol,:tinyintCol,:smallintCol,:bigintCol,:decimalCol,:doubleCol,:realCol,:timeCol,:dateCol,:timestampCol,:binaryCol,:blobCol,:otherCol,:uuidCol,:varcharCol,:varcharIgnorecaseCol,:charCol,:clobCol)";


    for (int i = 1; i <= inserts; i++) {
      if (i % 10 == 0)
        System.out.print(" " + i);
      if (i % 250 == 0)
        System.out.println();

      final int warmUp = 10;

      wrapMonitor(i, warmUp, "[insert single] jdbc", () -> execJdbcSingleInsert(sql, a));
      wrapMonitor(i, warmUp, "[insert single] orm", () -> execOrmSingleInsert(a));
      wrapMonitor(i, warmUp, "[insert single] persist", () -> execPersistSingleInsert(a));
      wrapMonitor(i, warmUp, "[insert single] sql2o", () -> execSql2oSingleInsert(sqlForSql2o, a));


    }
    System.out.println();
  }

  private void execPersistSingleInsert(AllTypes a) {
    try (Connection connection = connectionHelper.getConnection()) {
      Persist persist = new Persist(connection);
      persist.setUpdateAutoGeneratedKeys(true);
      persist.insert(a);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void execSql2oSingleInsert(String sql, AllTypes a) {
    try (org.sql2o.Connection conn = sql2o.open()) {
      conn.createQuery(sql).bind(a).executeUpdate();
    }
  }

  private void execOrmSingleInsert(AllTypes a) {
    try (Connection connection = connectionHelper.getConnection()) {
      OrmMapper.of(connection).insert(a);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void execJdbcSingleInsert(String sql, AllTypes a) {
    try (Connection connection = connectionHelper.getConnection()) {
      PreparedStatement stmt = connection.prepareStatement(sql);
      stmt.setLong(1, a.getIntCol());
      stmt.setBoolean(2, a.getBooleanCol());
      stmt.setInt(3, a.getTinyintCol());
      stmt.setInt(4, a.getSmallintCol());
      stmt.setLong(5, a.getBigintCol());
      stmt.setLong(6, a.getDecimalCol());
      stmt.setDouble(7, a.getDoubleCol());
      stmt.setFloat(8, a.getRealCol());
      stmt.setTime(9, a.getTimeCol());
      stmt.setDate(10, a.getDateCol());
      stmt.setTimestamp(11, a.getTimestampCol());
      stmt.setBytes(12, a.getBinaryCol());
      stmt.setBytes(13, a.getBlobCol());
      stmt.setObject(14, a.getOtherCol());
      stmt.setBytes(15, a.getUuidCol());
      stmt.setString(16, a.getVarcharCol());
      stmt.setString(17, a.getVarcharIgnorecaseCol());
      stmt.setString(18, a.getCharCol());
      stmt.setString(19, a.getClobCol());
      stmt.executeUpdate();
      stmt.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void profileReadSingleByPrimaryKey() {
    try (Connection connection = connectionHelper.getConnection()) {
      OrmMapper persist = OrmMapper.of(connection);
      // find a single id to be used by select by primary key
      int id = persist.readFirst(int.class, "select min(id) from all_types");



      // read

      System.out.print("read");

      String typicalSqlPrefx = "select * from all_types ";
      String typicalSql = typicalSqlPrefx + "where id=?";
      String optimizedSqlPrefix = "select "
          + "INT_COL,BOOLEAN_COL,TINYINT_COL,SMALLINT_COL,BIGINT_COL,DECIMAL_COL,"
          + "DOUBLE_COL,REAL_COL,TIME_COL,DATE_COL,TIMESTAMP_COL,BINARY_COL,BLOB_COL,OTHER_COL,UUID_COL,"
          + "VARCHAR_COL,VARCHAR_IGNORECASE_COL,CHAR_COL,CLOB_COL,ID from all_types";
      String optimizedSql = optimizedSqlPrefix + " where id=?";

      for (int i = 1; i <= reads; i++) {
        if (i % 50 == 0)
          System.out.print(" " + i);
        if (i % 1000 == 0)
          System.out.println();

        final String TYPICAL = " typical";
        final String OPTIMIZED = " optimized";

        final int warmUp = 10;

        wrapMonitor(i, warmUp, "[read single] jdbc" + OPTIMIZED,
            () -> execJdbcSingleRead(optimizedSql, id));
        wrapMonitor(i, warmUp, "[read single] orm" + OPTIMIZED, () -> execOrmSingleRead(id));
        wrapMonitor(i, warmUp, "[read single] orm map" + OPTIMIZED,
            () -> execOrmMapSingleRead(optimizedSql, id));

        wrapMonitor(i, warmUp, "[read single] persist" + OPTIMIZED,
            () -> execPersistSingleRead(id));

        wrapMonitor(i, warmUp, "[read single] jdbc" + TYPICAL,
            () -> execJdbcSingleRead(typicalSql, id));
        wrapMonitor(i, warmUp, "[read single] orm" + TYPICAL, () -> execOrmSingleRead(id));
        wrapMonitor(i, warmUp, "[read single] orm map" + TYPICAL,
            () -> execOrmMapSingleRead(typicalSql, id));

        String typicalSql2o = typicalSqlPrefx + " WHERE id = :id";
        String optimizedSql2o = optimizedSqlPrefix + " WHERE id = :id";
        wrapMonitor(i, warmUp, "[read single] sql2o" + OPTIMIZED,
            () -> execSql2oSingleRead(sql2o, optimizedSql2o, id));
        wrapMonitor(i, warmUp, "[read single] sql2o" + TYPICAL,
            () -> execSql2oSingleRead(sql2o, typicalSql2o, id));

      }
      System.out.println();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  private void execSql2oSingleRead(Sql2o sql2o, String sql, int id) {
    try (org.sql2o.Connection conn = sql2o.open()) {
      org.sql2o.Query query = conn.createQuery(sql);
      query.addParameter("id", id).executeAndFetchFirst(AllTypes.class);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  private void execPersistSingleRead(int id) {
    try (Connection connection = connectionHelper.getConnection()) {
      Persist persist = new Persist(connection);
      persist.readByPrimaryKey(AllTypes.class, id);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  private void execOrmSingleRead(int id) {
    try (Connection connection = connectionHelper.getConnection()) {
      OrmMapper orm = OrmMapper.of(connection);
      orm.readByPrimaryKey(AllTypes.class, id);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void execOrmMapSingleRead(String sql, int id) {
    try (Connection connection = connectionHelper.getConnection()) {
      OrmMapper persist = OrmMapper.of(connection);
      persist.readMapFirst(sql, id);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void execJdbcSingleRead(String sql, int id) {
    try (Connection connection = connectionHelper.getConnection()) {
      PreparedStatement stmt = connection.prepareStatement(sql);
      stmt.setLong(1, id);
      ResultSet rs = stmt.executeQuery();
      rs.next();
      AllTypes a = new AllTypes();
      a.setIntCol(rs.getLong(1));
      a.setBooleanCol(rs.getBoolean(2));
      a.setTinyintCol(rs.getInt(3));
      a.setSmallintCol(rs.getInt(4));
      a.setBigintCol(rs.getLong(5));
      a.setDecimalCol(rs.getLong(6));
      a.setDoubleCol(rs.getDouble(7));
      a.setRealCol(rs.getFloat(8));
      a.setTimeCol(rs.getTime(9));
      a.setDateCol(rs.getDate(10));
      a.setTimestampCol(rs.getTimestamp(11));
      a.setBinaryCol(rs.getBytes(12));
      a.setBlobCol(rs.getBytes(13));
      a.setOtherCol(rs.getObject(14));
      a.setUuidCol(rs.getBytes(15));
      a.setVarcharCol(rs.getString(16));
      a.setVarcharIgnorecaseCol(rs.getString(17));
      a.setCharCol(rs.getString(18));
      a.setClobCol(rs.getString(19));
      stmt.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void profileReadListAll() {
    System.out.print("read list");
    for (int i = 1; i <= readLists; i++) {
      if (i % 1 == 0)
        System.out.print(" " + i);
      if (i % 20 == 0)
        System.out.println();
      String sql = "select * from all_types";
      execJdbcReadListAll(sql);
      execOrmReadListAll(sql);
      execOrmReadListAllLazy(sql);
      execOrmMapReadListAll(sql);
      execSql2oReadAll(sql2o, sql);
    }
    System.out.println();
  }

  private void execSql2oReadAll(Sql2o sql2o, String sql) {
    Monitor mon = MonitorFactory.start("[" + mode + "]" + "[read list all] sql2o");
    try (org.sql2o.Connection conn = sql2o.open()) {
      org.sql2o.Query query = conn.createQuery(sql);
      query.executeAndFetch(AllTypes.class);
    } catch (Exception e) {
      e.printStackTrace();
    }
    mon.stop();
  }

  private void execOrmReadListAll(String sql) {
    Monitor mon = MonitorFactory.start("[" + mode + "]" + "[read list all] orm");
    try (Connection connection = connectionHelper.getConnection()) {
      OrmMapper persist = OrmMapper.of(connection);
      persist.readAll(AllTypes.class);
    } catch (Exception e) {
      e.printStackTrace();
    }
    mon.stop();
  }

  private void execOrmReadListAllLazy(String sql) {
    Monitor mon = MonitorFactory.start("[" + mode + "]" + "[read list all lazy] orm");
    try (Connection connection = connectionHelper.getConnection()) {
      OrmMapper persist = OrmMapper.of(connection);
      persist.readAllLazy(AllTypes.class).stream().collect(Collectors.toList());
    } catch (Exception e) {
      e.printStackTrace();
    }
    mon.stop();
  }

  private void execOrmMapReadListAll(String sql) {
    Monitor mon = MonitorFactory.start("[" + mode + "]" + "[read list all] orm map");
    try (Connection connection = connectionHelper.getConnection()) {
      OrmMapper persist = OrmMapper.of(connection);
      persist.readMapList("select * from all_types");
    } catch (Exception e) {
      e.printStackTrace();
    }
    mon.stop();
  }

  private void execJdbcReadListAll(String sql) {
    Monitor mon = MonitorFactory.start("[" + mode + "]" + "[read list all] jdbc");
    List<AllTypes> al = new ArrayList<>();
    try (Connection connection = connectionHelper.getConnection()) {
      PreparedStatement stmt = connection.prepareStatement(sql);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        AllTypes a = new AllTypes();
        a.setIntCol(rs.getLong(1));
        a.setBooleanCol(rs.getBoolean(2));
        a.setTinyintCol(rs.getInt(3));
        a.setSmallintCol(rs.getInt(4));
        a.setBigintCol(rs.getLong(5));
        a.setDecimalCol(rs.getLong(6));
        a.setDoubleCol(rs.getDouble(7));
        a.setRealCol(rs.getFloat(8));
        a.setTimeCol(rs.getTime(9));
        a.setDateCol(rs.getDate(10));
        a.setTimestampCol(rs.getTimestamp(11));
        a.setBinaryCol(rs.getBytes(12));
        a.setBlobCol(rs.getBytes(13));
        a.setOtherCol(rs.getObject(14));
        a.setUuidCol(rs.getBytes(15));
        a.setVarcharCol(rs.getString(16));
        a.setVarcharIgnorecaseCol(rs.getString(17));
        a.setCharCol(rs.getString(18));
        a.setClobCol(rs.getString(19));
        al.add(a);
      }
      stmt.close();
      mon.stop();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // read sort table javascript
  public static void createReport(String path, File reportPath) {
    List<String> sorttable = null;
    try (InputStream resource =
        Thread.currentThread().getContextClassLoader().getResourceAsStream(path + "sorttable.js")) {
      sorttable = new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8))
          .lines().collect(Collectors.toList());
    } catch (Exception e) {
      e.printStackTrace();
    }

    // create report html
    System.out.println("writing report to " + reportPath);
    String report = MonitorFactory.getReport();
    try (Writer w = new BufferedWriter(new FileWriter(reportPath))) {
      w.write("<html><head><style>*{font-family:tahoma; font-size:8pt} td{padding:3px}</style>");
      w.write("<script>\n" + String.join(System.lineSeparator(), sorttable) + "</script>");
      w.write("</head><body onload=\"initTable('report-table')\">");
      w.write(report.replace("<table", "<table id=\"report-table\""));
      w.write("</body></html>");
    } catch (IOException e) {
      e.printStackTrace();
    }
    MonitorFactory.reset();
  }

  private static AllTypes buildAllTypes() {
    AllTypes a = new AllTypes();
    byte[] binaryCol = new byte[255];
    for (int i = 0; i < 255; i++)
      binaryCol[i] = (byte) (i / 2);
    a.setBinaryCol(binaryCol);
    a.setBlobCol(new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
    Map<String, String> obj = new HashMap<>();
    obj.put("x", "y");
    a.setOtherCol(obj);
    a.setDateCol(new java.sql.Date(System.currentTimeMillis()));
    a.setTimeCol(new java.sql.Time(System.currentTimeMillis()));
    a.setTimestampCol(new java.sql.Timestamp(System.currentTimeMillis()));
    a.setCharCol("hello world char");
    a.setVarcharCol("hello world varchar");
    a.setVarcharIgnorecaseCol("hello world varchar ignore case");
    a.setClobCol("hello world clob");
    a.setIntCol(12345678);
    a.setBooleanCol(true);
    a.setTinyintCol(123);
    a.setSmallintCol(123);
    a.setBigintCol(Long.MAX_VALUE / 2);
    a.setDecimalCol(12345678);
    a.setDoubleCol(1234.5678);
    a.setRealCol(1234.56f);
    return a;
  }

  public static void waitInput() {
    try {
      System.out.println("Press ENTER key to call System.exit() in the console ... > ");
      System.in.read();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
    }
  }

  private String getPackageNameToPath() {
    return getPackageNameToPath(getClass());
  }

  public static String getPackageNameToPath(Class<?> clazz) {
    return clazz.getPackageName().replaceAll("\\.", "/") + "/";
  }

}
