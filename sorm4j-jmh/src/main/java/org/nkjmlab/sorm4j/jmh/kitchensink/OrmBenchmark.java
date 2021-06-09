/*
 * Copyright (c) 2014, Oracle America, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided with
 * the distribution.
 *
 * * Neither the name of Oracle nor the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.nkjmlab.sorm4j.jmh.kitchensink;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;
import org.nkjmlab.sorm4j.Sorm;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Warmup(iterations = 1)
@Measurement(iterations = 3)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.All)
@Threads(1)
@Fork(2)
@State(Scope.Thread)
public class OrmBenchmark {

  private static int numOfRows = 10240;
  @Param({"mem", "file"})
  private static String mode = "mem";
  private BenchmarkEnvironment environment;

  public static void main(String[] args) throws RunnerException {
    new Runner(new OptionsBuilder().include(OrmBenchmark2.class.getSimpleName())
        .result("tmp/" + "jmh-result-" + mode + "-" + System.currentTimeMillis() + ".csv")
        .resultFormat(ResultFormatType.CSV).build()).run();
  }

  @Setup
  public void setUp() {
    System.out.println(System.lineSeparator() + "### setup mode=" + mode + " " + numOfRows
        + " rows ##################");
    environment = new BenchmarkEnvironment(mode);
    environment.multiRowInsertJdbc();

  }



  @Benchmark
  public void multiRowInsertJdbi() {
    environment.multiRowInsertJdbi();
  }

  @Benchmark
  public void multiRowBatchInsertSql2o() {
    environment.multiRowSimpleBatchInsertSql2o();
  }

  @Benchmark
  public void multiRowBatchInsertJdbc() {
    environment.multiRowSimpleBatchInsertJdbc();
  }

  @Benchmark
  public void multiRowInsertJdbc() {
    environment.multiRowInsertJdbc();
  }

  @Benchmark
  public void multiRowInsertSorm() {
    environment.multiRowInsertSorm();
  }

  @Benchmark
  public void insertSingleDbUtils() {
    environment.insertSingleDbUtils();
  }

  @Benchmark
  public void insertSingleJdbc() {
    environment.insertSingleJdbc();
  }

  @Benchmark
  public void insertSingleJdbi() {
    environment.insertSingleJdbi();
  }


  @Benchmark
  public void insertSingleSorm() {
    environment.insertSingleSorm();
  }

  @Benchmark
  public void insertSingleSql2o() {
    environment.insertSingleSql2o();;
  }


  @Benchmark
  public void readSingleSorm4j() {
    environment.readSingleByPrimaryKeySorm();
  }


  @Benchmark
  public void readSingleHandecodedDbUtils() {
    environment.readSingleHandCodedDbUtils();
  }

  @Benchmark
  public void readSingleTypicalJdbc() {
    environment.readSingleTypicalJdbc();
  }

  @Benchmark
  public void readSingleTypicalJdbi() {
    environment.readSingleTypicalJdbi();
  }

  @Benchmark
  public void readSingleTypicalSql2o() {
    environment.readSingleTypicalSql2o();
  }


  @Benchmark
  public void readAllLazyStreamSorm() {
    environment.readAllLazyStreamSorm();
  }

  @Benchmark
  public void readAllMapListSorm() {
    environment.readAllMapListSorm();
  }

  @Benchmark
  public void readAllSorm() {
    environment.readAllSorm();
  }

  @Benchmark
  public void readAllTypicalJdbi() {
    environment.readAllTypicalJdbi();
  }

  @Benchmark
  public void readAllTypicalSql2o() {
    environment.readAllTypicalSql2o();
  }

  @Benchmark
  public void readAllJdbc() {
    environment.readAllJdbc();
  }

  private static class BenchmarkEnvironment {


    private final static List<String> colsWithoutId = List.of("INT_COL", "BOOLEAN_COL",
        "TINYINT_COL", "SMALLINT_COL", "BIGINT_COL", "DECIMAL_COL", "DOUBLE_COL", "REAL_COL",
        "TIME_COL", "DATE_COL", "TIMESTAMP_COL", "BINARY_COL", "BLOB_COL", "UUID_COL",
        "VARCHAR_COL", "VARCHAR_IGNORECASE_COL", "CHAR_COL", "CLOB_COL");

    private static final String CREATE_TABLE_SQL = "create table if not exists sample "
        + "(int_col int, boolean_col boolean, tinyint_col tinyint, smallint_col smallint, bigint_col bigint, decimal_col decimal, "
        + "double_col double, real_col real, time_col time, date_col date, timestamp_col timestamp, "
        + "binary_col binary, blob_col blob,  uuid_col uuid, varchar_col varchar, varchar_ignorecase_col varchar_ignorecase, "
        + "char_col char, clob_col clob, id int auto_increment primary key )";

    private static final String typicalSelectAllSql = "select * from sample";
    private static final String typicalSelectSql = typicalSelectAllSql + " where id=?";
    private static final String typicalNamedSelectSql = typicalSelectAllSql + " WHERE id = :id";

    // private static final String optimizedSelectAllSql =
    // "select " + String.join(",", colsWithoutId) + ", ID from sample";
    // private final String optimizedSelectSql = optimizedSelectAllSql + " where id=?";
    // private static final String optimizedNamedSelectSql = optimizedSelectAllSql + " WHERE id =
    // :id";

    private static final String placeHolders = "("
        + String.join(",",
            Stream.generate(() -> "?").limit(colsWithoutId.size()).collect(Collectors.toList()))
        + ")";

    private static final String insertSqlPrefix =
        "insert into sample(" + String.join(",", colsWithoutId) + ")values";

    private static final String insertSql = insertSqlPrefix + placeHolders;
    private static final String insertSqlWithNamedParameter = insertSqlPrefix
        + "(:intCol,:booleanCol,:tinyintCol,:smallintCol,:bigintCol,:decimalCol,:doubleCol,:realCol,"
        + ":timeCol,:dateCol,:timestampCol,:binaryCol,:blobCol,:uuidCol,:varcharCol,:varcharIgnorecaseCol,:charCol,:clobCol)";


    private static final int batchSize = 32;
    private static final int insertRows = 10240; // should be multiple of batch size

    private static final String insertMultiRowSql = insertSqlPrefix + String.join(",",
        Stream.generate(() -> placeHolders).limit(batchSize).collect(Collectors.toList()));



    private static final Map<String, String> urls = Map.of("mem",
        "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "file", "jdbc:h2:tcp:// localhost/~/db/orm");


    private static final Sample row = Sample.build();
    private static final Sample[] as =
        Stream.generate(() -> Sample.build()).limit(insertRows).toArray(Sample[]::new);
    private static final int ID = 1;


    private static DataSource createDataSourceH2(String url, String user, String password) {
      return org.h2.jdbcx.JdbcConnectionPool.create(url, user, password);
    }

    private static DataSource createDataSourceHikari(String url, String user, String password) {
      HikariConfig config = new HikariConfig();
      config.addDataSourceProperty("cachePrepStmts", "true");
      config.addDataSourceProperty("prepStmtCacheSize", "250");
      config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
      config.setJdbcUrl(url);
      config.setUsername(user);
      config.setPassword(password);
      return new HikariDataSource(config);
    }

    private static Sample getAllTpesFromResultSet(ResultSet rs) throws SQLException {
      Sample a = new Sample();
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
      a.setUuidCol(rs.getBytes(14));
      a.setVarcharCol(rs.getString(15));
      a.setVarcharIgnorecaseCol(rs.getString(16));
      a.setCharCol(rs.getString(17));
      a.setClobCol(rs.getString(18));
      return a;
    }

    public static void main(String[] args) {
      BenchmarkEnvironment environment = new BenchmarkEnvironment("mem");
      environment.multiRowSimpleBatchInsertSql2o();
      environment.readSingleHandCodedDbUtils();
      System.out.println(environment.readAllSormRet().size());
      environment = new BenchmarkEnvironment("mem");
      environment.multiRowInsertJdbi();
      environment.multiRowSimpleBatchInsertJdbc();
      System.out.println(environment.readAllSormRet().size());

      environment = new BenchmarkEnvironment("mem");
      environment.multiRowInsertJdbc();
      System.out.println(environment.readAllSormRet().size());

      environment = new BenchmarkEnvironment("mem");
      environment.multiRowInsertSorm();
      System.out.println(environment.readAllSormRet().size());

      environment.insertSingleJdbc();
      environment.insertSingleJdbi();
      environment.insertSingleSorm();
      environment.insertSingleSql2o();;
      environment.readSingleByPrimaryKeySorm();
      environment.readSingleTypicalJdbc();;
      environment.readSingleTypicalJdbi();
      environment.readSingleTypicalSql2o();
      environment.readAllLazyStreamSorm();
      environment.readAllMapListSorm();
      environment.readAllSorm();
      environment.readAllTypicalJdbi();
      environment.readAllTypicalSql2o();
      environment.readAllJdbc();

    }

    private static Sql2o prepareSql2o(DataSource ds) {
      Sql2o sql2o = new Sql2o(ds);
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
      mappings.put("UUID_COL", "uuidCol");
      mappings.put("VARCHAR_COL", "varcharCol");
      mappings.put("VARCHAR_IGNORECASE_COL", "varcharIgnorecaseCol");
      mappings.put("CHAR_COL", "charCol");
      mappings.put("CLOB_COL", "clobCol");
      mappings.put("ID", "id");
      sql2o.setDefaultColumnMappings(mappings);
      return sql2o;
    }

    private static void setParametersToStatement(PreparedStatement stmt, Sample a, int i)
        throws SQLException {
      final int cols = 18;
      stmt.setLong((cols * i) + 1, a.getIntCol());
      stmt.setBoolean((cols * i) + 2, a.getBooleanCol());
      stmt.setInt((cols * i) + 3, a.getTinyintCol());
      stmt.setInt((cols * i) + 4, a.getSmallintCol());
      stmt.setLong((cols * i) + 5, a.getBigintCol());
      stmt.setLong((cols * i) + 6, a.getDecimalCol());
      stmt.setDouble((cols * i) + 7, a.getDoubleCol());
      stmt.setFloat((cols * i) + 8, a.getRealCol());
      stmt.setTime((cols * i) + 9, a.getTimeCol());
      stmt.setDate((cols * i) + 10, a.getDateCol());
      stmt.setTimestamp((cols * i) + 11, a.getTimestampCol());
      stmt.setBytes((cols * i) + 12, a.getBinaryCol());
      stmt.setBytes((cols * i) + 13, a.getBlobCol());
      stmt.setBytes((cols * i) + 14, a.getUuidCol());
      stmt.setString((cols * i) + 15, a.getVarcharCol());
      stmt.setString((cols * i) + 16, a.getVarcharIgnorecaseCol());
      stmt.setString((cols * i) + 17, a.getCharCol());
      stmt.setString((cols * i) + 18, a.getClobCol());

    }

    private final DataSource dataSource;

    private Sql2o sql2o;


    private Jdbi jdbi;

    private Sorm sorm;

    private QueryRunner dbUtils;

    public BenchmarkEnvironment(String mode) {
      this.dataSource = createDataSourceHikari(urls.get(mode), "orm", "orm");
      this.sql2o = prepareSql2o(dataSource);
      this.jdbi = Jdbi.create(dataSource);
      this.sorm = Sorm.create(dataSource);
      this.dbUtils = prepareDbUtils(dataSource);

      this.dropAndCreateTable();
    }



    private QueryRunner prepareDbUtils(DataSource dataSource) {
      QueryRunner dbUtils = new QueryRunner(dataSource);
      return dbUtils;
    }

    private void dropAndCreateTable() {
      try (Connection connection = dataSource.getConnection()) {
        connection.createStatement().execute("drop table if exists sample");
        connection.createStatement().execute(CREATE_TABLE_SQL);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    public void insertSingleDbUtils() {
      try (Connection connection = dataSource.getConnection()) {
        int numRowsInserted = dbUtils.update(connection, insertSql, row.getIntCol(),
            row.getBooleanCol(), row.getTinyintCol(), row.getSmallintCol(), row.getBigintCol(),
            row.getDecimalCol(), row.getDoubleCol(), row.getRealCol(), row.getTimeCol(),
            row.getDateCol(), row.getTimestampCol(), row.getBinaryCol(), row.getBlobCol(),
            row.getUuidCol(), row.getVarcharCol(), row.getVarcharIgnorecaseCol(), row.getCharCol(),
            row.getClobCol());
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }


    public void insertSingleJdbc() {
      try (Connection connection = dataSource.getConnection()) {
        PreparedStatement stmt = connection.prepareStatement(insertSql);
        setParametersToStatement(stmt, row, 0);
        stmt.executeUpdate();
        stmt.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }



    public void insertSingleJdbi() {
      jdbi.useHandle(
          handler -> handler.createUpdate(insertSqlWithNamedParameter).bindBean(row).execute());
    }



    public void insertSingleSorm() {
      sorm.accept(con -> con.insert(row));
    }



    public void insertSingleSql2o() {
      try (org.sql2o.Connection conn = sql2o.open()) {
        conn.createQuery(insertSqlWithNamedParameter).bind(row).executeUpdate();
      }
    }

    public void multiRowSimpleBatchInsertJdbc() {
      try (Connection connection = dataSource.getConnection()) {
        PreparedStatement stmt = connection.prepareStatement(insertSql);
        for (Sample a : as) {
          setParametersToStatement(stmt, a, 0);
          stmt.addBatch();
        }
        stmt.executeBatch();
        stmt.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    public void multiRowSimpleBatchInsertSql2o() {
      try (org.sql2o.Connection conn = sql2o.open()) {
        Query query = conn.createQuery(insertSqlWithNamedParameter);
        for (Sample a : as) {
          query.bind(a).addToBatch();
        }
        query.executeBatch();
        query.close();
      }
    }

    public void multiRowInsertJdbi() {
      jdbi.useHandle(handle -> {
        PreparedBatch batch = handle.prepareBatch(insertSqlWithNamedParameter);
        for (Sample a : as) {
          batch.bindBean(a).add();
        }
        batch.execute();
      });
    }



    public void multiRowInsertJdbc() {
      int i = 0;
      try (Connection connection = dataSource.getConnection()) {
        PreparedStatement stmt = null;
        for (i = 0; i < as.length; i++) {
          if (i % batchSize == 0) {
            stmt = connection.prepareStatement(insertMultiRowSql);
          }
          setParametersToStatement(stmt, as[i], i % batchSize);
          if (i % batchSize == batchSize - 1) {
            stmt.executeUpdate();
          }
        }
        // When 10000 row and batch size=32, 32*312=9984 row.
        stmt.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }


    public void multiRowInsertSorm() {
      sorm.accept(con -> con.insert(as));
    }

    public void readAllLazyStreamSorm() {
      List<Sample> r =
          sorm.apply(conn -> conn.readAllLazy(Sample.class).stream().collect(Collectors.toList()));

    }

    public void readAllMapListSorm() {
      List<Map<String, Object>> r = sorm.apply(conn -> conn.readMapList(typicalSelectAllSql));
    }


    public void readAllSorm() {
      List<Sample> r = sorm.apply(conn -> conn.readAll(Sample.class));
    }

    public List<Sample> readAllSormRet() {
      return sorm.apply(conn -> conn.readAll(Sample.class));
    }

    public void readAllTypicalJdbi() {
      List<Sample> _a = jdbi.withHandle(
          handler -> handler.createQuery(typicalSelectAllSql).mapToBean(Sample.class).list());
    }

    public void readAllTypicalSql2o() {
      try (org.sql2o.Connection conn = sql2o.open()) {
        org.sql2o.Query query = conn.createQuery(typicalSelectAllSql);
        List<Sample> r = query.executeAndFetch(Sample.class);
      }
    }

    public void readAllJdbc() {
      List<Sample> al = new ArrayList<>();
      try (Connection connection = dataSource.getConnection()) {
        PreparedStatement stmt = connection.prepareStatement(typicalSelectAllSql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
          Sample a = getAllTpesFromResultSet(rs);
          al.add(a);
        }
        stmt.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    public void readSingleByPrimaryKeySorm() {
      Sample _a = sorm.apply(conn -> conn.readByPrimaryKey(Sample.class, ID));
    }

    public void readSingleMapByPrimaryKeySorm(String sql, int id) {
      Map<String, Object> _a = sorm.apply(conn -> conn.readMapFirst(typicalSelectSql, ID));
    }

    private final ResultSetHandler<Sample> resultSetHandler = new ResultSetHandler<>() {
      @Override
      public Sample handle(ResultSet rs) throws SQLException {
        return rs.next() ? getAllTpesFromResultSet(rs) : null;
      }
    };

    public void readSingleHandCodedDbUtils() {
      try (Connection connection = dataSource.getConnection()) {
        Sample _a = dbUtils.query(connection, typicalSelectSql, resultSetHandler, ID);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    public void readSingleTypicalJdbc() {
      try (Connection connection = dataSource.getConnection()) {
        PreparedStatement stmt = connection.prepareStatement(typicalSelectSql);
        stmt.setLong(1, ID);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        Sample _a = getAllTpesFromResultSet(rs);
        stmt.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }


    public void readSingleTypicalJdbi() {
      Sample _a = jdbi.withHandle(handler -> handler.createQuery(typicalSelectSql).bind(0, ID)
          .mapToBean(Sample.class).findFirst()).get();
    }

    public void readSingleTypicalSql2o() {
      try (org.sql2o.Connection conn = sql2o.open()) {
        org.sql2o.Query query = conn.createQuery(typicalNamedSelectSql);
        Sample _a = query.addParameter("id", ID).executeAndFetchFirst(Sample.class);
      }
    }

    public static class Sample {

      private long intCol;
      private boolean booleanCol;
      private int tinyintCol;
      private int smallintCol;
      private long bigintCol;
      private long decimalCol;
      private double doubleCol;
      private float realCol;

      public long getIntCol() {
        return intCol;
      }

      public void setIntCol(long intCol) {
        this.intCol = intCol;
      }

      public boolean getBooleanCol() {
        return booleanCol;
      }

      public void setBooleanCol(boolean booleanCol) {
        this.booleanCol = booleanCol;
      }

      public int getTinyintCol() {
        return tinyintCol;
      }

      public void setTinyintCol(int tinyintCol) {
        this.tinyintCol = tinyintCol;
      }

      public int getSmallintCol() {
        return smallintCol;
      }

      public void setSmallintCol(int smallintCol) {
        this.smallintCol = smallintCol;
      }

      public long getBigintCol() {
        return bigintCol;
      }

      public void setBigintCol(long bigintCol) {
        this.bigintCol = bigintCol;
      }

      public long getDecimalCol() {
        return decimalCol;
      }

      public void setDecimalCol(long decimalCol) {
        this.decimalCol = decimalCol;
      }

      public double getDoubleCol() {
        return doubleCol;
      }

      public void setDoubleCol(double doubleCol) {
        this.doubleCol = doubleCol;
      }

      public float getRealCol() {
        return realCol;
      }

      public void setRealCol(float realCol) {
        this.realCol = realCol;
      }

      private java.sql.Time timeCol;
      private java.sql.Date dateCol;
      private java.sql.Timestamp timestampCol;

      public java.sql.Time getTimeCol() {
        return timeCol;
      }

      public void setTimeCol(java.sql.Time timeCol) {
        this.timeCol = timeCol;
      }

      public java.sql.Date getDateCol() {
        return dateCol;
      }

      public void setDateCol(java.sql.Date dateCol) {
        this.dateCol = dateCol;
      }

      public java.sql.Timestamp getTimestampCol() {
        return timestampCol;
      }

      public void setTimestampCol(java.sql.Timestamp timestampCol) {
        this.timestampCol = timestampCol;
      }

      private byte[] binaryCol;
      private byte[] blobCol;
      private byte[] uuidCol;

      public byte[] getBinaryCol() {
        return binaryCol;
      }

      public void setBinaryCol(byte[] binaryCol) {
        this.binaryCol = binaryCol;
      }

      public byte[] getBlobCol() {
        return blobCol;
      }

      public void setBlobCol(byte[] blobCol) {
        this.blobCol = blobCol;
      }


      public byte[] getUuidCol() {
        return uuidCol;
      }

      public void setUuidCol(byte[] uuidCol) {
        this.uuidCol = uuidCol;
      }

      private String varcharCol;
      private String varcharIgnorecaseCol;
      private String charCol;
      private String clobCol;

      public String getVarcharCol() {
        return varcharCol;
      }

      public void setVarcharCol(String varcharCol) {
        this.varcharCol = varcharCol;
      }

      public String getVarcharIgnorecaseCol() {
        return varcharIgnorecaseCol;
      }

      public void setVarcharIgnorecaseCol(String varcharIgnorecaseCol) {
        this.varcharIgnorecaseCol = varcharIgnorecaseCol;
      }

      public String getCharCol() {
        return charCol;
      }

      public void setCharCol(String charCol) {
        this.charCol = charCol;
      }

      public String getClobCol() {
        return clobCol;
      }

      public void setClobCol(String clobCol) {
        this.clobCol = clobCol;
      }

      private long id;

      public long getId() {
        return id;
      }

      public void setId(long id) {
        this.id = id;
      }

      public static Sample build() {
        Sample a = new Sample();
        byte[] binaryCol = new byte[255];
        for (int i = 0; i < 255; i++)
          binaryCol[i] = (byte) (i / 2);
        a.setBinaryCol(binaryCol);
        a.setBlobCol(new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
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
    }
  }

}
