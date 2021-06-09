package org.nkjmlab.sorm4j.jmh.kitchensink;

import static org.nkjmlab.sorm4j.jmh.kitchensink.OrmBenchmark5.Post.*;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
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
import org.joda.time.DateTime;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.nkjmlab.sorm4j.Sorm;
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
import org.sql2o.Query;
import org.sql2o.Sql2o;
import org.sql2o.tools.FeatureDetector;

/**
 *
 * This code is based on following code at first.
 * https://github.com/aaberg/sql2o/blob/master/core/src/test/java/org/sql2o/performance/PojoPerformanceTest.java
 *
 * @author nkjm
 *
 */
@Warmup(iterations = 1)
@Measurement(iterations = 1)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Threads(1)
@Fork(1)
@State(Scope.Thread)
public class OrmBenchmark5 {
  private final static String DB_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
  private final static String DB_USER = "sa";
  private final static String DB_PASSWORD = "";
  private static final javax.sql.DataSource dataSource =
      JdbcConnectionPool.create(DB_URL, DB_USER, DB_PASSWORD);

  private static final HandCodedSelect handCodedSelect = new HandCodedSelect();
  private static final Sql2oSelect sql2oSelect = new Sql2oSelect();
  private static final JdbiSelect jdbiSelect = new JdbiSelect();
  private static final JooqSelect jooqSelect = new JooqSelect();
  private static final MyBatisSelect myBatisSelect = new MyBatisSelect();
  private static final SormSelect sormSelect = new SormSelect();

  private static final int NUM_OF_ROWS = 10240;

  public static void main(String[] args) {

    OrmBenchmark5 b = new OrmBenchmark5();
    b.setup();
    b.handCodedSelect();
    b.sql2oTypicalSelect();
    b.jDBISelect();
    b.jOOQSelect();
    b.myBatisSelect();
    b.sormSelect();
  }

  @Setup
  public void setup() {
    System.out.println(System.lineSeparator() + "### setup ##################");
    Sorm sorm = Sorm.create(dataSource);
    sorm.accept(conn -> {
      conn.executeUpdate(Post.DROP_AND_CREATE_TABLE);
      conn.insert(
          IntStream.range(0, NUM_OF_ROWS).mapToObj(i -> Post.createRandom(i)).toArray(Post[]::new));
    });
  }

  @Benchmark
  public void handCodedSelect() {
    handCodedSelect.run(idGenerator());
  }

  @Benchmark
  public void sql2oTypicalSelect() {
    sql2oSelect.run(idGenerator());
  }


  @Benchmark
  public void jDBISelect() {
    jdbiSelect.run(idGenerator());
  }

  @Benchmark
  public void jOOQSelect() {
    jooqSelect.run(idGenerator());
  }

  @Benchmark
  public void myBatisSelect() {
    myBatisSelect.run(idGenerator());
  }

  @Benchmark
  public void sormSelect() {
    sormSelect.run(idGenerator());
  }

  /**
   * Note: id is auto-incremented between 1 to NUM_OF_ROWS;
   *
   * @return
   */
  private static final int idGenerator() {
    return ThreadLocalRandom.current().nextInt(1, NUM_OF_ROWS);
  }

  static class Sql2oSelect implements BenchmarkBase {
    private static Sql2o sql2o = new Sql2o(dataSource);
    static {
      // turn off oracle because ResultSetUtils slows down with oracle
      setOracleAvailable(false);
    }

    @Override
    public void run(int input) {
      try (org.sql2o.Connection conn = sql2o.open()) {
        Query query = conn.createQuery(SELECT_TYPICAL_SQL).setAutoDeriveColumnNames(true);
        List<Post> ret = query.executeAndFetch(Post.class);
      }
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

  }

  /**
   * It appears JDBI does not support mapping underscore to camel case.
   */
  static class JdbiSelect implements BenchmarkBase {

    private static final Jdbi jdbi = Jdbi.create(dataSource);


    @Override
    public void run(int input) {
      List<Post> ret = jdbi.withHandle(
          handler -> handler.createQuery(SELECT_TYPICAL_SQL).mapToBean(Post.class).list());

    }

  }

  /**
   * TODO can this be optimized?
   */
  static class JooqSelect implements BenchmarkBase {


