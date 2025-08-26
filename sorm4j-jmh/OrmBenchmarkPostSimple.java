package org.nkjmlab.sorm4j.jmh;

import static org.nkjmlab.sorm4j.jmh.OrmBenchmarkPostSimple.Post.batchSize;
import static org.nkjmlab.sorm4j.jmh.OrmBenchmarkPostSimple.Post.insertMultiRowSql;
import static org.nkjmlab.sorm4j.jmh.OrmBenchmarkPostSimple.Post.insertSql;
import static org.nkjmlab.sorm4j.jmh.OrmBenchmarkPostSimple.Post.insertSqlWithNamedParameter;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.h2.jdbcx.JdbcConnectionPool;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.nkjmlab.sorm4j.Sorm;
import org.nkjmlab.sorm4j.annotation.OrmConstructor;
import org.nkjmlab.sorm4j.internal.util.Try;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import org.sql2o.tools.FeatureDetector;

/**
 * This code is based on following code at first.
 * https://github.com/aaberg/sql2o/blob/master/core/src/test/java/org/sql2o/performance/PojoPerformanceTest.java
 */
@Warmup(iterations = 1)
@Measurement(iterations = 3)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Threads(1)
@Fork(3)
@State(Scope.Thread)
public class OrmBenchmarkPostSimple {

  static class ApacheDbUtilsBench implements BenchmarkBase {

    /**
     * This class handles mapping "first_name" column to "firstName" property. It looks worse than
     * it is, most is copied from {@link org.apache.commons.dbutils.BeanProcessor}.
     */
    private static class IgnoreUnderscoreBeanProcessor extends BeanProcessor {

      @Override
      protected int[] mapColumnsToProperties(ResultSetMetaData md, PropertyDescriptor[] props)
          throws SQLException {
        int cols = md.getColumnCount();
        int[] columnToProperty = new int[cols + 1];
        Arrays.fill(columnToProperty, PROPERTY_NOT_FOUND);

        for (int col = 1; col <= cols; col++) {
          String columnName = md.getColumnLabel(col);
          if (null == columnName || 0 == columnName.length()) {
            columnName = md.getColumnName(col);
          }
          // this is the addition from BeanProcessor
          String noUnderscoreColName = columnName.replace("_", "");

          for (int i = 0; i < props.length; i++) {
            if (noUnderscoreColName.equalsIgnoreCase(props[i].getName())) {
              columnToProperty[col] = i;
              break;
            }
          }
        }

        return columnToProperty;
      }
    }

    private static final QueryRunner runner = new QueryRunner();

    private static final ResultSetHandler<Post> rsHandler =
        new BeanHandler<Post>(
            Post.class, new BasicRowProcessor(new IgnoreUnderscoreBeanProcessor()));

    @Override
    public int insert(Object row) {
      throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public int[] insertMultiRow(Object... inputs) {
      throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public List<? extends Object> readAll(int input) {
      throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public Object readOneRow(int input) {
      try (Connection conn = dataSource.getConnection()) {
        return runner.query(conn, SELECT_TYPICAL_SQL + " WHERE id = ?", rsHandler, input);
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static interface BenchmarkBase {
    int insert(Object input);

    int[] insertMultiRow(Object... inputs);

    List<? extends Object> readAll(int input);

    Object readOneRow(int input);
  }

  static class HandCodedBench implements BenchmarkBase {

    private static void setParametersToStatement(PreparedStatement stmt, Object o, int i)
        throws SQLException {
      Post a = (Post) o;
      final int cols = 5;
      stmt.setString((cols * i) + 1, a.getText());
      stmt.setTimestamp((cols * i) + 2, a.getCreationDate());
      stmt.setTimestamp((cols * i) + 3, a.getLastChangeDate());
      stmt.setInt((cols * i) + 4, a.getCounter1() != null ? a.getCounter1() : -1);
      stmt.setDouble((cols * i) + 5, a.getCounter2() != null ? a.getCounter2() : -1);
    }

    @Override
    public int insert(Object input) {
      try (Connection connection = dataSource.getConnection();
          PreparedStatement stmt = connection.prepareStatement(insertSql)) {
        setParametersToStatement(stmt, input, 0);
        int ret = stmt.executeUpdate();
        stmt.close();
        return ret;
      } catch (SQLException e) {
        throw new RuntimeException("error when executing query", e);
      }
    }

    @Override
    public int[] insertMultiRow(Object... inputs) {
      int i = 0;
      int[] ret = new int[inputs.length / batchSize + 1];
      try (Connection connection = dataSource.getConnection()) {
        PreparedStatement stmt = null;
        for (i = 0; i < inputs.length; i++) {
          if (i % batchSize == 0) {
            stmt = connection.prepareStatement(insertMultiRowSql);
          }
          setParametersToStatement(stmt, inputs[i], i % batchSize);
          if (i % batchSize == batchSize - 1) {
            ret[i / batchSize] = stmt.executeUpdate();
          }
        }
        stmt.close();
        return ret;
      } catch (SQLException e) {
        throw Try.rethrow(e);
      }
    }

    @Override
    public List<? extends Object> readAll(int input) {
      try (Connection conn = dataSource.getConnection();
          PreparedStatement stmt = conn.prepareStatement(SELECT_TYPICAL_SQL)) {
        try (ResultSet rs = stmt.executeQuery()) {
          List<Post> ret = new ArrayList<>();
          while (rs.next()) {
            // Using default constructor and setters is faster than using constructor with
            // parameter.
            Post p = new Post();
            p.setId(rs.getInt("id"));
            p.setText(rs.getString("text"));
            p.setCreationDate(rs.getTimestamp("creation_date"));
            p.setLastChangeDate(rs.getTimestamp("last_change_date"));
            p.setCounter1(getNullableInt(rs, "counter1"));
            p.setCounter2(getNullableDouble(rs, "counter2"));
            ret.add(p);
          }
          return ret;
        }
      } catch (SQLException e) {
        throw new RuntimeException("error when executing query", e);
      }
    }

    @Override
    public Object readOneRow(int input) {
      try (Connection conn = dataSource.getConnection();
          PreparedStatement stmt = conn.prepareStatement(SELECT_TYPICAL_SQL + " WHERE id = ?")) {
        stmt.setInt(1, input);
        try (ResultSet rs = stmt.executeQuery()) {
          rs.next();
          // Post p = new Post(rs.getInt("id"), getNullableInt(rs, "counter1"),
          // getNullableDouble(rs, "counter2"), rs.getTimestamp("creation_date"),
          // rs.getTimestamp("last_change_date"), rs.getString("text"));

          // Using default constructor and setters is faster than using constructor with parameter.
          Post p = new Post();
          p.setId(rs.getInt("id"));
          p.setText(rs.getString("text"));
          p.setCreationDate(rs.getTimestamp("creation_date"));
          p.setLastChangeDate(rs.getTimestamp("last_change_date"));
          p.setCounter1(getNullableInt(rs, "counter1"));
          p.setCounter2(getNullableDouble(rs, "counter2"));
          return p;
        }
      } catch (SQLException e) {
        throw new RuntimeException("error when executing query", e);
      }
    }
  }

  static class JdbiBench implements BenchmarkBase {

    private static final Jdbi jdbi = Jdbi.create(dataSource);

    @Override
    public int insert(Object input) {
      return jdbi.withHandle(
          handler -> handler.createUpdate(insertSqlWithNamedParameter).bindBean(input).execute());
    }

    @Override
    public int[] insertMultiRow(Object... inputs) {
      return jdbi.withHandle(
          handle -> {
            PreparedBatch batch = handle.prepareBatch(insertSqlWithNamedParameter);
            for (Object o : inputs) {
              Post a = (Post) o;
              batch.bindBean(a).add();
            }
            return batch.execute();
          });
    }

    @Override
    public List<? extends Object> readAll(int input) {
      return jdbi.withHandle(
          handler -> handler.createQuery(SELECT_TYPICAL_SQL).mapToBean(Post.class).list());
    }

    @Override
    public Object readOneRow(int input) {
      return jdbi.withHandle(
              handler ->
                  handler
                      .createQuery(SELECT_TYPICAL_SQL + " WHERE id=?")
                      .bind(0, input)
                      .mapToBean(Post.class)
                      .findFirst())
          .get();
    }
  }

  static class JooqBench implements BenchmarkBase {

    @Override
    public int insert(Object input) {
      throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public int[] insertMultiRow(Object... inputs) {
      throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public List<? extends Object> readAll(int input) {
      try (Connection conn = dataSource.getConnection()) {
        DSLContext context = DSL.using(conn, SQLDialect.H2);
        return context.select().from("post").fetch().into(Post.class);
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public Object readOneRow(int input) {
      try (Connection conn = dataSource.getConnection()) {
        DSLContext context = DSL.using(conn, SQLDialect.H2);
        return context.select().from("post").where("id = ?", input).fetchOne().into(Post.class);
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
  }

  /** It appears executing raw SQL is not possible with MyBatis. */
  static class MyBatisBench implements BenchmarkBase {
    private interface MyBatisPostAllMapper {
      @Select(SELECT_TYPICAL_SQL)
      @Results({
        @Result(property = "creationDate", column = "creation_date"),
        @Result(property = "lastChangeDate", column = "last_change_date")
      })
      List<Post> selectPost(int id);
    }

    private interface MyBatisPostPrimaryKeyMapper {
      @Select(SELECT_TYPICAL_SQL + " WHERE id = #{id}")
      @Results({
        @Result(property = "creationDate", column = "creation_date"),
        @Result(property = "lastChangeDate", column = "last_change_date")
      })
      Post selectPost(int id);
    }

    private static final SqlSessionFactory sqlSessionFactory;

    static {
      TransactionFactory transactionFactory = new JdbcTransactionFactory();
      Environment environment = new Environment("development", transactionFactory, dataSource);
      org.apache.ibatis.session.Configuration config =
          new org.apache.ibatis.session.Configuration(environment);
      config.addMapper(MyBatisPostAllMapper.class);
      config.addMapper(MyBatisPostPrimaryKeyMapper.class);
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(config);
    }

    @Override
    public int insert(Object input) {
      throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public int[] insertMultiRow(Object... inputs) {
      throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public List<? extends Object> readAll(int input) {
      try (SqlSession session = sqlSessionFactory.openSession()) {
        return session.getMapper(MyBatisPostAllMapper.class).selectPost(input);
      }
    }

    @Override
    public Object readOneRow(int input) {
      try (SqlSession session = sqlSessionFactory.openSession()) {
        return session.getMapper(MyBatisPostPrimaryKeyMapper.class).selectPost(input);
      }
    }
  }

  public static record PostRecord(
      int id,
      Integer counter1,
      Double counter2,
      Timestamp creationDate,
      Timestamp lastChangeDate,
      String text) {
    public static final String CREATE_TABLE_IF_NOT_EXISTS =
        "CREATE TABLE IF NOT EXISTS post_record ( "
            + "id INT AUTO_INCREMENT PRIMARY KEY , text VARCHAR(255) , creation_date TIMESTAMP, last_change_date TIMESTAMP , "
            + "counter1 INT , counter2 DOUBLE);";

    public static PostRecord createRandom(int seed) {
      ThreadLocalRandom r = ThreadLocalRandom.current();
      PostRecord p =
          new PostRecord(
              -1,
              r.nextDouble() < 0.99 ? r.nextInt() : null,
              r.nextDouble() < 0.99 ? r.nextDouble() : null,
              Timestamp.valueOf(LocalDateTime.now()),
              Timestamp.valueOf(LocalDateTime.now()),
              "a name " + seed);
      return p;
    }
  }

  public static class Post {
    public static final String CREATE_TABLE_IF_NOT_EXISTS =
        "CREATE TABLE IF NOT EXISTS post ( "
            + "id INT AUTO_INCREMENT PRIMARY KEY , text VARCHAR(255) , creation_date TIMESTAMP, last_change_date TIMESTAMP , "
            + "counter1 INT , counter2 DOUBLE);";

    private static final List<String> colsWithoutId =
        List.of("text", "creation_date", "last_change_date", "counter1", "counter2");

    private static final String placeHolders =
        "("
            + String.join(
                ",",
                Stream.generate(() -> "?").limit(colsWithoutId.size()).collect(Collectors.toList()))
            + ")";

    private static final String insertSqlPrefix =
        "insert into post (" + String.join(",", colsWithoutId) + ") values ";

    public static final String insertSql = insertSqlPrefix + placeHolders;
    public static final String insertSqlWithNamedParameter =
        insertSqlPrefix + " (:text,:creationDate,:lastChangeDate,:counter1,:counter2)";

    public static final int batchSize = 32;

    public static final String insertMultiRowSql =
        insertSqlPrefix
            + String.join(
                ",",
                Stream.generate(() -> placeHolders).limit(batchSize).collect(Collectors.toList()));

    private static final Post[] posts =
        Stream.generate(() -> createRandom(randomIdGenerator()))
            .limit(NUM_OF_ROWS)
            .toArray(Post[]::new);

    private static final Post post = createPost();

    private static Post createPost() {
      Post p = new Post();
      p.text = "a name test";
      p.creationDate = Timestamp.valueOf(LocalDateTime.now());
      p.lastChangeDate = Timestamp.valueOf(LocalDateTime.now());
      p.counter1 = 1;
      p.counter2 = 999.0;
      return p;
    }

    public static Post createRandom(int seed) {
      ThreadLocalRandom r = ThreadLocalRandom.current();
      Post p = new Post();
      p.text = "a name " + seed;
      p.creationDate = Timestamp.valueOf(LocalDateTime.now());
      p.lastChangeDate = Timestamp.valueOf(LocalDateTime.now());
      p.counter1 = r.nextDouble() < 0.99 ? r.nextInt() : null;
      p.counter2 = r.nextDouble() < 0.99 ? r.nextDouble() : null;
      return p;
    }

    private int id;
    private Integer counter1;
    private Double counter2;
    private Timestamp creationDate;
    private Timestamp lastChangeDate;

    private String text;

    public Post() {}

    @OrmConstructor({"id", "counter1", "counter2", "creationDate", "lastChangeDate", "text"})
    public Post(
        int id,
        Integer counter1,
        Double counter2,
        Timestamp creationDate,
        Timestamp lastChangeDate,
        String text) {
      this.id = id;
      this.counter1 = counter1;
      this.counter2 = counter2;
      this.creationDate = creationDate;
      this.lastChangeDate = lastChangeDate;
      this.text = text;
    }

    public Integer getCounter1() {
      return counter1;
    }

    public Double getCounter2() {
      return counter2;
    }

    public Timestamp getCreationDate() {
      return creationDate;
    }

    public int getId() {
      return id;
    }

    public Timestamp getLastChangeDate() {
      return lastChangeDate;
    }

    public String getText() {
      return text;
    }

    public void setCounter1(Integer counter1) {
      this.counter1 = counter1;
    }

    public void setCounter2(Double counter2) {
      this.counter2 = counter2;
    }

    public void setCreationDate(Timestamp creationDate) {
      this.creationDate = creationDate;
    }

    public void setId(int id) {
      this.id = id;
    }

    public void setLastChangeDate(Timestamp lastChangeDate) {
      this.lastChangeDate = lastChangeDate;
    }

    public void setText(String text) {
      this.text = text;
    }
  }

  static class SormBench implements BenchmarkBase {
    private static final Sorm sorm = Sorm.create(dataSource);

    @Override
    public int insert(Object input) {
      return sorm.applyHandler(conn -> conn.insert(input));
    }

    @Override
    public int[] insertMultiRow(Object... inputs) {
      return sorm.applyHandler(conn -> conn.insert(inputs));
    }

    @Override
    public List<? extends Object> readAll(int input) {
      return sorm.applyHandler(conn -> conn.readList(PostRecord.class, SELECT_TYPICAL_SQL));
    }

    @Override
    public Object readOneRow(int input) {
      //      return sorm.applyHandler(
      //          conn -> conn.readFirst(Post.class, SELECT_TYPICAL_SQL + " WHERE id=?", input));
      return sorm.applyHandler(conn -> conn.selectByPrimaryKey(PostRecord.class, input));
    }
  }

  static class SpringJdbcTemplateBench implements BenchmarkBase {
    private static final NamedParameterJdbcTemplate jdbcTemplate =
        new NamedParameterJdbcTemplate(dataSource);

    @Override
    public int insert(Object input) {
      throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public int[] insertMultiRow(Object... inputs) {
      throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public List<? extends Object> readAll(int input) {
      throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public Object readOneRow(int input) {
      return jdbcTemplate.queryForObject(
          SELECT_TYPICAL_SQL + " WHERE id = :id",
          Collections.singletonMap("id", input),
          new BeanPropertyRowMapper<Post>(Post.class));
    }
  }

  static class Sql2oBench implements BenchmarkBase {
    private static Sql2o sql2o = new Sql2o(dataSource);

    static {
      setOracleAvailable(false);
      // sql2o.setDefaultColumnMappings(
      // Map.of("creation_date", "creationDate", "last_change_date", "lastChangeDate"));
    }

    private static void setOracleAvailable(boolean b) {
      try {
        Field f = FeatureDetector.class.getDeclaredField("oracleAvailable");
        f.setAccessible(true);
        f.set(null, b);
      } catch (NoSuchFieldException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }

    @Override
    public int insert(Object input) {
      try (org.sql2o.Connection conn = sql2o.open()) {
        return conn.createQuery(insertSqlWithNamedParameter)
            .bind(input)
            .executeUpdate()
            .getResult();
      }
    }

    @Override
    public int[] insertMultiRow(Object... inputs) {
      try (org.sql2o.Connection conn = sql2o.open()) {
        Query query = conn.createQuery(insertSqlWithNamedParameter);
        for (Object o : inputs) {
          Post a = (Post) o;
          query.bind(a).addToBatch();
        }
        int[] ret = query.executeBatch().getBatchResult();
        query.close();
        return ret;
      }
    }

    @Override
    public List<? extends Object> readAll(int input) {
      try (org.sql2o.Connection conn = sql2o.open()) {
        Query query = conn.createQuery(SELECT_TYPICAL_SQL).setAutoDeriveColumnNames(true);
        return query.executeAndFetch(Post.class);
      }
    }

    @Override
    public Object readOneRow(int input) {
      try (org.sql2o.Connection conn = sql2o.open()) {
        Query query =
            conn.createQuery(SELECT_TYPICAL_SQL + " WHERE id = :id").setAutoDeriveColumnNames(true);
        return query.addParameter("id", input).executeAndFetchFirst(Post.class);
      }
    }
  }

  private static final String DB_PASSWORD = "";

  private static final String DB_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
  private static final String DB_USER = "sa";

  private static final javax.sql.DataSource dataSource =
      JdbcConnectionPool.create(DB_URL, DB_USER, DB_PASSWORD);
  private static final HandCodedBench handCodedBench = new HandCodedBench();

  private static final ApacheDbUtilsBench apacheDbUtilsBench = new ApacheDbUtilsBench();

  private static final JdbiBench jdbiBench = new JdbiBench();

  private static final JooqBench jooqBench = new JooqBench();

  private static final MyBatisBench myBatisBench = new MyBatisBench();
  private static final SormBench sormBench = new SormBench();

  private static final SpringJdbcTemplateBench springJdbcTemplateBench =
      new SpringJdbcTemplateBench();
  private static final Sql2oBench sql2oBench = new Sql2oBench();

  private static final List<BenchmarkBase> ALL_BENCHS =
      List.of(
          handCodedBench,
          apacheDbUtilsBench,
          jdbiBench,
          jooqBench,
          myBatisBench,
          sormBench,
          springJdbcTemplateBench,
          sql2oBench,
          springJdbcTemplateBench);

  // "select * " is faster than "select col1, col2, ..., coln" in H2 2.1.210. the former is also
  // faster than "select tablname.col1, tablname.col2, ..., tablname.coln".
  static final String SELECT_TYPICAL_SQL = "SELECT * FROM post";

  static final int NUM_OF_ROWS = 10240;

  private static final Double getNullableDouble(ResultSet rs, String colName) throws SQLException {
    Object obj = rs.getObject(colName);
    return obj == null ? null : (Double) obj;
  }

  private static final Integer getNullableInt(ResultSet rs, String colName) throws SQLException {
    Object obj = rs.getObject(colName);
    return obj == null ? null : (Integer) obj;
  }

  public static void main(String[] args) {
    OrmBenchmarkPostSimple b = new OrmBenchmarkPostSimple();
    b.setup();

    List<BenchmarkBase> benchs = List.of(sormBench);
    b.runReadOne(benchs);
    System.out.println(">>>");
    new Scanner(System.in).nextLine();
    b.runReadOne(benchs);
    b.runReadAll(benchs);
    //    b.runInsertOne(benchs);
    //    b.runInsertAll(benchs);
    //    b.runAll(benchs);
  }

  private void runReadOne(List<BenchmarkBase> benchs) {
    benchs.forEach(
        bench -> {
          try {
            IntStream.range(0, 10)
                .forEach(i -> bench.readOneRow(OrmBenchmarkPostSimple.randomIdGenerator()));
          } catch (UnsupportedOperationException e) {
            System.err.println(e.getMessage());
          } catch (Exception e) {
            e.printStackTrace();
          }
        });
  }

  private void runReadAll(List<BenchmarkBase> benchs) {
    benchs.forEach(
        bench -> {
          try {
            IntStream.range(0, 2).forEach(i -> bench.readAll(randomIdGenerator()));
          } catch (UnsupportedOperationException e) {
            System.err.println(e.getMessage());
          } catch (Exception e) {
            e.printStackTrace();
          }
        });
  }

  private void runInsertOne(List<BenchmarkBase> benchs) {

    benchs.forEach(
        bench -> {
          try {
            IntStream.range(0, 10).forEach(i -> bench.insert(Post.post));
          } catch (UnsupportedOperationException e) {
            System.err.println(e.getMessage());
          } catch (Exception e) {
            e.printStackTrace();
          }
        });
  }

  private void runInsertAll(List<BenchmarkBase> benchs) {
    benchs.forEach(
        bench -> {
          try {
            IntStream.range(0, 2).forEach(i -> bench.insertMultiRow(Post.posts));
          } catch (UnsupportedOperationException e) {
            System.err.println(e.getMessage());
          } catch (Exception e) {
            e.printStackTrace();
          }
        });
  }

  private void runAll(List<BenchmarkBase> benchs) {
    runReadOne(benchs);
    runReadAll(benchs);
    runInsertOne(benchs);
    runInsertAll(benchs);
  }

  /**
   * Note: id is auto-incremented between 1 to NUM_OF_ROWS;
   *
   * @return
   */
  static final int randomIdGenerator() {
    return ThreadLocalRandom.current().nextInt(1, OrmBenchmarkPostSimple.NUM_OF_ROWS + 1);
  }

  public Object apacheDbUtilsReadOne() {
    return apacheDbUtilsBench.readOneRow(OrmBenchmarkPostSimple.randomIdGenerator());
  }

  @Benchmark
  public Object handCodedInsertOne() {
    return handCodedBench.insert(Post.post);
  }

  @Benchmark
  public int[] handCodedInsertMultiRow() {
    return handCodedBench.insertMultiRow(Post.posts);
  }

  @Benchmark
  public Object handCodedReadAll() {
    return handCodedBench.readAll(OrmBenchmarkPostSimple.randomIdGenerator());
  }

  @Benchmark
  public Object handCodedReadOne() {
    return handCodedBench.readOneRow(OrmBenchmarkPostSimple.randomIdGenerator());
  }

  @Benchmark
  public Object jDBIInsertOne() {
    return jdbiBench.insert(Post.post);
  }

  @Benchmark
  public int[] jDBIInsertMultiRow() {
    return jdbiBench.insertMultiRow(Post.posts);
  }

  @Benchmark
  public Object jDBIReadAll() {
    return jdbiBench.readAll(OrmBenchmarkPostSimple.randomIdGenerator());
  }

  @Benchmark
  public Object jDBIReadOne() {
    return jdbiBench.readOneRow(OrmBenchmarkPostSimple.randomIdGenerator());
  }

  @Benchmark
  public Object jOOQInsertOne() {
    return jooqBench.insert(Post.post);
  }

  @Benchmark
  public int[] jOOQInsertMultiRow() {
    return jooqBench.insertMultiRow(Post.posts);
  }

  @Benchmark
  public Object jOOQReadAll() {
    return jooqBench.readAll(OrmBenchmarkPostSimple.randomIdGenerator());
  }

  @Benchmark
  public Object jOOQReadOne() {
    return jooqBench.readOneRow(OrmBenchmarkPostSimple.randomIdGenerator());
  }

  @Benchmark
  public Object myBatisInsertOne() {
    return myBatisBench.insert(Post.post);
  }

  @Benchmark
  public Object myBatisInsertMultiRow() {
    return myBatisBench.insertMultiRow(Post.posts);
  }

  @Benchmark
  public Object myBatisReadAll() {
    return myBatisBench.readAll(OrmBenchmarkPostSimple.randomIdGenerator());
  }

  @Benchmark
  public Object myBatisReadOne() {
    return myBatisBench.readOneRow(OrmBenchmarkPostSimple.randomIdGenerator());
  }

  @Setup
  public void setup() {
    System.out.println(System.lineSeparator() + "### setup ##################");
    Sorm sorm = Sorm.create(dataSource);
    sorm.acceptHandler(
        conn -> {
          conn.executeUpdate(Post.CREATE_TABLE_IF_NOT_EXISTS);
          conn.insert(
              IntStream.range(0, OrmBenchmarkPostSimple.NUM_OF_ROWS)
                  .mapToObj(i -> Post.createRandom(i))
                  .toArray(Post[]::new));
        });
    sorm.acceptHandler(
        conn -> {
          conn.executeUpdate(PostRecord.CREATE_TABLE_IF_NOT_EXISTS);
          conn.insert(
              IntStream.range(0, OrmBenchmarkPostSimple.NUM_OF_ROWS)
                  .mapToObj(i -> PostRecord.createRandom(i))
                  .toArray(PostRecord[]::new));
        });
  }

  @Benchmark
  public Object sormInsertOne() {
    return sormBench.insert(Post.post);
  }

  @Benchmark
  public int[] sormInsertMultiRow() {
    return sormBench.insertMultiRow(Post.posts);
  }

  @Benchmark
  public Object sormReadAll() {
    return sormBench.readAll(OrmBenchmarkPostSimple.randomIdGenerator());
  }

  @Benchmark
  public Object sormReadOne() {
    return sormBench.readOneRow(OrmBenchmarkPostSimple.randomIdGenerator());
  }

  @Benchmark
  public Object springJdbcTemplateReadOne() {
    return springJdbcTemplateBench.readOneRow(OrmBenchmarkPostSimple.randomIdGenerator());
  }

  @Benchmark
  public Object sql2oInsertOne() {
    return sql2oBench.insert(Post.post);
  }

  @Benchmark
  public int[] sql2oInsertMultiRow() {
    return sql2oBench.insertMultiRow(Post.posts);
  }

  @Benchmark
  public Object sql2oReadAll() {
    return sql2oBench.readAll(OrmBenchmarkPostSimple.randomIdGenerator());
  }

  @Benchmark
  public Object sql2oReadOne() {
    return sql2oBench.readOneRow(OrmBenchmarkPostSimple.randomIdGenerator());
  }
}