    @Override
    public void run(int input) {
      try (Connection conn = dataSource.getConnection()) {
        DSLContext context = DSL.using(conn, SQLDialect.H2);
        List<Post> ret = context.select().from("post").fetch().into(Post.class);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

  }

  private static final Integer getNullableInt(ResultSet rs, String colName) throws SQLException {
    Object obj = rs.getObject(colName);
    return obj == null ? null : (Integer) obj;
  }

  private static final Double getNullableDouble(ResultSet rs, String colName) throws SQLException {
    Object obj = rs.getObject(colName);
    return obj == null ? null : (Double) obj;
  }


  static class HandCodedSelect implements BenchmarkBase {

    @Override
    public void run(int input) {
      try (Connection conn = dataSource.getConnection();
          PreparedStatement stmt = conn.prepareStatement(SELECT_TYPICAL_SQL)) {
        try (ResultSet rs = stmt.executeQuery()) {
          List<Post> ret = new ArrayList<>();
          while (rs.next()) {
            Post p = new Post();
            p.setId(rs.getInt("id"));
            p.setText(rs.getString("text"));
            p.setCreationDate(rs.getDate("creation_date"));
            p.setLastChangeDate(rs.getDate("last_change_date"));
            p.setCounter1(getNullableInt(rs, "counter1"));
            p.setCounter2(getNullableDouble(rs, "counter2"));
            ret.add(p);
          }
        }
      } catch (SQLException e) {
        throw new RuntimeException("error when executing query", e);
      }
    }

  }



  /**
   * It appears executing raw SQL is not possible with MyBatis. Therefore "typical" = "optimized",
   * there is no difference.
   */
  static class MyBatisSelect implements BenchmarkBase {
    private static final SqlSessionFactory sqlSessionFactory;
    static {
      TransactionFactory transactionFactory = new JdbcTransactionFactory();
      Environment environment = new Environment("development", transactionFactory, dataSource);
      org.apache.ibatis.session.Configuration config =
          new org.apache.ibatis.session.Configuration(environment);
      config.addMapper(MyBatisPostMapper.class);
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(config);
    }


    @Override
    public void run(int input) {
      try (SqlSession session = sqlSessionFactory.openSession()) {
        List<Post> ret = session.getMapper(MyBatisPostMapper.class).selectPost(input);
      }
    }

    private interface MyBatisPostMapper {
      @Select(SELECT_TYPICAL_SQL)
      @Results({@Result(property = "creationDate", column = "creation_date"),
          @Result(property = "lastChangeDate", column = "last_change_date")})
      List<Post> selectPost(int id);
    }

  }


  static class SormSelect implements BenchmarkBase {
    private static final Sorm sorm = Sorm.create(dataSource);


    @Override
    public void run(int input) {
      List<Post> ret = sorm.apply(conn -> conn.readList(Post.class, Post.SELECT_TYPICAL_SQL));
    }

  }

  public static interface BenchmarkBase {
    void run(int input);

  }

  public static class Post {
    public static final String DROP_AND_CREATE_TABLE = "DROP TABLE IF EXISTS post;"
        + "CREATE TABLE post ( "
        + "id INT NOT NULL IDENTITY PRIMARY KEY , text VARCHAR(255) , creation_date DATETIME , last_change_date DATETIME , "
        + "counter1 INT , counter2 DOUBLE);";
    public static final String SELECT_TYPICAL_SQL = "SELECT * FROM post";
    public static final String SELECT_OPTIMAL_SQL =
        "SELECT id, text, creation_date as creationDate, last_change_date as lastChangeDate, counter1, counter2 FROM post";

    public int id;
    private String text;
    private Date creationDate;
    private Date lastChangeDate;
    private Integer counter1;
    private Double counter2;

    public int getId() {
      return id;
    }

    public static Post createRandom(int idx) {
      ThreadLocalRandom r = ThreadLocalRandom.current();
      Post p = new Post();
      p.text = "a name " + idx;
      p.creationDate = new DateTime(System.currentTimeMillis() + r.nextInt()).toDate();
      p.lastChangeDate = new DateTime(System.currentTimeMillis() + r.nextInt()).toDate();
      p.counter1 = r.nextDouble() > 0.5 ? r.nextInt() : null;
      p.counter2 = r.nextDouble() > 0.5 ? r.nextDouble() : null;
      return p;
    }

    public void setId(int id) {
      this.id = id;
    }

    public String getText() {
      return text;
    }

    public void setText(String text) {
      this.text = text;
    }

    public Date getCreationDate() {
      return creationDate;
    }

    public void setCreationDate(Date creationDate) {
      this.creationDate = creationDate;
    }

    public Date getLastChangeDate() {
      return lastChangeDate;
    }

    public void setLastChangeDate(Date lastChangeDate) {
      this.lastChangeDate = lastChangeDate;
    }

    public Integer getCounter1() {
      return counter1;
    }

    public void setCounter1(Integer counter1) {
      this.counter1 = counter1;
    }

    public Double getCounter2() {
      return counter2;
    }

    public void setCounter2(Double counter2) {
      this.counter2 = counter2;
    }
  }
}
